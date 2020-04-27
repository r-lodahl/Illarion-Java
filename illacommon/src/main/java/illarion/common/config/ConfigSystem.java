/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.common.config;

import org.bushe.swing.event.EventBus;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This is the main class for the configuration system. It contains the storage for the configuration values and
 * allows to apply changes to those values.
 * <p>
 * This class is fully thread save as the access is synchronized using a read/write lock. So reading access will work
 * mostly without synchronization. How ever in case there are any changes done to the configuration or the
 * configuration is saved or load the other parts of the application can't access the data of this configuration and
 * are blocked until its save to read again.
 * </p>
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ConfigSystem implements Config {
    /**
     * The encoding used to encode the XML configuration files.
     */
    @Nonnull
    private static final String ENCODING = "UTF-8"; //$NON-NLS-1$

    /**
     * The name of the default properties file in the resource bundle.
     */
    @Nonnull
    private static final String DEFAULT_CONFIG_FILENAME = "default-config.properties";

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigSystem.class);

    /**
     * This flag is set to {@code true} in case any changes where applied
     * to the configuration. Only in case those changes got applied the
     * configuration file needs to be saved at all.
     */
    private boolean changed;

    /**
     * The load entries of the configuration file.
     */
    @Nonnull
    private final Properties userProperties;

    /**
     * The file that stores the configuration.
     */
    @Nonnull
    private final Path configFile;

    /**
     * This lock is used to synchronize the access to the configuration system
     * properly. A read write lock is used here because most of the time the
     * configuration will be accessed reading.
     */
    @Nonnull
    private final ReadWriteLock lock;

    /**
     * Create a configuration object with a file as source. The configuration
     * system will try to load the data from this source.
     *
     * @param source The configuration file that is supposed to be load
     */
    public ConfigSystem(@Nonnull Path source) {
        configFile = source;
        changed = false;

        Properties defaultProperties = new Properties();
        defaultProperties.load(ConfigSystem.class.getResourceAsStream(DEFAULT_CONFIG_FILENAME));

        userProperties = new Properties(defaultProperties);
        if (Files.exists(configFile)) {
            userProperties.load(Files.newInputStream(configFile));
        } else {
            changed = true;
        }

        lock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean getBoolean(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return false;
        }

        if (!(value instanceof Boolean)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return false;
        }

        return (Boolean) value;
    }

    @Override
    public double getDouble(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return 0.d;
        }

        if (!(value instanceof Double)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return 0.d;
        }

        return (Double) value;
    }

    @Nullable
    @Override
    public Path getPath(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return null;
        }

        if (!(value instanceof String)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return null;
        }

        return Paths.get(value.toString());
    }

    @Override
    public float getFloat(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return 0.f;
        }

        if (!(value instanceof Float)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return 0.f;
        }

        return (Float) value;
    }

    @Override
    public int getInteger(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return 0;
        }

        if (!(value instanceof Integer)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return 0;
        }

        return (Integer) value;
    }

    @Nullable
    public Object getObject(String key) {
        Object value;
        lock.readLock().lock();
        try {
            value = userProperties.get(key);
        } finally {
            lock.readLock().unlock();
        }

        if (value == null) {
            LOGGER.warn("No config entry found for: {}", key);
            return null;
        }

        return value;
    }

    @Nullable
    @Override
    public String getString(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return null;
        }

        if (!(value instanceof String)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return null;
        }

        return value.toString();
    }

    @Override
    public void remove(@Nonnull String key) {
        userProperties.remove(key);
    }

    private interface ConfigTypeConverter {
        @Nonnull
        String getString(@Nonnull Object object);

        Object getObject(@Nonnull String string);
    }

    private abstract static class AbstractConfigTypeConverter implements ConfigTypeConverter {
        @Nonnull
        @Override
        public final String getString(@Nonnull Object object) {
            return object.toString();
        }
    }

    private enum ConfigTypes {
        BooleanEntry("bool", Boolean.class, new AbstractConfigTypeConverter() {
            @Override
            public Boolean getObject(@Nonnull String string) {
                return Boolean.valueOf(string);
            }
        }),
        ByteEntry("byte", Byte.class, new AbstractConfigTypeConverter() {
            @Override
            public Byte getObject(@Nonnull String string) {
                return Byte.valueOf(string);
            }
        }),
        DoubleEntry("double", Double.class, new AbstractConfigTypeConverter() {
            @Nonnull
            @Override
            public Double getObject(@Nonnull String string) {
                return Double.valueOf(string);
            }
        }),
        FileEntry("file", Path.class, new ConfigTypeConverter() {
            @Nonnull
            @Override
            public String getString(@Nonnull Object object) {
                return ((Path) object).toAbsolutePath().toString();
            }

            @Nonnull
            @Override
            public Path getObject(@Nonnull String string) {
                return Paths.get(string);
            }
        }),
        FloatEntry("float", Float.class, new AbstractConfigTypeConverter() {
            @Nonnull
            @Override
            public Float getObject(@Nonnull String string) {
                return Float.valueOf(string);
            }
        }),
        IntegerEntry("int", Integer.class, new AbstractConfigTypeConverter() {
            @Override
            public Integer getObject(@Nonnull String string) {
                return Integer.valueOf(string);
            }
        }),
        LongEntry("long", Long.class, new AbstractConfigTypeConverter() {
            @Override
            public Long getObject(@Nonnull String string) {
                return Long.valueOf(string);
            }
        }),
        ShortEntry("short", Short.class, new AbstractConfigTypeConverter() {
            @Override
            public Short getObject(@Nonnull String string) {
                return Short.valueOf(string);
            }
        }),
        StringEntry("string", String.class, new AbstractConfigTypeConverter() {
            @Nonnull
            @Override
            public String getObject(@Nonnull String string) {
                return string;
            }
        });

        @Nonnull
        private final String typeName;
        @Nonnull
        private final Class<?> typeClass;
        @Nonnull
        private final ConfigTypeConverter converter;

        ConfigTypes(@Nonnull String typeName, @Nonnull Class<?> typeClass, @Nonnull ConfigTypeConverter converter) {
            this.typeClass = typeClass;
            this.typeName = typeName;
            this.converter = converter;
        }

        @Nonnull
        @Contract(pure = true)
        public String getTypeName() {
            return typeName;
        }

        @Nonnull
        @Contract(pure = true)
        public Class<?> getTypeClass() {
            return typeClass;
        }

        @Nonnull
        @Contract(pure = true)
        public ConfigTypeConverter getConverter() {
            return converter;
        }
    }

    @Override
    public void save() {
        if (!changed) {
            return; // no changes applied
        }

        if (Files.isDirectory(configFile)) {
            LOGGER.warn("Configuration not saved: config file set to illegal value.");
            return;
        }

        lock.writeLock().lock();
        try (OutputStream stream = Files.newOutputStream(configFile)){
            userProperties.store(stream, "Writing user properties file.");
        } catch (@Nonnull IOException e) {
            LOGGER.error("Configuration not saved: error accessing config file.");
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void set(@Nonnull String key, boolean value) {
        set(key, Boolean.valueOf(value));
    }

    @Override
    public void set(@Nonnull String key, double value) {
        set(key, Double.valueOf(value));
    }

    @Override
    public void set(@Nonnull String key, @Nonnull Path value) {
        set(key, value.toAbsolutePath().toString());
    }

    @Override
    public void set(@Nonnull String key, float value) {
        set(key, Float.valueOf(value));
    }

    @Override
    public void set(@Nonnull String key, int value) {
        set(key, Integer.valueOf(value));
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a Object value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void set(@Nonnull String key, @Nonnull Object value) {
        lock.writeLock().lock();
        try {
            if (value.equals(userProperties.get(key))) {
                return;
            }

            userProperties.put(key, value);
            reportChangedKey(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void set(@Nonnull String key, @Nonnull String value) {
        set(key, (Object) value);
    }

    /**
     * Report the change of a entry of the configuration to all listeners set in
     * this configuration.
     *
     * @param key the key that was changed
     */
    private void reportChangedKey(@Nonnull String key) {
        changed = true;

        EventBus.publish(key, new ConfigChangedEvent(this, key));
    }
}
