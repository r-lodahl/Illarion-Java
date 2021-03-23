package illarion.client.util.account.request;

import illarion.client.util.account.form.AccountCreateForm;
import illarion.client.util.account.response.AccountCreateResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountCreateRequest implements Request<AccountCreateResponse> {
    @NotNull
    private final AccountCreateForm data;

    public AccountCreateRequest(@NotNull AccountCreateForm data) {
        this.data = data;
    }

    @NotNull
    @Override
    public String getRoute() {
        return "/account/account";
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
    public Class<AccountCreateResponse> getResponseClass() {
        return AccountCreateResponse.class;
    }
}
