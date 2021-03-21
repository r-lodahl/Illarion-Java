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
package illarion.easynpc;

import illarion.common.util.MessageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Localized text handler. Loads the localized messages and returns them if
 * requested, regarding the language settings of the client.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Lang implements MessageSource {
    /**
     * The singleton instance of this class.
     */
    private static final Lang INSTANCE = new Lang();

    /**
     * The logger instance that handles the log output of this class.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The file name of the message bundles the client loads for the language.
     */
    private static final String MESSAGE_BUNDLE = "messages";
    /**
     * The storage of the localized messages. Holds the key for the string and
     * the localized full message.
     */
    private final ResourceBundle messages;
    /**
     * The current local settings.
     */
    private Locale locale;

    /**
     * Constructor of the game. Triggers the messages to load.
     */
    private Lang() {
        locale = Locale.getDefault();
        locale = locale.getLanguage().equalsIgnoreCase(Locale.GERMAN.getLanguage()) ? Locale.GERMAN : Locale.ENGLISH;

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        messages = ResourceBundle.getBundle(MESSAGE_BUNDLE, locale, cl);
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the instance of the class
     */
    @NotNull
    public static MessageSource getInstance() {
        return INSTANCE;
    }

    /**
     * Get a localized message from a key.
     *
     * @param clazz The class that is accessing this text
     * @param key The key of the localized message
     * @return the localized message or the key with surrounding < > in case the
     * key was not found in the storage
     */
    @NotNull
    public static String getMsg(@NotNull Class<?> clazz, String key) {
        return getMsg(clazz.getName() + '.' + key);
    }

    /**
     * Get the localized message from a key.
     *
     * @param key The key of the localized message
     * @return the localized message or the key with surrounding &lt; &gt; in
     * case the key was not found in the storage
     */
    public static String getMsg(@NotNull String key) {
        return INSTANCE.getMessage(key);
    }

    /**
     * Get the current local settings.
     *
     * @return the local object of the chosen local settings
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Get a localized message from a key.
     *
     * @param key The key of the localized message
     * @return the localized message or the key with surrounding &lt; &gt; in
     * case the key was not found in the storage
     */
    @NotNull
    @Override
    public String getMessage(@NotNull String key) {
        try {
            return messages.getString(key);
        } catch (MissingResourceException e) {
            LOGGER.warn("Failed searching translated version of: {}", key);
            return '<' + key + '>';
        }
    }

    /**
     * Check if a key contains a message.
     *
     * @param key the key that shall be checked
     * @return true in case a message was found
     */
    public boolean hasMsg(@NotNull String key) {
        try {
            messages.getString(key);
        } catch (MissingResourceException e) {
            return false;
        }
        return true;
    }

    /**
     * Check if the client is currently running with the English language.
     *
     * @return true if the language is set to English
     */
    public boolean isEnglish() {
        return Locale.ENGLISH.equals(locale);
    }

    /**
     * Check if the client is currently running with the German language.
     *
     * @return true if the language is set to German
     */
    public boolean isGerman() {
        return Locale.GERMAN.equals(locale);
    }
}
