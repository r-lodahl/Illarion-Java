package illarion.client.util.account.request;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Request<T> {
    @NotNull
    String getRoute();

    @NotNull
    String getMethod();

    @Nullable
    Object getData();

    @NotNull
    Class<T> getResponseClass();
}
