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
import illarion.client.util.account.form.CharacterCreateForm;
import illarion.client.util.account.response.CharacterCreateResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class CharacterCreateRequest implements AuthenticatedRequest<CharacterCreateResponse> {
    @NotNull
    private final IllarionAuthenticator authenticator;
    @NotNull
    private final String serverId;
    @NotNull
    private final CharacterCreateForm data;

    CharacterCreateRequest(@NotNull IllarionAuthenticator authenticator,
                           @NotNull String serverId,
                           @NotNull CharacterCreateForm data) {
        this.authenticator = authenticator;
        this.serverId = serverId;
        this.data = data;
    }

    @NotNull
    @Override
    public IllarionAuthenticator getAuthenticator() {
        return authenticator;
    }

    @NotNull
    @Override
    public String getRoute() {
        return "/account/character/" + serverId;
    }

    @NotNull
    @Override
    public String getMethod() {
        return "POST";
    }

    @Nullable
    @Override
    public Object getData() {
        return data;
    }

    @NotNull
    @Override
    public Class<CharacterCreateResponse> getResponseClass() {
        return CharacterCreateResponse.class;
    }
}
