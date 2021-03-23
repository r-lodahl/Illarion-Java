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
import illarion.client.util.account.form.CharacterUpdateForm;
import illarion.client.util.account.response.CharacterUpdateResponse;
import illarion.common.types.CharacterId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class CharacterUpdateRequest implements AuthenticatedRequest<CharacterUpdateResponse> {
    @NotNull
    private final IllarionAuthenticator authenticator;
    @NotNull
    private final String serverId;
    @NotNull
    private final CharacterId charId;
    @NotNull
    private final CharacterUpdateForm data;

    CharacterUpdateRequest(@NotNull IllarionAuthenticator authenticator,
                           @NotNull String serverId,
                           @NotNull CharacterId charId,
                           @NotNull CharacterUpdateForm data) {
        this.authenticator = authenticator;
        this.serverId = serverId;
        this.charId = charId;
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
        return "/account/character/" + serverId + '/' + charId.getValue();
    }

    @NotNull
    @Override
    public String getMethod() {
        return "PUT";
    }

    @Nullable
    @Override
    public Object getData() {
        return data;
    }

    @NotNull
    @Override
    public Class<CharacterUpdateResponse> getResponseClass() {
        return CharacterUpdateResponse.class;
    }
}
