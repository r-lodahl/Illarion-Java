package illarion.common.config;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public interface ConfigWriter {
    /**
     * Save the current state of the configuration.
     */
    void save();

    /**
     * Remove one entry from the configuration. That causes that the value is not available at all any longer. Only
     * use this function in case you are absolutely sure what you are doing. This causes that not even the default
     * value is available anymore for that session unless its defined by hand again.
     *
     * @param key the key of the entry that is supposed to be removed
     */
    void remove(@Nonnull String key);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a boolean value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, boolean value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a double value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, double value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a path.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, @Nonnull Path value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a float value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, float value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a integer value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, int value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a String value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, @Nonnull String value);
}
