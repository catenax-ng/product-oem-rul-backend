package net.catena_x.btp.rul.oem.backend.util.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuLStarterCalculationType {
    @JsonProperty("OkNoData")
    OK_NO_DATA,
    @JsonProperty("OkCalculating")
    OK_CALCULATING
}
