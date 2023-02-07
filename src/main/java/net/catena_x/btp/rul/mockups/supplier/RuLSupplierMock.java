package net.catena_x.btp.rul.mockups.supplier;

import net.catena_x.btp.libraries.bamm.common.BammLoaddataSource;
import net.catena_x.btp.libraries.bamm.common.BammStatus;
import net.catena_x.btp.libraries.bamm.custom.remainingusefullife.RemainingUsefulLife;
import net.catena_x.btp.libraries.notification.dao.NotificationDAO;
import net.catena_x.btp.libraries.notification.dto.Notification;
import net.catena_x.btp.libraries.notification.dto.items.NotificationHeader;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import net.catena_x.btp.libraries.util.apihelper.ResponseChecker;
import net.catena_x.btp.libraries.util.apihelper.model.DefaultApiResult;
import net.catena_x.btp.libraries.util.exceptions.BtpException;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dao.supplierservice.RuLNotificationFromSupplierContentDAO;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dao.supplierservice.RuLNotificationToSupplierContentDAO;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dao.supplierservice.items.RuLInputDAO;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dao.supplierservice.items.RuLOutputDAO;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;

@Component
public class RuLSupplierMock {
    @Autowired private ApiHelper apiHelper;
    @Autowired private RestTemplate restTemplate;

    @Value("${security.api.username}") private String apiUsername;
    @Value("${security.api.password}") private String apiPassword;

    private final Logger logger = LoggerFactory.getLogger(RuLSupplierMock.class);

    public ResponseEntity<DefaultApiResult> runRuLCalculationMock(
                @NotNull final NotificationDAO<RuLNotificationToSupplierContentDAO> data) {
        final List<RuLInputDAO> rulInputs = data.getContent().getEndurancePredictorInputs();
        rulInputs.sort(Comparator.comparing(RuLInputDAO::getComponentId));

        final List<RuLOutputDAO> outputs = new ArrayList<>(rulInputs.size());
        for (final RuLInputDAO inputData: rulInputs) {
            outputs.add(new RuLOutputDAO(inputData.getComponentId(),
                    "GearBox", generateRemainingUsefulLife()));
        }

        final Notification<RuLNotificationFromSupplierContentDAO> notification = new Notification<>();
        notification.setHeader(new NotificationHeader());
        notification.getHeader().setReferencedNotificationID(data.getContent().getRequestRefId());

        notification.setContent(new RuLNotificationFromSupplierContentDAO());
        notification.getContent().setRequestRefId(data.getContent().getRequestRefId());
        notification.getContent().setComponentType("GearBox");
        notification.getContent().setEndurancePredictorOutputs(outputs);

        new Thread(() ->
        {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException exception) {
            }

            final HttpUrl requestUrl = HttpUrl.parse("http://localhost:25554/")
                    .newBuilder().addPathSegment("ruldatareceiver").addPathSegment("notifyresult").build();

            final HttpHeaders headers = generateDefaultHeaders();
            addAuthorizationHeaders(headers);

            final HttpEntity<Notification<RuLNotificationFromSupplierContentDAO>> request =
                    new HttpEntity<>(notification, headers);

            final ResponseEntity<DefaultApiResult> response = restTemplate.postForEntity(
                    requestUrl.toString(), request, DefaultApiResult.class);

            checkResponse(response);
        }).start();
        return apiHelper.accepted("Accepted.");
    }

    private void checkResponse(final ResponseEntity<DefaultApiResult> response) {
        try {
            ResponseChecker.checkResponse(response);
        } catch (final BtpException exception) {
            logger.error(exception.getMessage());
        }
    }

    private void addAuthorizationHeaders(final HttpHeaders headers) {
        headers.add("Authorization", getAuthString());
    }

    private String getAuthString() {
        StringBuilder sb = new StringBuilder();

        String authStr = sb.append(apiUsername).append(":").append(apiPassword).toString();
        sb.setLength(0);
        sb.append("Basic ").append(Base64.getEncoder().encodeToString(authStr.getBytes()));
        return sb.toString();
    }

    protected HttpHeaders generateDefaultHeaders() {
        final HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private RemainingUsefulLife generateRemainingUsefulLife() {
        return new RemainingUsefulLife(4314.0f, "4314", 123458,
                        new BammLoaddataSource("loggedOEM", "loggedOEM"),
                        new BammStatus(Instant.now() , "840", 840.0f, 99331));
    }
}
