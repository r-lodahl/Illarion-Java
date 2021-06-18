package org.illarion.engine.ui.login;

import org.jetbrains.annotations.NotNull;

public record LoginData(int serverId, String server, String username, String password, boolean savePassword) {
    public LoginData(int serverId,
                     @NotNull String server,
                     @NotNull String username,
                     @NotNull String password,
                     boolean savePassword) {
        this.serverId = serverId;
        this.server = server;
        this.username = username;
        this.password = password;
        this.savePassword = savePassword;
    }
}
