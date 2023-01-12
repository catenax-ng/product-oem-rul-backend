package net.catena_x.btp.rul.oem.backend.rul_service.collector.util;

import net.catena_x.btp.libraries.notification.dto.Notification;
import net.catena_x.btp.libraries.notification.dto.items.NotificationHeader;
import net.catena_x.btp.libraries.notification.enums.NFSeverity;
import net.catena_x.btp.libraries.notification.enums.NFStatus;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dto.supplierservice.RuLDataToSupplierContent;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.enums.NotificationClassification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

@Component
public class RuLSupplierNotificationCreator {
    @Value("${supplier.rulservice.endpoint}") private URL supplierRuLServiceEndpoint;
    @Value("${supplier.bpn}") private String supplierBpn;
    @Value("${edc.bpn}") private String edcBpn;
    @Value("${edc.endpoint}") private URL edcEndpoint;

    public Notification<RuLDataToSupplierContent> createForHttp(
            @NotNull final String requestId,
            @NotNull final RuLDataToSupplierContent RuLDataToSupplierContent) {

        final Notification<RuLDataToSupplierContent> notification = new Notification<>();
        notification.setHeader(createHeader(requestId));
        notification.setContent(RuLDataToSupplierContent);
        return notification;
    }

    private NotificationHeader createHeader(@NotNull final String requestId) {

        final NotificationHeader header = new NotificationHeader();

        header.setNotificationID(requestId);

        setSenderData(header);
        setRecipientData(header);
        setRequestDependentData(header);

        return header;
    }

    private void setSenderData(@NotNull final NotificationHeader headerInOut) {
        headerInOut.setSenderBPN(edcBpn);
        headerInOut.setSenderAddress(edcEndpoint.toString());
    }

    private void setRecipientData(@NotNull final NotificationHeader headerInOut) {
        headerInOut.setRecipientBPN(supplierBpn);
        headerInOut.setRecipientAddress(supplierRuLServiceEndpoint.toString());
    }

    private void setRequestDependentData(@NotNull final NotificationHeader headerInOut) {
        headerInOut.setClassification(NotificationClassification.RULRESULT.toString());
        headerInOut.setSeverity(NFSeverity.MINOR);
        headerInOut.setStatus(NFStatus.SENT);
        headerInOut.setTimeStamp(Instant.now());
        headerInOut.setTargetDate(headerInOut.getTimeStamp().plus(Duration.ofHours(12L)));
    }
}
