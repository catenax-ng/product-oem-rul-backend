package net.catena_x.btp.rul.oem.backend.model.dto.serviceoption;

import net.catena_x.btp.rul.oem.backend.database.rul.tables.serviceoption.RuLServiceOptionTableInternal;
import net.catena_x.btp.rul.oem.backend.model.enums.RuLServiceOptionType;
import net.catena_x.btp.rul.oem.util.exceptions.OemRuLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.function.Supplier;

@Component
public class RuLServiceOptionTable {
    @Autowired private RuLServiceOptionTableInternal internal;
    @Autowired private RuLServiceOptionConverter rulServiceOptionConverter;

    public Exception runSerializableNewTransaction(@NotNull final Supplier<Exception> function) {
        return internal.runSerializableNewTransaction(function);
    }

    public Exception runSerializableExternalTransaction(@NotNull final Supplier<Exception> function) {
        return internal.runSerializableExternalTransaction(function);
    }

    public void setInfoItemNewTransaction(@NotNull final RuLServiceOptionType key, @NotNull final String value)
            throws OemRuLException {
        internal.setInfoItemNewTransaction(key, value);
    }

    public void setInfoItemExternalTransaction(@NotNull final RuLServiceOptionType key, @NotNull final String value)
            throws OemRuLException {
        internal.setInfoItemExternalTransaction(key, value);
    }

    public void updateInfoItemNewTransaction(@NotNull final RuLServiceOptionType key, @NotNull final String value)
            throws OemRuLException {
        internal.updateInfoItemNewTransaction(key, value);
    }

    public void updateInfoItemExternalTransaction(@NotNull final RuLServiceOptionType key, @NotNull final String value)
            throws OemRuLException {
        internal.updateInfoItemExternalTransaction(key, value);
    }

    public void deleteAllNewTransaction() throws OemRuLException {
        internal.deleteAllNewTransaction();
    }

    public void deleteAllExternalTransaction() throws OemRuLException {
        internal.deleteAllExternalTransaction();
    }

    public void deleteNewTransaction(@NotNull final RuLServiceOptionType key) throws OemRuLException {
        internal.deleteNewTransaction(key);
    }

    public void deleteExternalTransaction(@NotNull final RuLServiceOptionType key) throws OemRuLException {
        internal.deleteExternalTransaction(key);
    }

    public String getInfoValueNewTransaction(@NotNull final RuLServiceOptionType key) throws OemRuLException {
        return internal.getInfoValueNewTransaction(key);
    }

    public String getInfoValueExternalTransaction(@NotNull final RuLServiceOptionType key) throws OemRuLException {
        return internal.getInfoValueExternalTransaction(key);
    }

    public RuLServiceOption getInfoItemNewTransaction(@NotNull final RuLServiceOptionType key)
            throws OemRuLException {
        return rulServiceOptionConverter.toDTO(internal.getInfoItemNewTransaction(key));
    }

    public RuLServiceOption getInfoItemExternalTransaction(@NotNull final RuLServiceOptionType key)
            throws OemRuLException {
        return rulServiceOptionConverter.toDTO(internal.getInfoItemExternalTransaction(key));
    }

    public List<RuLServiceOption> getAllNewTransaction() throws OemRuLException {
        return rulServiceOptionConverter.toDTO(internal.getAllNewTransaction());
    }

    public List<RuLServiceOption> getAllExternalTransaction() throws OemRuLException {
        return rulServiceOptionConverter.toDTO(internal.getAllExternalTransaction());
    }
}
