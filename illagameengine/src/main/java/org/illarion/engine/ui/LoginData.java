package org.illarion.engine.ui;


import org.jetbrains.annotations.NotNull;

public class LoginData {
    public final int serverId;
    public final String server;
    public final String username;
    public final String password;
    public final boolean savePassword;

    public LoginData(int serverId, @NotNull String server, @NotNull String username, @NotNull String password, boolean savePassword) {
        this.serverId = serverId;
        this.server = server;
        this.username = username;
        this.password = password;
        this.savePassword = savePassword;
    }
}
