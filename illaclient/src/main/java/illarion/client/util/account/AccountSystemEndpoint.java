package illarion.client.util.account;

import org.jetbrains.annotations.NotNull;

public class AccountSystemEndpoint {
    @NotNull public final String url;
    @NotNull public final String name;
    @NotNull public final String configPasswordKey;
    @NotNull public final String configUsernameKey;
    @NotNull public final String configStorePasswordKey;

    public AccountSystemEndpoint(@NotNull String url,
                                 @NotNull String name,
                                 @NotNull String configUsernameKey,
                                 @NotNull String configPasswordKey,
                                 @NotNull String configStorePasswordKey) {
        this.url = url;
        this.name = name;
        this.configPasswordKey = configPasswordKey;
        this.configUsernameKey = configUsernameKey;
        this.configStorePasswordKey = configStorePasswordKey;
    }
}
