package net.catena_x.btp.rul.oem.backend.model.enums;

public enum RuLCalculationStatus {
    CREATED,
    RUNNING,
    FAILED_INTERNAL_BUILD_REQUEST,
    FAILED_INTERNAL_PROCESS_RESULTS,
    FAILED_EXTERNAL,
    ABANDONED,
    CALCULATED,
    READY,
    SENT_TO_REQUESTER,
    SEND_TO_REQUESTER_FAILED
}
