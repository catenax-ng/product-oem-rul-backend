package net.catena_x.btp.rul.oem.backend.database.rul.tables.calculation;

import net.catena_x.btp.rul.oem.backend.database.rul.annotations.RuLTransactionDefaultCreateNew;
import net.catena_x.btp.rul.oem.backend.database.rul.annotations.RuLTransactionDefaultUseExisting;
import net.catena_x.btp.rul.oem.backend.database.rul.annotations.RuLTransactionSerializableCreateNew;
import net.catena_x.btp.rul.oem.backend.database.rul.annotations.RuLTransactionSerializableUseExisting;
import net.catena_x.btp.rul.oem.backend.database.rul.base.RuLTableBase;
import net.catena_x.btp.rul.oem.backend.database.rul.tables.vinrelation.RuLVinRelationTableInternal;
import net.catena_x.btp.rul.oem.backend.model.enums.RuLCalculationStatus;
import net.catena_x.btp.rul.oem.util.exceptions.OemRuLException;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Component
public class RuLCalculationTableInternal extends RuLTableBase {
    @Autowired private RuLCalculationRepository rulCalculationRepository;
    @Autowired private RuLVinRelationTableInternal rulVinRelationTableInternal;

    @RuLTransactionSerializableUseExisting
    public void resetDbExternalTransaction() throws OemRuLException {
        deleteAllExternalTransaction();
        rulVinRelationTableInternal.deleteAllExternalTransaction();
    }
    @RuLTransactionSerializableCreateNew
    public void resetDbNewTransaction() throws OemRuLException {
        resetDbExternalTransaction();
    }

