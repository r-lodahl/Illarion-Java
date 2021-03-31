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
import illarion.client.util.account.form.AccountCheckForm;
import illarion.client.util.account.form.AccountCreateForm;
import illarion.client.util.account.form.CharacterCreateForm;
import illarion.client.util.account.request.*;
import illarion.client.util.account.response.*;
import illarion.common.types.CharacterId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.ui.LoginData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AccountSystem implements AutoCloseable {
    private final static Logger LOGGER = LogManager.getLogger();

    public final static String OFFICIAL_ENDPOINT = "https://illarion.org/app.php";

    private final Object requestHandlerLock = new Object();

    @Nullable
    private String endpoint;

    @Nullable
    private IllarionAuthenticator authenticator;

    @Nullable
    @SuppressWarnings("FieldAccessedSynchronizedAndUnsynchronized")
    private RequestHandler requestHandler;

    /**
     * Set the authentication that is used for the interaction with the account system.
     *
     * @param loginData the authentication credentials
     * @throws IllegalStateException in case the authentication is already set
     */
    public void setAuthentication(@NotNull LoginData loginData) {
        authenticator = new IllarionAuthenticator(loginData.username, loginData.password);
    }

    public void setEndpoint(String customEndpoint) {
        if (customEndpoint.equals(endpoint)) {
            return;
        }

        try {
            closeRequestHandler();
        } catch (Exception e) {
            LOGGER.debug("Failed to close AccountSystem RequestHandler");
        }

        endpoint = customEndpoint;
    }

    @NotNull
    private RequestHandler getRequestHandler() {
        if (endpoint == null || endpoint.isEmpty()) {
            throw new IllegalStateException("Communicating with the account system is not possible until the endpoint is set.");
        }

        if (requestHandler != null) {
            return requestHandler;
        }

        synchronized (requestHandlerLock) {
            if (requestHandler == null) {
                requestHandler = new RequestHandler(endpoint);
            }
        }

        return requestHandler;
    }

    @NotNull
    private IllarionAuthenticator getAuthenticator() {
        IllarionAuthenticator authenticator = this.authenticator;
        if (authenticator == null) {
            throw new IllegalStateException("The request requires authentication credentials.");
        }
        return authenticator;
    }

    @NotNull
    public ListenableFuture<AccountGetResponse> getAccountInformation() {
        RequestHandler handler = getRequestHandler();
        IllarionAuthenticator authenticator = getAuthenticator();

        Request<AccountGetResponse> request = new AccountGetRequest(authenticator);

        return handler.sendRequestAsync(request);
    }

    @NotNull
    public ListenableFuture<CharacterGetResponse> getCharacterInformation(@NotNull String serverId,
                                                                          @NotNull CharacterId id) {
        RequestHandler handler = getRequestHandler();
        IllarionAuthenticator authenticator = getAuthenticator();

        Request<CharacterGetResponse> request = new CharacterGetRequest(authenticator, serverId, id);

        return handler.sendRequestAsync(request);
    }

    @NotNull
    public ListenableFuture<AccountCheckResponse> performAccountCredentialsCheck(@Nullable String username,
                                                                                 @Nullable String email) {
        RequestHandler handler = getRequestHandler();

        AccountCheckForm payload = new AccountCheckForm(username, email);
        Request<AccountCheckResponse> request = new AccountCheckRequest(payload);

        return handler.sendRequestAsync(request);
    }

    @NotNull
    public ListenableFuture<AccountCreateResponse> createAccount(@NotNull String username,
                                                                 @Nullable String email,
                                                                 @NotNull String password) {
        RequestHandler handler = getRequestHandler();

        AccountCreateForm payload = new AccountCreateForm(username, password, email);
        Request<AccountCreateResponse> request = new AccountCreateRequest(payload);

        return handler.sendRequestAsync(request);
    }

    @NotNull
    public ListenableFuture<CharacterCreateGetResponse> getPossibleCharacterCreationSpecifications(@NotNull String serverId) {
        RequestHandler handler = getRequestHandler();
        IllarionAuthenticator authenticator = getAuthenticator();

        Request<CharacterCreateGetResponse> request = new CharacterCreateGetRequest(authenticator, serverId);

        return handler.sendRequestAsync(request);
    }

    @NotNull
    public ListenableFuture<CharacterCreateResponse> createCharacter(@NotNull String serverId,
                                                                     @NotNull CharacterCreateForm characterCreateForm) {
        var requestHandler = getRequestHandler();
        var authenticator = getAuthenticator();

        var request = new CharacterCreateRequest(authenticator, serverId, characterCreateForm);

        return requestHandler.sendRequestAsync(request);
    }

    private void closeRequestHandler() throws Exception {
        RequestHandler handler = requestHandler;
        requestHandler = null;
        if (handler != null) {
            handler.close();
        }
    }

    @Override
    public void close() throws Exception {
        closeRequestHandler();
    }
}
