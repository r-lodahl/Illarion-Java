package org.illarion.engine.ui;

public class LoginData {
    public final int serverId;
    public final String server;
    public final String username;
    public final String password;
    public final boolean savePassword;

    public LoginData(int serverId, String server, String username, String password, boolean savePassword) {
        this.serverId = serverId;
        this.server = server;
        this.username = username;
        this.password = password;
        this.savePassword = savePassword;
    }
}
