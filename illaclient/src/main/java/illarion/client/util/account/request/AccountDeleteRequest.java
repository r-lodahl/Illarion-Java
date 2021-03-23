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
package illarion.client.util.account.request;

import illarion.client.util.account.IllarionAuthenticator;
import illarion.client.util.account.response.AccountDeleteResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class AccountDeleteRequest implements AuthenticatedRequest<AccountDeleteResponse> {
    @NotNull
    private final IllarionAuthenticator authenticator;

    AccountDeleteRequest(@NotNull IllarionAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @NotNull
    @Override
    public IllarionAuthenticator getAuthenticator() {
        return authenticator;
    }

    @NotNull
    @Override
    public String getRoute() {
        return "/account/account";
    }

    @NotNull
    @Override
    public String getMethod() {
        return "DELETE";
    }

    @Nullable
    @Override
    public Object getData() {
        return null;
    }

    @NotNull
    @Override
    public Class<AccountDeleteResponse> getResponseClass() {
        return AccountDeleteResponse.class;
    }
}
