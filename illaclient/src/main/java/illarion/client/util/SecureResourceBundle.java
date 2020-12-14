package illarion.client.util;

import org.illarion.engine.ui.NullSecureResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SecureResourceBundle implements NullSecureResourceBundle {
    @Nonnull private static final Logger log = LoggerFactory.getLogger(Lang.class);

    private ResourceBundle resourceBundle;

    void setResourceBundle(@Nonnull ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    @Nonnull
    public String getLocalizedString(@Nonnull String key) {
        try {
            return resourceBundle.getString(key);
        } catch (@Nonnull MissingResourceException | NullPointerException e) {
            log.warn("Failed getting localized version of: {}. Reason: {}", key, e.getMessage());
            return '<' + key + '>';
        }
    }
}
