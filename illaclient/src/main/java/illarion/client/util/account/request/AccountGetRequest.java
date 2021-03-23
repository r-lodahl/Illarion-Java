package illarion.client.util.account.request;

import illarion.client.util.account.response.AccountGetResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.Authenticator;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountGetRequest implements AuthenticatedRequest<AccountGetResponse> {
    @NotNull
    private final Authenticator authenticator;

    public AccountGetRequest(@NotNull Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @NotNull
    @Override
    public Authenticator getAuthenticator() {
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
        return "GET";
    }

    @Nullable
    @Override
    public Object getData() {
        return null;
    }

    @NotNull
    @Override
    public Class<AccountGetResponse> getResponseClass() {
        return AccountGetResponse.class;
    }
}
