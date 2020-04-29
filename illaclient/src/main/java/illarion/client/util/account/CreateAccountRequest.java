package illarion.client.util.account;

import illarion.client.util.account.request.CreateAccount;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class CreateAccountRequest implements Request {
    @Nonnull
    private final CreateAccount data;

    CreateAccountRequest(@Nonnull CreateAccount data) {
        this.data = data;
    }

    @Nonnull
    @Override
    public String getRoute() {
        return "/account/account";
    }

    @Nonnull
    @Override
    public String getMethod() {
        return "POST";
    }

    @Nullable
    @Override
    public Object getData() {
        return data;
    }

    @Nonnull
    @Override
    public Map<Integer, Class> getResponseMap() {
        Map<Integer, Class> responses = new HashMap<>();
        return responses;
    }
}
