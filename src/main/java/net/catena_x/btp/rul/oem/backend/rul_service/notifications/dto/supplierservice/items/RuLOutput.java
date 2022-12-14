package net.catena_x.btp.rul.oem.backend.rul_service.notifications.dto.supplierservice.items;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.catena_x.btp.libraries.bamm.custom.remainingusefullife.RemainingUsefulLife;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuLOutput {
    private String componentId;
    private RemainingUsefulLife remainingUsefulLife;
}
