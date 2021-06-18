package org.illarion.engine.ui.login;

import org.jetbrains.annotations.NotNull;

public record AccountCreationData(@NotNull String name, @NotNull String email, @NotNull String password) {
    public AccountCreationData(@NotNull String name, @NotNull String email, @NotNull String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
