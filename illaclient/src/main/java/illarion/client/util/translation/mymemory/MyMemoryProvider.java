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
package illarion.client.util.translation.mymemory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import illarion.client.util.translation.TranslationDirection;
import illarion.client.util.translation.TranslationProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MyMemoryProvider implements TranslationProvider {
    @NotNull
    private static final Logger log = LogManager.getLogger();

    @Nullable
    private final URL serviceUrl;

    private boolean operational;

    public MyMemoryProvider() {
        URL url = null;
        try {
            url = new URL("http://api.mymemory.translated.net/get");
        } catch (MalformedURLException e) {
            log.error("Failed to resolve the URL to the translator service. Service is not active.", e);
        }
        serviceUrl = url;
        operational = true;
    }

    @Nullable
    @Override
    public String getTranslation(@NotNull String original, @NotNull TranslationDirection direction) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(serviceUrl).append('?');
        try {
            queryBuilder.append("q=").append(URLEncoder.encode(original, StandardCharsets.UTF_8));
            queryBuilder.append('&').append("langpair=").append(getLangPair(direction));
            queryBuilder.append('&').append("of=json");
            queryBuilder.append('&').append("de=").append(URLEncoder.encode("webmaster@illarion.org", StandardCharsets.UTF_8));

            URL queryUrl = new URL(queryBuilder.toString());
            try (JsonReader rd = new JsonReader(new InputStreamReader(queryUrl.openStream(), StandardCharsets.UTF_8))) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                Response response = gson.fromJson(rd, Response.class);
                if ((response != null) && (response.getResponseData() != null) &&
                        (response.getResponseData().getMatch() > 0.75)) {
                    return response.getResponseData().getTranslatedText();
                }
            } catch (IOException e) {
                log.error("Error while reading from the service.", e);
            } catch (JsonParseException e) {
                log.error("Unexpected error while decoding json", e);
            }
        } catch (MalformedURLException e) {
            log.error("Generated URL for the query to MyMemory appears to have a invalid format.", e);
        }

        // The provider is not working anymore. That either happens because the provider is unreachable or because
        // the provider is not accepting any more requests. Either way to reduce overhead the provider may shut down for
        // this session to reduce the overhead.
        operational = false;
        return null;
    }

    @NotNull
    @Contract(pure = true)
    private static String getLangPair(@NotNull TranslationDirection direction) {
        switch (direction) {
            case GermanToEnglish:
                return "de|en";
            case EnglishToGerman:
                return "en|de";
        }
        throw new UnsupportedOperationException("Unexpected translation direction received.");
    }

    @Override
    public boolean isProviderWorking() {
        return (serviceUrl != null) && operational;
    }
}
