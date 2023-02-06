package net.catena_x.btp.rul.oem.backend.database.rul.tables.serviceoption;

import net.catena_x.btp.rul.oem.backend.database.rul.annotations.RuLTransactionDefaultCreateNew;
import net.catena_x.btp.rul.oem.backend.database.rul.annotations.RuLTransactionDefaultUseExisting;
import net.catena_x.btp.rul.oem.backend.database.rul.annotations.RuLTransactionSerializableCreateNew;
import net.catena_x.btp.rul.oem.backend.database.rul.annotations.RuLTransactionSerializableUseExisting;
import net.catena_x.btp.rul.oem.backend.database.rul.base.RuLTableBase;
import net.catena_x.btp.rul.oem.backend.model.enums.RuLServiceOptionType;
import net.catena_x.btp.rul.oem.util.exceptions.OemRuLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.function.Supplier;

@Component
public class RuLServiceOptionTableInternal extends RuLTableBase {
    @Autowired private RuLServiceOptionRepository rulServiceOptionRepository;

    private final Logger logger = LoggerFactory.getLogger(RuLServiceOptionTableInternal.class);

    @RuLTransactionSerializableUseExisting
    public Exception runSerializableExternalTransaction(@NotNull final Supplier<Exception> function) {
        return function.get();
    }

    @RuLTransactionSerializableCreateNew
    public Exception runSerializableNewTransaction(@NotNull final Supplier<Exception> function) {
        return runSerializableExternalTransaction(function);
    }

    @RuLTransactionDefaultUseExisting
    public void setInfoItemExternalTransaction(@NotNull final RuLServiceOptionType key, @NotNull final String value)
            throws OemRuLException {
        try {
            rulServiceOptionRepository.insert(key.toString(), value);
        } catch (final Exception exception) {
            logger.error(exception.getMessage());
            exception.printStackTrace();
            throw failed("Inserting info value failed! " + exception.getMessage(), exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public void setInfoItemNewTransaction(@NotNull final RuLServiceOptionType key, @NotNull final String value)
            throws OemRuLException {
        setInfoItemExternalTransaction(key, value);
    }

    @RuLTransactionDefaultUseExisting
    public void updateInfoItemExternalTransaction(@NotNull final RuLServiceOptionType key, @NotNull final String value)
            throws OemRuLException {
        try {
            rulServiceOptionRepository.update(key.toString(), value);
        } catch (final Exception exception) {
            logger.error(exception.getMessage());
            exception.printStackTrace();
            throw failed("Updating info value failed! " + exception.getMessage(), exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public void updateInfoItemNewTransaction(@NotNull final RuLServiceOptionType key, @NotNull final String value)
            throws OemRuLException {
        updateInfoItemExternalTransaction(key, value);
    }

    @RuLTransactionDefaultUseExisting
    public RuLServiceOptionDAO getInfoItemExternalTransaction(@NotNull final RuLServiceOptionType key) throws OemRuLException {
        try {
            return rulServiceOptionRepository.queryByKey(key.toString());
        } catch (final Exception exception) {
            logger.error(exception.getMessage());
            exception.printStackTrace();
            throw failed("Reading info item failed! " + exception.getMessage(), exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public RuLServiceOptionDAO getInfoItemNewTransaction(@NotNull final RuLServiceOptionType key) throws OemRuLException {
        return getInfoItemExternalTransaction(key);
    }

    @RuLTransactionDefaultUseExisting
    public List<RuLServiceOptionDAO> getAllExternalTransaction() throws OemRuLException {
        try {
            return rulServiceOptionRepository.queryAll();
        } catch (final Exception exception) {
            logger.error(exception.getMessage());
            exception.printStackTrace();
            throw failed("Reading all info items failed! " + exception.getMessage(), exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public List<RuLServiceOptionDAO> getAllNewTransaction() throws OemRuLException {
        return getAllExternalTransaction();
    }

    @RuLTransactionDefaultUseExisting
    public void deleteAllExternalTransaction() throws OemRuLException {
        try {
            rulServiceOptionRepository.deleteAll();
        } catch (final Exception exception) {
            logger.error(exception.getMessage());
            exception.printStackTrace();
            throw failed("Deleting all info items failed! " + exception.getMessage(), exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public void deleteAllNewTransaction() throws OemRuLException {
        deleteAllExternalTransaction();
    }

    @RuLTransactionDefaultUseExisting
    public void deleteExternalTransaction(@NotNull final RuLServiceOptionType key) throws OemRuLException {
        try {
            rulServiceOptionRepository.delete(key.toString());
        } catch (final Exception exception) {
            logger.error(exception.getMessage());
            exception.printStackTrace();
            throw failed("Deleting info item failed! " + exception.getMessage(), exception);
        }
    }

    @RuLTransactionDefaultCreateNew
    public void deleteNewTransaction(@NotNull final RuLServiceOptionType key) throws OemRuLException {
        deleteExternalTransaction(key);
    }

    @RuLTransactionDefaultUseExisting
    public String getInfoValueExternalTransaction(@NotNull final RuLServiceOptionType key) throws OemRuLException {
        return getInfoItemExternalTransaction(key).getValue();
    }

    @RuLTransactionDefaultCreateNew
    public String getInfoValueNewTransaction(@NotNull final RuLServiceOptionType key) throws OemRuLException {
        return getInfoValueExternalTransaction(key);
    }
}
