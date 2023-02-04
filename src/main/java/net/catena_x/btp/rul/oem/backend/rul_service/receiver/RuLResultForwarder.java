package net.catena_x.btp.rul.oem.backend.rul_service.receiver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.catena_x.btp.libraries.bamm.custom.remainingusefullife.RemainingUsefulLife;
import net.catena_x.btp.libraries.edc.EdcApi;
import net.catena_x.btp.libraries.edc.model.EdcAssetAddress;
import net.catena_x.btp.libraries.edc.util.exceptions.EdcException;
import net.catena_x.btp.libraries.notification.dto.Notification;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import net.catena_x.btp.libraries.util.apihelper.model.DefaultApiResult;
import net.catena_x.btp.libraries.util.datahelper.DataHelper;
import net.catena_x.btp.rul.oem.backend.model.dto.calculation.RuLCalculation;
import net.catena_x.btp.rul.oem.backend.model.dto.calculation.RuLCalculationTable;
import net.catena_x.btp.rul.oem.backend.model.enums.RuLCalculationStatus;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dto.requester.RuLDataToRequesterContent;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dto.requester.RuLNotificationToRequesterConverter;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dto.supplierservice.RuLNotificationFromSupplierContent;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dto.supplierservice.items.RuLOutput;
import net.catena_x.btp.rul.oem.backend.rul_service.receiver.util.RuLRequesterNotificationCreator;
import net.catena_x.btp.rul.oem.util.exceptions.OemRuLException;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.function.Supplier;

@Component
public class RuLResultForwarder {
    @Autowired private ApiHelper apiHelper;
    @Autowired private RuLCalculationTable rulCalculationTable;
    @Autowired private RuLNotificationToRequesterConverter rulNotificationToRequesterConverter;
    @Autowired private RuLRequesterNotificationCreator rulRequesterNotificationCreator;
    @Autowired private EdcApi edcApi;

    private final Logger logger = LoggerFactory.getLogger(RuLResultForwarder.class);

    public ResponseEntity<DefaultApiResult> forwardResult(
            @NotNull final String supplierNotificationID,
            @NotNull final RuLNotificationFromSupplierContent supplierNotificatonContent) throws OemRuLException {

        try {
            final RuLOutput result = DataHelper.getFirstAndOnlyItem(supplierNotificatonContent.getRulOutputs());
            assertIdsEqual(supplierNotificationID, supplierNotificatonContent);

            final RemainingUsefulLife remainingUsefulLife = result.getRemainingUsefulLife();
            updateCalculationStatusToCalculated(supplierNotificationID, remainingUsefulLife);

            final RuLCalculation calculation = rulCalculationTable.getByIdNewTransaction(
                    supplierNotificatonContent.getRequestRefId());

            if(calculation == null) {
                return apiHelper.failed("Failed to process RuL calculation result, unknown calculation id "
                        + supplierNotificatonContent.getRequestRefId() + "!");
            }

            forwardResultWithHttp(supplierNotificationID, calculation.getRequesterResultAddress(), remainingUsefulLife);

            return apiHelper.ok("Forwarded RuL calculation result with id "
                    + supplierNotificatonContent.getRequestRefId() + " successfully.");
        } catch (final Exception exception) {
            try {
                updateCalculationStatusToFailedProcessing(supplierNotificatonContent.getRequestRefId());
                return apiHelper.failed("Failed to process RuL calculation with id "
                        + supplierNotificatonContent.getRequestRefId() + ": " + exception.getMessage());
            } catch (final Exception innerException) {
                return apiHelper.failed("Failed to process RuL calculation result: "
                        + innerException.getMessage());
            }
        }
    }

    private void updateCalculationStatusToCalculated(
            @NotNull final String refId, @NotNull final RemainingUsefulLife remainingUsefulLife) throws Exception {

        final Supplier<Exception> updateStatus = ()-> {
            try {
                final RuLCalculation calculation = rulCalculationTable.getByIdExternalTransaction(refId);
                assertCalculation(refId, calculation);
                assertCalculationInStaredState(refId, calculation.getStatus());

                rulCalculationTable.updateStatusAndRuLExternalTransaction(refId, RuLCalculationStatus.CALCULATED,
                        remainingUsefulLife);

            } catch(final Exception exception) {
                return exception;
            }

            return null;
        };

        final Exception updateException = rulCalculationTable.runSerializableNewTransaction(updateStatus);
        if(updateException != null) {
            throw updateException;
        }
    }

    private void forwardResultWithHttp(@NotNull final String calculationId,
                                       @NotNull @NotNull final EdcAssetAddress requesterAssetAddress,
                                       @NotNull final RemainingUsefulLife result) throws OemRuLException {

        final Notification<RuLDataToRequesterContent> notification =
                prepareNotification(calculationId, requesterAssetAddress, new RuLDataToRequesterContent(result));

        logger.info("Forwarding for id " + calculationId + " prepared.");

        checkForwardResult(calculationId, forwardToRequester(calculationId, requesterAssetAddress, notification));
    }

