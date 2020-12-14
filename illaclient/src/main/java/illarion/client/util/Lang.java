/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.util;

import com.google.common.eventbus.Subscribe;
import illarion.client.gui.events.LocalizationChangedEvent;
import illarion.common.config.ConfigChangedEvent;
import org.illarion.engine.EventBus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.*;

/**
 * Localized text handler. Loads the localized messages and returns them if
 * requested, regarding the language settings of the client.
 */
public enum Lang {
    INSTANCE;

    /**
     * The logger instance that handles the log output of this class.
     */
    @Nonnull private static final Logger log = LoggerFactory.getLogger(Lang.class);

    /**
     * The string that is the key of the language settings in the configuration
     * file.
     */
    @Nonnull public final String CONFIG_KEY_LOCALIZATION = "locale";

    @Nonnull private final Set<Locale> validLocales = Set.of(Locale.ENGLISH, Locale.GERMAN);

    @Nonnull private final Locale defaultLocale = Locale.ENGLISH;

    /**
     * The current local settings.
     */
    private Locale currentLocale;

    @Nonnull private final SecureResourceBundle messagesResourceBundle;
    @Nonnull private final SecureResourceBundle loginResourceBundle;
    @Nonnull private final SecureResourceBundle ingameResourceBundle;

    Lang() {
        messagesResourceBundle = new SecureResourceBundle();
        loginResourceBundle = new SecureResourceBundle();
        ingameResourceBundle = new SecureResourceBundle();
        EventBus.INSTANCE.register(this);
    }

    public @NotNull SecureResourceBundle getMessagesResourceBundle() {
        return messagesResourceBundle;
    }

    public @NotNull SecureResourceBundle getLoginResourceBundle() {
        return loginResourceBundle;
    }

    public @NotNull SecureResourceBundle getIngameResourceBundle() {
        return ingameResourceBundle;
    }

    public void setLocale(@Nonnull Locale locale) {
        if (locale.equals(currentLocale)) {
            return;
        }

        currentLocale = locale;

        if (!validLocales.contains(locale)) {
            log.warn("Tried to load invalid locale %s, using default locale as a fallback".formatted(locale));
            currentLocale = defaultLocale;
        }

        messagesResourceBundle.setResourceBundle(ResourceBundle.getBundle("messages", currentLocale, Lang.class.getClassLoader()));
        loginResourceBundle.setResourceBundle(ResourceBundle.getBundle("login", currentLocale, Lang.class.getClassLoader()));
        ingameResourceBundle.setResourceBundle(ResourceBundle.getBundle("ingame", currentLocale, Lang.class.getClassLoader()));
    }

    @Nonnull
    public Locale getLocale() {
        if (currentLocale == null) {
            log.warn("getLocale() was accessed before locale was set, returning defaultLocale.");
            return defaultLocale;
        }

        return defaultLocale;
    }

    /**
     *
     * Check if the language settings are still correct and reload the messages if needed.
     */
    @Subscribe
    public void onOptionsChanged(@Nonnull ConfigChangedEvent event) {
        if (!event.getKey().equals(CONFIG_KEY_LOCALIZATION)) {
            return;
        }

        Locale locale = new Locale(event.getKey());
        setLocale(locale);

        EventBus.INSTANCE.post(new LocalizationChangedEvent());
    }
}
