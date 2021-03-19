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
package illarion.easyquest;

import illarion.common.util.MessageSource;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Lang implements MessageSource {
    /**
     * The singleton instance of this class.
     */
    @NotNull
    private static final Lang INSTANCE = new Lang();

    /**
     * The file name of the message bundles the client loads for the language.
     */
    @NotNull
    private static final String MESSAGE_BUNDLE = "easyquest_messages";
    /**
     * The storage of the localized messages. Holds the key for the string and
     * the localized full message.
     */
    @NotNull
    private final ResourceBundle messages;
    /**
     * The current local settings.
     */
    @NotNull
    private Locale locale;

    /**
     * Constructor of the game. Triggers the messages to load.
     */
    private Lang() {
        locale = Locale.getDefault();
        if (locale.getLanguage().equalsIgnoreCase(Locale.GERMAN.getLanguage())) {
            locale = Locale.GERMAN;
        } else {
            locale = Locale.ENGLISH;
        }

        messages = ResourceBundle.getBundle(MESSAGE_BUNDLE, locale, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the instance of the class
     */
    @NotNull
    public static Lang getInstance() {
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
    public static String getMsg(@NotNull Class<?> clazz, @NotNull String key) {
        return getMsg(clazz.getName() + '.' + key);
    }

    /**
     * Get the localized message from a key.
     *
     * @param key The key of the localized message
     * @return the localized message or the key with surrounding &lt; &gt; in
     * case the key was not found in the storage
     */
    @NotNull
    public static String getMsg(@NotNull String key) {
        return INSTANCE.getMessage(key);
    }

    /**
     * Get the current local settings.
     *
     * @return the local object of the chosen local settings
     */
    @NotNull
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
    @Override
    @NotNull
    public String getMessage(@NotNull String key) {
        try {
            return messages.getString(key);
        } catch (@NotNull MissingResourceException e) {
            System.out.println("Failed searching translated version of: " + key);
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
        } catch (@NotNull MissingResourceException e) {
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
        return locale == Locale.ENGLISH;
    }

    /**
     * Check if the client is currently running with the German language.
     *
     * @return true if the language is set to German
     */
    public boolean isGerman() {
        return locale == Locale.GERMAN;
    }
}
