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
package illarion.client.util.translation;

import illarion.client.IllaClient;
import illarion.client.util.Lang;
import illarion.client.util.translation.mymemory.MyMemoryProvider;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.config.ConfigReader;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Translator {
    @NotNull
    public static final String CONFIG_KEY_PROVIDER = "translator_provider";
    public static final String CONFIG_KEY_DIRECTION = "translator_direction";

    private static final int CONFIG_VALUE_PROVIDER_MY_MEMORY = 1;
    private static final int CONFIG_VALUE_DIRECTION_EN_DE = 1;
    private static final int CONFIG_VALUE_DIRECTION_DE_EN = 2;

    @NotNull
    private final ExecutorService executorService;
    @Nullable
    private TranslationProvider provider;
    @NotNull
    private TranslationDirection direction;

    public Translator() {
        provider = getConfigProvider(IllaClient.getConfig());
        direction = getConfigDirection(IllaClient.getConfig());
        executorService = Executors.newCachedThreadPool();
        AnnotationProcessor.process(this);
    }

    @Nullable
    private static TranslationProvider getConfigProvider(@NotNull ConfigReader config) {
        return config.getInteger(CONFIG_KEY_PROVIDER) == CONFIG_VALUE_PROVIDER_MY_MEMORY
                ? new MyMemoryProvider()
                : null;
    }

    @NotNull
    @Contract(pure = true)
    private static TranslationDirection getConfigDirection(@NotNull ConfigReader cfg) {
        int value = cfg.getInteger(CONFIG_KEY_DIRECTION);
        return switch (value) {
            case CONFIG_VALUE_DIRECTION_EN_DE -> TranslationDirection.EnglishToGerman;
            case CONFIG_VALUE_DIRECTION_DE_EN -> TranslationDirection.GermanToEnglish;
            default -> Lang.INSTANCE.getLocale().equals(Locale.ENGLISH) ? TranslationDirection.GermanToEnglish :
                    TranslationDirection.EnglishToGerman;
        };
    }

    @EventTopicSubscriber(topic = CONFIG_KEY_PROVIDER)
    private void onConfigProviderChanged(@NotNull String key, @NotNull ConfigChangedEvent event) {
        provider = getConfigProvider(event.getConfig());
    }

    @EventTopicSubscriber(topic = CONFIG_KEY_DIRECTION)
    private void onConfigDirectionChanged(@NotNull String key, @NotNull ConfigChangedEvent event) {
        direction = getConfigDirection(event.getConfig());
    }

    public void translate(@NotNull String original, @NotNull TranslatorCallback callback) {
        if (isServiceEnabled()) {
            assert provider != null;
            executorService.submit(new TranslateTask(executorService, provider, direction, original, callback));
        } else {
            callback.sendTranslation(null);
        }
    }

    public boolean isServiceEnabled() {
        return (provider != null) && provider.isProviderWorking();
    }
}
