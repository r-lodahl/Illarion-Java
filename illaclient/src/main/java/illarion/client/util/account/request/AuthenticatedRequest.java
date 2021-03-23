package illarion.client.util.account.request;

import org.jetbrains.annotations.NotNull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface AuthenticatedRequest<T> extends Request<T> {
    @NotNull
    java.net.Authenticator getAuthenticator();
}
