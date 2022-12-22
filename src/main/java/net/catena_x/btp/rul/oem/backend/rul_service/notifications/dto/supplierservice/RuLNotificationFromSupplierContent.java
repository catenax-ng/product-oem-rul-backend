package net.catena_x.btp.rul.oem.backend.rul_service.notifications.dto.supplierservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.catena_x.btp.rul.oem.backend.rul_service.notifications.dto.supplierservice.items.RuLOutput;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuLNotificationFromSupplierContent {
    private String requestRefId;
    private List<RuLOutput> rulOutputs;
}