    private Notification<RuLDataToRequesterContent> prepareNotification(
            @NotNull final String requestId, @NotNull @NotNull final EdcAssetAddress requesterAssetAddress,
            @NotNull final RuLDataToRequesterContent rulDataToRequesterContent) throws OemRuLException {

        try {
            return rulRequesterNotificationCreator.createForHttp(requestId, rulDataToRequesterContent,
                                                                 requesterAssetAddress);
        } catch (final Exception exception) {
            try {
                updateCalculationStatusToFaildBuildRequest(requestId);
            } catch (final Exception updateException) {
                throw new OemRuLException(updateException);
            }

            throw new OemRuLException(exception);
        }
    }

    private void checkForwardResult(@NotNull final String requestId,
                                    @NotNull final ResponseEntity<JsonNode> result) throws OemRuLException {
        if (result.getStatusCode() == HttpStatus.OK
                || result.getStatusCode() == HttpStatus.CREATED
                || result.getStatusCode() == HttpStatus.ACCEPTED) {
            logger.info("RuL calculation result for id " + requestId + " forwarded.");
            try {
                updateCalculationStatusToSent(requestId);
            } catch (final Exception exception) {
                throw new OemRuLException(exception);
            }
        } else {
            forwardingCallFailed(requestId, "Forwarding RuL calculation result to requester for id \"" +
                    " + requestId + \" failed: http code " + result.getStatusCode().toString() + ", response body: "
                    + result.getBody().toString());
        }
    }

    private void forwardingCallFailed(@NotNull final String requestId,
                                      @NotNull final String errorText) throws OemRuLException {
        logger.error(errorText);

        try {
            rulCalculationTable.updateStatusNewTransaction(requestId, RuLCalculationStatus.SEND_TO_REQUESTER_FAILED);
        } catch (final Exception exception) {
            new OemRuLException(exception.getMessage() + "\n" + errorText, exception);
        }

        throw new OemRuLException(errorText);
    }

    private ResponseEntity<JsonNode> forwardToRequester(
            @NotNull final String requestId, @NotNull final EdcAssetAddress requesterAssetAddress,
            @NotNull final Notification<RuLDataToRequesterContent> notification) throws OemRuLException {
        return startAsyncRequest(requestId, requesterAssetAddress.getConnectorUrl(), requesterAssetAddress.getAssetId(),
                rulNotificationToRequesterConverter.toDAO(notification), JsonNode.class);
    }

    @Autowired
    ObjectMapper objectMapper;
    public <BodyType, ResponseType> ResponseEntity<ResponseType> startAsyncRequest(
            @NotNull final String requestId, @NotNull final String endpoint, @NotNull final String asset,
            @NotNull final BodyType messageBody, @NotNull Class<ResponseType> responseTypeClass)
            throws OemRuLException {

        String test = "";
        try{
            test = objectMapper.writeValueAsString(messageBody);
        } catch (Exception e) {}


        try {
            return edcApi.post(HttpUrl.parse(endpoint), asset, responseTypeClass,
                    messageBody, generateDefaultHeaders());
        } catch (final EdcException exception) {
            try {
                updateCalculationStatusToFailedSending(requestId);
            } catch(final Exception updateException) {
                throw new OemRuLException(updateException);
            }

            throw new OemRuLException(exception);
        }
    }

    protected HttpHeaders generateDefaultHeaders() {
        final HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private void updateCalculationStatusToFailedProcessing(@NotNull final String refId) throws Exception {
        rulCalculationTable.updateStatusNewTransaction(refId, RuLCalculationStatus.FAILED_INTERNAL_PROCESS_RESULTS);
    }

    private void updateCalculationStatusToFailedSending(@NotNull final String refId) throws Exception {
        rulCalculationTable.updateStatusNewTransaction(refId, RuLCalculationStatus.SEND_TO_REQUESTER_FAILED);
    }

    private void updateCalculationStatusToSent(@NotNull final String refId) throws Exception {
        rulCalculationTable.updateStatusNewTransaction(refId, RuLCalculationStatus.SENT_TO_REQUESTER);
    }

    private void updateCalculationStatusToFaildBuildRequest(@NotNull final String refId) throws Exception {
        rulCalculationTable.updateStatusNewTransaction(
                refId, RuLCalculationStatus.FAILED_INTERNAL_BUILD_RESULT_REQUEST);
    }

    private static void assertCalculationInStaredState(
            @NotNull final String refId, @NotNull final RuLCalculationStatus status) throws OemRuLException {
        if(status != RuLCalculationStatus.CREATED
                && status != RuLCalculationStatus.RUNNING) {
            throw new OemRuLException("Calculation id " + refId + " had status " + status.toString() + "!");
        }
    }

    private static void assertCalculation(
            @NotNull final String refId, @Nullable final RuLCalculation calculation) throws OemRuLException {
        if(calculation == null) {
            throw new OemRuLException("Unknown calculation id " + refId + "!");
        }
    }

    private static void assertIdsEqual(@NotNull final String supplierNotificationID,
                                       @NotNull final RuLNotificationFromSupplierContent supplierNotificatonContent)
            throws OemRuLException {
        if (!supplierNotificationID.equals(supplierNotificatonContent.getRequestRefId())) {
            throw new OemRuLException("Id in notification content is not equal to referenced notification id!");
        }
    }
}

