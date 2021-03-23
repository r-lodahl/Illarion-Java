package illarion.client.util.account.request;

import illarion.client.util.account.form.AccountCheckForm;
import illarion.client.util.account.response.AccountCheckResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountCheckRequest implements Request<AccountCheckResponse> {
    @NotNull
    private final AccountCheckForm data;

    public AccountCheckRequest(@NotNull AccountCheckForm data) {
        this.data = data;
    }

    @NotNull
    @Override
    public String getRoute() {
        return "/account/account/check";
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
    public Class<AccountCheckResponse> getResponseClass() {
        return AccountCheckResponse.class;
    }
}
