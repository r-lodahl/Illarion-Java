package illarion.client.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.ui.NullSecureResourceBundle;
import org.jetbrains.annotations.NotNull;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SecureResourceBundle implements NullSecureResourceBundle {
    @NotNull private static final Logger log = LogManager.getLogger();

    private ResourceBundle resourceBundle;

    void setResourceBundle(@NotNull ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    @NotNull
    public String getLocalizedString(@NotNull String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException | NullPointerException e) {
            log.warn("Failed getting localized version of: {}. Reason: {}", key, e.getMessage());
            return '<' + key + '>';
        }
    }
}
