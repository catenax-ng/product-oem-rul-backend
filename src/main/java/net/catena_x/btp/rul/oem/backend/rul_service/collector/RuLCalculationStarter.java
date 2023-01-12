package net.catena_x.btp.rul.oem.backend.rul_service.collector;

import com.fasterxml.jackson.databind.JsonNode;
import net.catena_x.btp.libraries.edc.EdcApi;
import net.catena_x.btp.libraries.edc.util.exceptions.EdcException;
import net.catena_x.btp.libraries.notification.dto.Notification;
import net.catena_x.btp.libraries.oem.backend.database.util.exceptions.OemDatabaseException;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.Vehicle;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.VehicleTable;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import net.catena_x.btp.rul.oem.backend.model.dto.calculation.RuLCalculationTable;
import net.catena_x.btp.rul.oem.backend.model.enums.RuLCalculationStatus;
import net.catena_x.btp.rul.oem.backend.rul_service.collector.util.RuLInputDataBuilder;
import net.catena_x.btp.rul.oem.backend.rul_service.collector.util.RuLSupplierNotificationCreator;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dto.requester.RuLNotificationFromRequesterContent;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dto.supplierservice.RuLDataToSupplierContent;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dto.supplierservice.RuLNotificationToSupplierConverter;
import net.catena_x.btp.rul.oem.backend.rul_service.util.RuLStarterApiResult;
import net.catena_x.btp.rul.oem.backend.util.enums.RuLStarterCalculationType;
import net.catena_x.btp.rul.oem.util.exceptions.OemRuLException;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@Component
public class RuLCalculationStarter {
    @Autowired private ApiHelper apiHelper;
    @Autowired private RuLNotificationToSupplierConverter rulNotificationToSupplierConverter;
    @Autowired private RuLSupplierNotificationCreator rulSupplierNotificationCreator;
    @Autowired private RuLVinToIdConverter rulVinToIdConverter;
    @Autowired private VehicleTable vehicleTable;
    @Autowired private RuLInputDataBuilder rulInputDataBuilder;
    @Autowired private RuLCalculationTable rulCalculationTable;
    @Autowired private EdcApi edcApi;

    @Value("${supplier.rulservice.inputAssetName}") private String inputAssetName;
    @Value("${supplier.rulservice.endpoint}") private URL supplierRuLServiceEndpoint;

    private final Logger logger = LoggerFactory.getLogger(RuLCalculationStarter.class);

    public ResponseEntity<String> startCalculation(
            @NotNull final String requesterNotificationId,
            @NotNull final RuLNotificationFromRequesterContent requesterNotificatonContent) throws OemRuLException {

        try {
            final Vehicle vehicle = getVehicleByVIN(requesterNotificatonContent.getVin());

            final String requestId = generateRequestId();
            final RuLDataToSupplierContent dataToSupplierContent = rulInputDataBuilder.build(requestId, vehicle);

            if(dataToSupplierContent == null) {
                return noDataForVehicle(requesterNotificatonContent.getVin());
            }

            createNewCalculationInDatabase(requestId, requesterNotificationId);
            dispatchRequestWithHttp(requestId, dataToSupplierContent);
        } catch(final Exception exception) {
            return apiHelper.failedAsString("Starting RuL calculation failed: " + exception.getMessage());
        }

        return ok(RuLStarterCalculationType.OK_CALCULATING, "Started RuL calculation successfully.");
    }

    @NotNull
    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    private ResponseEntity<String> noDataForVehicle(@NotNull final String vin) {
        return ok(RuLStarterCalculationType.OK_NO_DATA,
                "Vehicle with vin " + vin + " has no load collectives for RuL calculation.");
    }

    private ResponseEntity<String> ok(final RuLStarterCalculationType type, final String info) {
        return apiHelper.okAsString(new RuLStarterApiResult(Instant.now(), type, info), RuLStarterApiResult.class);
    }

