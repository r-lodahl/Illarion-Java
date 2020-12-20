package illarion.common.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;

public interface ConfigReader {
    /**
     * Get one entry of the configuration file. In this case the value is read as a boolean value.
     *
     * @param key the key of that value
     * @return the value that was read from the configuration or {@code false} in case no value is set
     */
    boolean getBoolean(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a double value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or {@code 0} in case there is no value set
     * for this key
     */
    double getDouble(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a Path value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or {@code null} in case there is no value set
     * for this key
     */
    @Nullable
    Path getPath(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a float value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or {@code 0} in case there is no value set
     * for this key
     */
    float getFloat(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a integer value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or {@code 0} in case there is no value set
     * for this key
     */
    int getInteger(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a String value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or {@code null} in case there is no value set
     * for this key
     */
    @Nullable
    String getString(@Nonnull String key);
}
