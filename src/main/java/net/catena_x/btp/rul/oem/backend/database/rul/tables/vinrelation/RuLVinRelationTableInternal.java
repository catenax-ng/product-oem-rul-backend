package net.catena_x.btp.rul.oem.backend.database.rul.tables.vinrelation;

import net.catena_x.btp.rul.oem.backend.database.rul.annotations.RuLTransactionDefaultCreateNew;
import net.catena_x.btp.rul.oem.backend.database.rul.annotations.RuLTransactionDefaultUseExisting;
import net.catena_x.btp.rul.oem.backend.database.rul.base.RuLTableBase;
import net.catena_x.btp.rul.oem.util.exceptions.OemRuLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public class RuLVinRelationTableInternal extends RuLTableBase {
    @Autowired private RuLVinRelationRepository rulVinRelationRepository;

    @RuLTransactionDefaultUseExisting
    public void insertExternalTransaction(@NotNull final String vin,
                                          @NotNull final String refId) throws OemRuLException {
        try {
            rulVinRelationRepository.insert(vin, refId);
        } catch (final Exception exception) {
            throw failed("Inserting VIN relation failed!", exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public void insertNewTransaction(@NotNull final String vin, @NotNull final String refId) throws OemRuLException {
        insertExternalTransaction(vin, refId);
    }

    @RuLTransactionDefaultUseExisting
    public void deleteAllExternalTransaction() throws OemRuLException {
        try {
            rulVinRelationRepository.deleteAll();
        } catch (final Exception exception) {
            throw failed("Deleting all VIN relations failed!", exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public void deleteAllNewTransaction() throws OemRuLException {
        deleteAllExternalTransaction();
    }

    @RuLTransactionDefaultUseExisting
    public void deleteByVinExternalTransaction(@NotNull final String vin) throws OemRuLException {
        try {
            rulVinRelationRepository.deleteByVin(vin);
        } catch (final Exception exception) {
            throw failed("Deleting VIN relation failed!", exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public void deleteByVinNewTransaction(@NotNull final String vin) throws OemRuLException {
        deleteByVinExternalTransaction(vin);
    }

    @RuLTransactionDefaultUseExisting
    public void deleteByRefIdExternalTransaction(@NotNull final String refId) throws OemRuLException {
        try {
            rulVinRelationRepository.deleteByRefId(refId);
        } catch (final Exception exception) {
            throw failed("Deleting VIN relation failed!", exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public void deleteByRefIdNewTransaction(@NotNull final String refId) throws OemRuLException {
        deleteByRefIdExternalTransaction(refId);
    }

    @RuLTransactionDefaultUseExisting
    public List<RuLVinRelationDAO> getAllExternalTransaction() throws OemRuLException {
        try {
            return rulVinRelationRepository.queryAll();
        } catch (final Exception exception) {
            throw failed("Querying VIN relations failed!", exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public List<RuLVinRelationDAO> getAllNewTransaction() throws OemRuLException {
        return getAllExternalTransaction();
    }

    @RuLTransactionDefaultUseExisting
    public RuLVinRelationDAO getByVinExternalTransaction(@NotNull final String vin) throws OemRuLException {
        try {
            return rulVinRelationRepository.queryByVin(vin);
        } catch (final Exception exception) {
            throw failed("Querying VIN relation failed!", exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public RuLVinRelationDAO getByVinNewTransaction(@NotNull final String vin) throws OemRuLException {
        return getByVinExternalTransaction(vin);
    }

    @RuLTransactionDefaultUseExisting
    public RuLVinRelationDAO getByRefIdExternalTransaction(@NotNull final String refId) throws OemRuLException {
        try {
            return rulVinRelationRepository.queryByRefId(refId);
        } catch (final Exception exception) {
            throw failed("Querying VIN relation failed!", exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public RuLVinRelationDAO getByRefIdNewTransaction(@NotNull final String refId) throws OemRuLException {
        return getByRefIdExternalTransaction(refId);
    }
}
