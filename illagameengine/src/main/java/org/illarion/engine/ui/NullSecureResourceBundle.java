package org.illarion.engine.ui;

import javax.annotation.Nonnull;

public interface NullSecureResourceBundle {
    @Nonnull String getLocalizedString(String key);
}
