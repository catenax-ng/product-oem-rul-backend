package net.catena_x.btp.rul.oem.backend.model.dto.calculation;

import net.catena_x.btp.libraries.bamm.custom.remainingusefullife.RemainingUsefulLife;
import net.catena_x.btp.rul.oem.backend.database.rul.tables.calculation.RuLCalculationTableInternal;
import net.catena_x.btp.rul.oem.backend.model.dto.util.RuLRemainingUsefulLifeConverter;
import net.catena_x.btp.rul.oem.backend.model.enums.RuLCalculationStatus;
import net.catena_x.btp.rul.oem.util.exceptions.OemRuLException;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Component
public class RuLCalculationTable {
    @Autowired private RuLCalculationTableInternal internal;
    @Autowired private RuLCalculationConverter rulCalculationConverter;
    @Autowired private RuLRemainingUsefulLifeConverter rulRemainingUsefulLifeConverter;

    public void resetDbNewTransaction() throws OemRuLException {
        internal.resetDbNewTransaction();
    }

    public void resetDbExternalTransaction() throws OemRuLException {
        internal.resetDbExternalTransaction();
    }

    public void insertNewTransaction(@NotNull final String id, @NotNull final String requesterNotificationId,
                                     @NotNull final Instant calculationTimestamp,
                                     @NotNull final RuLCalculationStatus status,
                                     @Nullable final RemainingUsefulLife rul) throws OemRuLException {
        internal.insertNewTransaction(
                id, requesterNotificationId, calculationTimestamp, status, rulRemainingUsefulLifeConverter.toDAO(rul) );
    }

    public void insertExternalTransaction(@NotNull final String id, @NotNull final String requesterNotificationId,
                                          @NotNull final Instant calculationTimestamp,
                                          @NotNull final RuLCalculationStatus status,
                                          @Nullable final RemainingUsefulLife rul) throws OemRuLException {
        internal.insertExternalTransaction(
                id, requesterNotificationId, calculationTimestamp, status, rulRemainingUsefulLifeConverter.toDAO(rul) );
    }

    public String insertGetIdNewTransaction(@NotNull final String requesterNotificationId,
                                            @NotNull final Instant calculationTimestamp,
                                            @NotNull final RuLCalculationStatus status,
                                            @Nullable final RemainingUsefulLife rul) throws OemRuLException {
        return internal.insertGetIdNewTransaction(
                requesterNotificationId, calculationTimestamp, status, rulRemainingUsefulLifeConverter.toDAO(rul));
    }

    public String insertGetIdExternalTransaction(@NotNull final String requesterNotificationId,
                                                 @NotNull final Instant calculationTimestamp,
                                                 @NotNull final RuLCalculationStatus status,
                                                 @Nullable final RemainingUsefulLife rul) throws OemRuLException {
        return internal.insertGetIdExternalTransaction(
                requesterNotificationId, calculationTimestamp, status, rulRemainingUsefulLifeConverter.toDAO(rul));
    }

    public void createNowNewTransaction(@NotNull final String id, @NotNull final String requesterNotificationId)
            throws OemRuLException {
        internal.createNowNewTransaction(id, requesterNotificationId);
    }

    public void createNowExternalTransaction(@NotNull final String id, @NotNull final String requesterNotificationId)
            throws OemRuLException {
        internal.createNowExternalTransaction(id, requesterNotificationId);
    }

    public String createNowGetIdNewTransaction(@NotNull final String requesterNotificationId) throws OemRuLException {
        return internal.createNowGetIdNewTransaction(requesterNotificationId);
    }

    public String createNowGetIdExternalTransaction(@NotNull final String requesterNotificationId)
            throws OemRuLException {
        return internal.createNowGetIdExternalTransaction(requesterNotificationId);
    }

    public void updateStatusNewTransaction(@NotNull final String id, @NotNull final RuLCalculationStatus newStatus)
            throws OemRuLException {
        internal.updateStatusNewTransaction(id, newStatus);
    }

    public void updateStatusExternalTransaction(@NotNull final String id, @NotNull final RuLCalculationStatus newStatus)
            throws OemRuLException {
        internal.updateStatusExternalTransaction(id, newStatus);
    }

    public void updateStatusAndRuLNewTransaction(
            @NotNull final String id, @NotNull final RuLCalculationStatus newStatus,
            @Nullable final RemainingUsefulLife rul) throws OemRuLException {
        internal.updateStatusAndRuLNewTransaction(id, newStatus, rulRemainingUsefulLifeConverter.toDAO(rul));
    }

    public void updateStatusAndRuLExternalTransaction(
            @NotNull final String id, @NotNull final RuLCalculationStatus newStatus,
            @Nullable final RemainingUsefulLife rul) throws OemRuLException {
        internal.updateStatusAndRuLExternalTransaction(id, newStatus, rulRemainingUsefulLifeConverter.toDAO(rul));
    }