    private Vehicle getVehicleByVIN(final String vin) throws OemRuLException {
        try {
            final Vehicle vehicle = vehicleTable.getByIdWithTelematicsDataNewTransaction(getVehicleIdByVIN(vin));
            checkVehicleFound(vehicle, vin);
            return vehicle;
        } catch(final OemDatabaseException exception) {
            throw new OemRuLException(exception.getMessage(), exception);
        }
    }

    private String getVehicleIdByVIN(final String vin) throws OemRuLException {
        assertVIN(vin);
        return rulVinToIdConverter.convert(vin);
    }

    private void checkVehicleFound(final Vehicle vehicle, final String vin) throws OemRuLException {
        if(vehicle == null) {
            throw new OemRuLException("No vehicle found to corresponding vin " + vin);
        }
    }

    private void assertVIN(final String vin) throws OemRuLException {
        if(vin == null) {
            throw new OemRuLException("No vin given!");
        }
    }

    private void createNewCalculationInDatabase(
            @NotNull final String requestId, @NotNull final String requesterNotificationId) throws OemRuLException {
        rulCalculationTable.createNowNewTransaction(requestId, requesterNotificationId);
    }


    private void dispatchRequestWithHttp(@NotNull final String requestId,
                                         @NotNull final RuLDataToSupplierContent rulDataToSupplierContent)
            throws OemRuLException {
        final Notification<RuLDataToSupplierContent> notification = prepareNotification(
                requestId, rulDataToSupplierContent);

        logger.info("Request for id " + requestId + " prepared.");

        processResult(requestId, callService(requestId, notification));
    }

    private Notification<RuLDataToSupplierContent> prepareNotification(
            @NotNull final String requestId, @NotNull final RuLDataToSupplierContent rulDataToSupplierContent)
            throws OemRuLException {

        try {
            return rulSupplierNotificationCreator.createForHttp(requestId, rulDataToSupplierContent);
        } catch (final Exception exception) {
            setCalculationStatus(requestId, RuLCalculationStatus.FAILED_INTERNAL_BUILD_CALCULATION_REQUEST);
            throw new OemRuLException(exception);
        }
    }

    private void setCalculationStatus(@NotNull final String requestId, @NotNull final RuLCalculationStatus newStatus)
            throws OemRuLException {
        rulCalculationTable.updateStatusNewTransaction(requestId, newStatus);
    }

    private void processResult(@NotNull final String requestId, @NotNull final ResponseEntity<JsonNode> result)
            throws OemRuLException {

        if (result.getStatusCode() == HttpStatus.OK
                || result.getStatusCode() == HttpStatus.CREATED
                || result.getStatusCode() == HttpStatus.ACCEPTED) {
            logger.info("External calculation service for id " + requestId + " started.");
            setCalculationStatus(requestId, RuLCalculationStatus.RUNNING);
        } else {
            serviceCallFailed("Starting external calculation service for id \" + requestId + \" failed: "
                    + "http code " + result.getStatusCode().toString() + ", response body: "
                    + result.getBody().toString());
        }
    }

    private ResponseEntity<JsonNode> callService(
            @NotNull final String requestId, @NotNull final Notification<RuLDataToSupplierContent> notification)
            throws OemRuLException {
        return startAsyncRequest(requestId, supplierRuLServiceEndpoint.toString(), inputAssetName,
                rulNotificationToSupplierConverter.toDAO(notification), JsonNode.class);
    }

    private void serviceCallFailed(@NotNull final String errorText) throws OemRuLException {
        logger.error(errorText);
        throw new OemRuLException(errorText);
    }

    public <BodyType, ResponseType> ResponseEntity<ResponseType> startAsyncRequest(
            @NotNull final String requestId, @NotNull final String endpoint, @NotNull final String asset,
            @NotNull final BodyType messageBody, @NotNull Class<ResponseType> responseTypeClass)
            throws OemRuLException {

        try {
            return edcApi.post(HttpUrl.parse(endpoint), asset, responseTypeClass,
                    messageBody, generateDefaultHeaders());
        } catch (final EdcException exception) {
            setCalculationStatus(requestId, RuLCalculationStatus.FAILED_EXTERNAL);
            throw new OemRuLException(exception);
        }
    }

    protected HttpHeaders generateDefaultHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
