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

import org.jetbrains.annotations.NotNull;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * This is the authentication provider that can be used to interact with the API of the account system.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class IllarionAuthenticator extends Authenticator {
    /**
     * The user name that is used to authenticate with the account system.
     */
    @NotNull
    private final String username;

    /**
     * The password that is used to authenticate.
     */
    @NotNull
    private final String password;

    /**
     * Create a new instance of the authenticator that provides a password authentication based based on the provided
     * username and password.
     *
     * @param username the user name
     * @param password the password
     */
    IllarionAuthenticator(@NotNull String username, @NotNull String password) {
        this.username = username;
        this.password = password;
    }

    @NotNull
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password.toCharArray());
    }
}