    public void deleteAllNewTransaction() throws OemRuLException {
        internal.deleteAllNewTransaction();
    }

    public void deleteAllExternalTransaction() throws OemRuLException {
        internal.deleteAllExternalTransaction();
    }

    public void deleteByIdNewTransaction(@NotNull final String id) throws OemRuLException {
        internal.deleteByIdNewTransaction(id);
    }

    public void deleteByIdExternalTransaction(@NotNull final String id) throws OemRuLException {
        internal.deleteByIdExternalTransaction(id);
    }

    public void deleteByRequesterNotificationIdNewTransaction(@NotNull final String requesterNotificationId)
            throws OemRuLException {
        internal.deleteByRequesterNotificationIdNewTransaction(requesterNotificationId);
    }

    public void deleteByRequesterNotificationIdExternalTransaction(@NotNull final String requesterNotificationId)
            throws OemRuLException {
        internal.deleteByRequesterNotificationIdExternalTransaction(requesterNotificationId);
    }

    public void deleteByStatusNewTransaction(@NotNull final RuLCalculationStatus status) throws OemRuLException {
        internal.deleteByStatusNewTransaction(status);
    }

    public void deleteByStatusExternalTransaction(@NotNull final RuLCalculationStatus status) throws OemRuLException {
        internal.deleteByStatusExternalTransaction(status);
    }

    public RuLCalculation getByIdNewTransaction(@NotNull final String id) throws OemRuLException {
        return rulCalculationConverter.toDTO(internal.getByIdNewTransaction(id));
    }

    public RuLCalculation getByIdExternalTransaction(@NotNull final String id) throws OemRuLException {
        return rulCalculationConverter.toDTO(internal.getByIdExternalTransaction(id));
    }

    public RuLCalculation getByRequesterNotificationIdNewTransaction(@NotNull final String requesterNotificationId)
            throws OemRuLException {
        return rulCalculationConverter.toDTO(
                internal.getByRequesterNotificationIdNewTransaction(requesterNotificationId));
    }

    public RuLCalculation getByRequesterNotificationIdExternalTransaction(@NotNull final String requesterNotificationId)
            throws OemRuLException {
        return rulCalculationConverter.toDTO(
                internal.getByRequesterNotificationIdExternalTransaction(requesterNotificationId));
    }

    public List<RuLCalculation> getAllNewTransaction()
            throws OemRuLException {
        return rulCalculationConverter.toDTO(internal.getAllNewTransaction());
    }

    public List<RuLCalculation> getAllExternalTransaction()
            throws OemRuLException {
        return rulCalculationConverter.toDTO(internal.getAllExternalTransaction());
    }

    public List<RuLCalculation> getByStatusNewTransaction(@NotNull final RuLCalculationStatus status)
            throws OemRuLException {
        return rulCalculationConverter.toDTO(internal.getByStatusNewTransaction(status));
    }

    public List<RuLCalculation> getByStatusExternalTransaction(@NotNull final RuLCalculationStatus status)
            throws OemRuLException {
        return rulCalculationConverter.toDTO(internal.getByStatusExternalTransaction(status));
    }

    public List<RuLCalculation> getAllOrderByCalculationTimestampNewTransaction() throws OemRuLException {
        return rulCalculationConverter.toDTO(internal.getAllOrderByCalculationTimestampNewTransaction());
    }

    public List<RuLCalculation> getAllOrderByCalculationTimestampExternalTransaction() throws OemRuLException {
        return rulCalculationConverter.toDTO(internal.getAllOrderByCalculationTimestampExternalTransaction());
    }

    public List<RuLCalculation> getByCalculationSinceNewTransaction(@NotNull final Instant calculationTimestampSince)
            throws OemRuLException {
        return rulCalculationConverter.toDTO(internal.getByCalculationSinceNewTransaction(calculationTimestampSince));
    }

    public List<RuLCalculation> getByCalculationSinceExternalTransaction(
            @NotNull final Instant calculationTimestampSince) throws OemRuLException {
        return rulCalculationConverter.toDTO(internal.getByCalculationSinceExternalTransaction(calculationTimestampSince));
    }

    public List<RuLCalculation> getByCalculationUntilNewTransaction(@NotNull final Instant calculationTimestampUntil)
            throws OemRuLException {
        return rulCalculationConverter.toDTO(internal.getByCalculationUntilNewTransaction(calculationTimestampUntil));
    }

    public List<RuLCalculation> getByCalculationUntilExternalTransaction(
            @NotNull final Instant calculationTimestampUntil) throws OemRuLException {
        return rulCalculationConverter.toDTO(
                internal.getByCalculationUntilExternalTransaction(calculationTimestampUntil));
    }
}