    @RuLTransactionSerializableUseExisting
    public void insertExternalTransaction(@NotNull final String id,
                                          @NotNull final String requesterNotificationId,
                                          @NotNull final Instant calculationTimestamp,
                                          @NotNull final RuLCalculationStatus status,
                                          @Nullable final String rul) throws OemRuLException {
        try {
            rulCalculationRepository.insert(id, requesterNotificationId, calculationTimestamp, status.toString(), rul);
        } catch (final Exception exception) {
            throw failed("Inserting calculation failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public void insertNewTransaction(@NotNull final String id,
                                     @NotNull final String requesterNotificationId,
                                     @NotNull final Instant calculationTimestamp,
                                     @NotNull final RuLCalculationStatus status,
                                     @Nullable final String rul) throws OemRuLException {
        insertExternalTransaction(id, requesterNotificationId, calculationTimestamp, status, rul);
    }

    @RuLTransactionSerializableUseExisting
    public String insertGetIdExternalTransaction(@NotNull final String requesterNotificationId,
                                                 @NotNull final Instant calculationTimestamp,
                                                 @NotNull final RuLCalculationStatus status,
                                                 @Nullable final String rul) throws OemRuLException {
        try {
            final String id = generateNewId();
            rulCalculationRepository.insert(id, requesterNotificationId, calculationTimestamp, status.toString(), rul);
            return id;
        } catch (final Exception exception) {
            throw failed("Inserting calculation failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public String insertGetIdNewTransaction(@NotNull final String requesterNotificationId,
                                            @NotNull final Instant calculationTimestamp,
                                            @NotNull final RuLCalculationStatus status,
                                            @Nullable final String rul) throws OemRuLException {
        return insertGetIdExternalTransaction(requesterNotificationId, calculationTimestamp, status, rul);
    }

    @RuLTransactionSerializableUseExisting
    public void createNowExternalTransaction(@NotNull final String id,
                                             @NotNull final String requesterNotificationId) throws OemRuLException {
        try {
            rulCalculationRepository.createNow(
                    id, requesterNotificationId, RuLCalculationStatus.CREATED.toString(), null);
        } catch (final Exception exception) {
            throw failed("Inserting calculation failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public void createNowNewTransaction(@NotNull final String id,
                                        @NotNull final String requesterNotificationId) throws OemRuLException {
        createNowExternalTransaction(id, requesterNotificationId);
    }

    @RuLTransactionSerializableUseExisting
    public String createNowGetIdExternalTransaction(@NotNull final String requesterNotificationId)
            throws OemRuLException {
        try {
            final String id = generateNewId();
            rulCalculationRepository.createNow(
                    id, requesterNotificationId, RuLCalculationStatus.CREATED.toString(), null);
            return id;
        } catch (final Exception exception) {
            throw failed("Inserting calculation failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public String createNowGetIdNewTransaction(@NotNull final String requesterNotificationId)
            throws OemRuLException {
        return createNowGetIdExternalTransaction(requesterNotificationId);
    }

    @RuLTransactionSerializableUseExisting
    public void updateStatusExternalTransaction(@NotNull final String id, @NotNull final RuLCalculationStatus newStatus)
            throws OemRuLException {
        try {
            rulCalculationRepository.updateStatus(id, newStatus.toString());
        } catch (final Exception exception) {
            throw failed("Updating calculation status failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public void updateStatusNewTransaction(@NotNull final String id, @NotNull final RuLCalculationStatus newStatus)
            throws OemRuLException {
        updateStatusExternalTransaction(id, newStatus);
    }

    @RuLTransactionSerializableUseExisting
    public void updateStatusAndRuLExternalTransaction(
            @NotNull final String id, @NotNull final RuLCalculationStatus newStatus,
            @NotNull final String rul) throws OemRuLException {
        try {
            rulCalculationRepository.updateStatusAndRuL(id, newStatus.toString(), rul);
        } catch (final Exception exception) {
            throw failed("Updating calculation status and RuL failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public void updateStatusAndRuLNewTransaction(
            @NotNull final String id, @NotNull final RuLCalculationStatus newStatus, @NotNull final String rul)
            throws OemRuLException {
        updateStatusAndRuLExternalTransaction(id, newStatus, rul);
    }

    @RuLTransactionDefaultUseExisting
    public void deleteAllExternalTransaction() throws OemRuLException {
        try {
            rulCalculationRepository.deleteAll();
        } catch (final Exception exception) {
            throw failed("Deleting all calculations failed!", exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public void deleteAllNewTransaction() throws OemRuLException {
        deleteAllExternalTransaction();
    }

    @RuLTransactionSerializableUseExisting
    public void deleteByIdExternalTransaction(@NotNull final String id) throws OemRuLException {
        try {
            rulCalculationRepository.deleteById(id);
        } catch (final Exception exception) {
            throw failed("Deleting calculation failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public void deleteByIdNewTransaction(@NotNull final String id) throws OemRuLException {
        deleteByIdExternalTransaction(id);
    }

    @RuLTransactionSerializableUseExisting
    public void deleteByRequesterNotificationIdExternalTransaction(@NotNull final String requesterNotificationId)
            throws OemRuLException {
        try {
            rulCalculationRepository.deleteByRequesterNotificationId(requesterNotificationId);
        } catch (final Exception exception) {
            throw failed("Deleting calculation failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public void deleteByRequesterNotificationIdNewTransaction(@NotNull final String requesterNotificationId)
            throws OemRuLException {
        deleteByRequesterNotificationIdExternalTransaction(requesterNotificationId);
    }

    @RuLTransactionSerializableUseExisting
    public void deleteByStatusExternalTransaction(@NotNull final RuLCalculationStatus status)
            throws OemRuLException {
        try {
            rulCalculationRepository.deleteByStatus(status.toString());
        } catch (final Exception exception) {
            throw failed("Deleting calculation failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public void deleteByStatusNewTransaction(@NotNull final RuLCalculationStatus status) throws OemRuLException {
        deleteByStatusExternalTransaction(status);
    }

    @RuLTransactionSerializableUseExisting
    public void deleteCalculatedUntilExternalTransaction(@NotNull final Instant calculatedUntil) throws OemRuLException {
        try {
            rulCalculationRepository.deleteCalculatedUntil(calculatedUntil);
        } catch (final Exception exception) {
            throw failed("Deleting calculation failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public void deleteCalculatedUntilNewTransaction(@NotNull final Instant calculatedUntil)
            throws OemRuLException {
        deleteCalculatedUntilExternalTransaction(calculatedUntil);
    }

    @RuLTransactionSerializableUseExisting
    public List<RuLCalculationDAO> getAllExternalTransaction() throws OemRuLException {
        try {
            return rulCalculationRepository.queryAll();
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public List<RuLCalculationDAO> getAllNewTransaction() throws OemRuLException {
        return getAllExternalTransaction();
    }

    @RuLTransactionSerializableUseExisting
    public RuLCalculationDAO getByIdExternalTransaction(@NotNull final String id) throws OemRuLException {
        try {
            return rulCalculationRepository.queryById(id);
        } catch (final Exception exception) {
            throw failed("Querying calculation by id failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public RuLCalculationDAO getByIdNewTransaction(@NotNull final String id) throws OemRuLException {
        return getByIdExternalTransaction(id);
    }

    @RuLTransactionSerializableUseExisting
    public RuLCalculationDAO getByRequesterNotificationIdExternalTransaction(@NotNull final String requesterNotificationId)
            throws OemRuLException {
        try {
            return rulCalculationRepository.queryByRequesterNotificationId(requesterNotificationId);
        } catch (final Exception exception) {
            throw failed("Querying calculation by requester notification id failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public RuLCalculationDAO getByRequesterNotificationIdNewTransaction(@NotNull final String requesterNotificationId)
            throws OemRuLException {
        return getByRequesterNotificationIdExternalTransaction(requesterNotificationId);
    }

    @RuLTransactionSerializableUseExisting
    public List<RuLCalculationDAO> getByStatusExternalTransaction(@NotNull final RuLCalculationStatus status)
            throws OemRuLException {
        try {
            return rulCalculationRepository.queryByStatus(status.toString());
        } catch (final Exception exception) {
            throw failed("Querying calculations by status failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public List<RuLCalculationDAO> getByStatusNewTransaction(@NotNull final RuLCalculationStatus status)
            throws OemRuLException {
        return getByStatusExternalTransaction(status);
    }

    @RuLTransactionSerializableUseExisting
    public List<RuLCalculationDAO> getAllOrderByCalculationTimestampExternalTransaction() throws OemRuLException {
        try {
            return rulCalculationRepository.queryAllOrderByCalculationTimestamp();
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public List<RuLCalculationDAO> getAllOrderByCalculationTimestampNewTransaction() throws OemRuLException {
        return getAllOrderByCalculationTimestampExternalTransaction();
    }

    @RuLTransactionSerializableUseExisting
    public List<RuLCalculationDAO> getByCalculationSinceExternalTransaction(
            @NotNull final Instant calculationTimestampSince) throws OemRuLException {
        try {
            return rulCalculationRepository.queryByCalculationSince(calculationTimestampSince);
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public List<RuLCalculationDAO> getByCalculationSinceNewTransaction(
            @NotNull final Instant calculationTimestampSince) throws OemRuLException {
        return getByCalculationSinceExternalTransaction(calculationTimestampSince);
    }

    @RuLTransactionSerializableUseExisting
    public List<RuLCalculationDAO> getByCalculationUntilExternalTransaction(
            @NotNull final Instant calculationTimestampUntil) throws OemRuLException {
        try {
            return rulCalculationRepository.queryByCalculationUntil(calculationTimestampUntil);
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @RuLTransactionSerializableCreateNew
    public List<RuLCalculationDAO> getByCalculationUntilNewTransaction(
            @NotNull final Instant calculationTimestampUntil) throws OemRuLException {
        return getByCalculationUntilExternalTransaction(calculationTimestampUntil);
    }
}
