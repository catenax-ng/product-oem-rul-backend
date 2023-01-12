package net.catena_x.btp.rul.mockups.requester;

import net.catena_x.btp.libraries.notification.dao.NotificationDAO;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import net.catena_x.btp.libraries.util.apihelper.model.DefaultApiResult;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dao.requester.RuLNotificationToRequesterContentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class RuLRequesterMock {
    @Autowired private ApiHelper apiHelper;

    public ResponseEntity<DefaultApiResult> receiveRuLResultFromOemMock(
            @NotNull final NotificationDAO<RuLNotificationToRequesterContentDAO> data) {

        try {
            System.out.println("Received result for RuL calculation with id "
                    + data.getHeader().getReferencedNotificationID() + ": RuL = ("
                    + data.getContent().getRul().getRemainingOperatingTime() + " h, "
                    + data.getContent().getRul().getRemainingRunningDistance() + " km)");
            return apiHelper.ok("Result successfully processed.");
        } catch (final Exception exception) {
            final String id = data.getHeader() != null?
                    (data.getHeader().getReferencedNotificationID() != null?
                            data.getHeader().getReferencedNotificationID() : "unknown") : "unknown";
            return apiHelper.failed("Receiving RuL data for id " + id + " failed: " + exception.getMessage());
        }
    }
}
