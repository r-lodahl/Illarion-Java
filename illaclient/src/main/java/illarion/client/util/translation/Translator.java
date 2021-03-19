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
import illarion.common.config.Config;
import illarion.common.config.ConfigChangedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class handles the translations that are queried from a server.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Translator {
    @NotNull
    public static final String CFG_KEY_PROVIDER = "translator_provider";
    public static final int CFG_VALUE_PROVIDER_NONE = 0;
    public static final int CFG_VALUE_PROVIDER_MY_MEMORY = 1;
    public static final int CFG_VALUE_PROVIDER_YANDEX = 2;
    public static final String CFG_KEY_DIRECTION = "translator_direction";
    public static final int CFG_VALUE_DIRECTION_DEFAULT = 0;
    public static final int CFG_VALUE_DIRECTION_EN_DE = 1;
    public static final int CFG_VALUE_DIRECTION_DE_EN = 2;
    @NotNull
    private static final Logger log = LogManager.getLogger();
    @NotNull
    private final ExecutorService executorService;
    @Nullable
    private TranslationProvider provider;
    @NotNull
    private TranslationDirection direction;

    public Translator() {
        provider = getCfgProvider(IllaClient.getConfig());
        direction = getCfgDirection(IllaClient.getConfig());
        executorService = Executors.newCachedThreadPool();
        AnnotationProcessor.process(this);
    }

    @Nullable
    private static TranslationProvider getCfgProvider(@NotNull Config cfg) {
        int value = cfg.getInteger(CFG_KEY_PROVIDER);
        switch (value) {
            case CFG_VALUE_PROVIDER_MY_MEMORY:
                return new MyMemoryProvider();
            case CFG_VALUE_PROVIDER_YANDEX:
                // return new YandexProvider(); Currently disabled because the service does not work this way anymore
                cfg.set(CFG_KEY_PROVIDER, CFG_VALUE_PROVIDER_NONE);
                return null;
            case CFG_VALUE_PROVIDER_NONE:
            default:
                return null;
        }
    }

    @NotNull
    @Contract(pure = true)
    private static TranslationDirection getCfgDirection(@NotNull Config cfg) {
        int value = cfg.getInteger(CFG_KEY_DIRECTION);
        switch (value) {
            case CFG_VALUE_DIRECTION_EN_DE:
                return TranslationDirection.EnglishToGerman;
            case CFG_VALUE_DIRECTION_DE_EN:
                return TranslationDirection.GermanToEnglish;
            case CFG_VALUE_DIRECTION_DEFAULT:
            default:
                return Lang.INSTANCE.getLocale().equals(Locale.ENGLISH) ? TranslationDirection.GermanToEnglish :
                        TranslationDirection.EnglishToGerman;
        }
    }

    @EventTopicSubscriber(topic = CFG_KEY_PROVIDER)
    private void onConfigProviderChanged(@NotNull String key, @NotNull ConfigChangedEvent event) {
        provider = getCfgProvider(event.getConfig());
    }

    @EventTopicSubscriber(topic = CFG_KEY_DIRECTION)
    private void onConfigDirectionChanged(@NotNull String key, @NotNull ConfigChangedEvent event) {
        direction = getCfgDirection(event.getConfig());
    }

    public void translate(@NotNull String original, @NotNull TranslatorCallback callback) {
        if (isServiceEnabled()) {
            assert provider != null; // ensured by: isServiceEnabled()
            executorService.submit(new TranslateTask(executorService, provider, direction, original, callback));
        } else {
            callback.sendTranslation(null);
        }
    }

    public boolean isServiceEnabled() {
        return (provider != null) && provider.isProviderWorking();
    }
}
