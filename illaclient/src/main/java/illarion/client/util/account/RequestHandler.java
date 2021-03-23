/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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
package illarion.client.util.account;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import illarion.client.util.Lang;
import illarion.client.util.account.request.AuthenticatedRequest;
import illarion.client.util.account.request.Request;
import illarion.common.types.CharacterId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Period;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

class RequestHandler implements AutoCloseable {
    @NotNull
    private final String rootUrl;

    @NotNull
    private final ListeningExecutorService executorService;

    @NotNull
    private final Gson gson;

    @NotNull
    private final Charset utf8;

    RequestHandler(@NotNull String rootUrl) {
        this.rootUrl = rootUrl;
        executorService = MoreExecutors.listeningDecorator(
                Executors.newCachedThreadPool(new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .setNameFormat("AccountSystem-REST-Thread %d")
                        .build()));

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(Period.class, new PeriodTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(CharacterId.class, new CharacterIdTypeAdapter())
                .create();

        utf8 = StandardCharsets.UTF_8;
    }

    @NotNull
    private static String buildRequestUrl(@NotNull String root, @NotNull String route) {
        String fixedRoot;
        if (root.endsWith("/")) {
            fixedRoot = root.substring(0, root.length() - 1);
        } else {
            fixedRoot = root;
        }

        String fullRoute;
        if (route.startsWith("/")) {
            fullRoute = fixedRoot + route;
        } else {
            fullRoute = fixedRoot + '/' + route;
        }
        fullRoute += ".json";
        return fullRoute;
    }

    @NotNull
    private static InputStream getInputStream(@NotNull HttpURLConnection connection) {
        try {
            return connection.getInputStream();
        } catch (Throwable t) {
            InputStream stream = connection.getErrorStream();
            return (stream == null) ? new NullInputStream() : stream;
        }
    }

    @NotNull
    public <T> ListenableFuture<T> sendRequestAsync(@NotNull Request<T> request) {
        return executorService.submit(() -> sendRequest(request));
    }

    @Nullable
    public <T> T sendRequest(@NotNull Request<T> request) throws IOException {
        if (request instanceof AuthenticatedRequest) {
            java.net.Authenticator.setDefault(((AuthenticatedRequest<T>) request).getAuthenticator());
        }

        URL requestUrl = new URL(buildRequestUrl(rootUrl, request.getRoute()));
        URLConnection urlConnection = requestUrl.openConnection();

        if (!(urlConnection instanceof HttpURLConnection)) {
            throw new IllegalStateException("Request did not create the expected http connection.");
        }

        HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;

        /*if (httpConnection instanceof HttpsURLConnection) {
            SSLSocketFactory factory = IllarionSSLSocketFactory.getFactory();
            if (factory != null) {
                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(factory);
            }
        }*/

        Object payload = request.getData();
        if (payload != null) {
            httpConnection.setDoOutput(true);
        }

        httpConnection.setRequestMethod(request.getMethod());
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        httpConnection.setRequestProperty("Accept-Language", Lang.INSTANCE.getLocale().equals(Locale.GERMAN)
                ? "de-DE,de"
                : "en-US,en-GB,en");

        if (payload != null) {
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            try (JsonWriter wr = new JsonWriter(new OutputStreamWriter(httpConnection.getOutputStream(), utf8))) {
                gson.toJson(payload, payload.getClass(), wr);
                wr.flush();
            }
        }

        var responseCode = httpConnection.getResponseCode();

        if ((responseCode / 100) == 5) {
            throw new IllegalStateException("Server responded with a server error.");
        }

        Class<T> responseClass = request.getResponseClass();

        try (InputStream in = getInputStream(httpConnection)) {
            InputStream usedIn = in;
            String contentEncoding = httpConnection.getHeaderField("Content-Encoding");
            if (contentEncoding != null) {
                switch (contentEncoding) {
                    case "gzip":
                        usedIn = new GZIPInputStream(usedIn);
                        break;
                    case "deflate":
                        usedIn = new DeflaterInputStream(usedIn);
                        break;
                    default:
                        break;
                }
            }

            try (JsonReader rd = new JsonReader(new InputStreamReader(usedIn, utf8))) {
                return gson.fromJson(rd, responseClass);
            }
        }
    }

    @Override
    public void close() throws Exception {
        executorService.shutdown();
    }

    private static class NullInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            return -1;
        }
    }
}
