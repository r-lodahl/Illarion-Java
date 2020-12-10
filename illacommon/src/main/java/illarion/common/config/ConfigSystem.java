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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.channels.ScatteringByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.nio.file.StandardOpenOption.*;

/**
 * This is the main class for the configuration system. It contains the storage for the configuration values and
 * allows to apply changes to those values.
 * <p>
 * This class is fully thread save as the access is synchronized using a read/write lock. So reading access will work
 * mostly without synchronization. How ever in case there are any changes done to the configuration or the
 * configuration is saved or load the other parts of the application can't access the data of this configuration and
 * are blocked until its save to read again.
 * </p>
 */
public class ConfigSystem implements Config {
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
     * The properties file containing the default configuration that will be used
     * in case that no configuration file exists.
     */
    @Nonnull
    private static final String DEFAULT_CONFIG_FILENAME = "default-config.properties";

    @Nonnull
    private final Properties userProperties;

    /**
     * Create a configuration object with a file as source. The configuration
     * system will try to load the data from this source.
     *
     * @param source The configuration file that is supposed to be load
     */
    public ConfigSystem(@Nonnull Path source, InputStream defaultPropertiesStream) {
        configFile = source;
        changed = false;

        boolean configLoaded = false;
        Properties defaultProperties = new Properties();
        try {
            defaultProperties.load(defaultPropertiesStream);
            configLoaded = true;
        } catch (IOException e) {
            LOGGER.warn("Failed to load default configuration file - try to continue");
        }

        userProperties = new Properties(defaultProperties);
        if (Files.exists(configFile)) {
            try {
                userProperties.load(Files.newInputStream(configFile));
                configLoaded = true;
            } catch (IOException e) {
                LOGGER.warn("Failed to load existing user configuration - try to continue");
            }
        } else {
            changed = true;
        }

        if (!configLoaded) {
            throw new NoSuchElementException("Could not load default or user configuration. Please check the" +
                    "installation and the access rights of the application.");
        }


        lock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean getBoolean(@Nonnull String key) {
        return Boolean.parseBoolean(getString(key));
    }

    @Override
    public double getDouble(@Nonnull String key) {
        String value = getString(key);

        if (value == null) {
            return 0.d;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return 0.d;
        }
    }

    @Nullable
    @Override
    public Path getPath(@Nonnull String key) {
        String value = getString(key);

        if (value == null) {
            return null;
        }

        return Paths.get(value);
    }

    @Override
    public float getFloat(@Nonnull String key) {
        String value = getString(key);

        if (value == null) {
            return 0.f;
        }

        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return 0.f;
        }
    }

    @Override
    public int getInteger(@Nonnull String key) {
        String value = getString(key);

        if (value == null) {
            return 0;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return 0;
        }
    }

    @Nullable
    @Override
    public String getString(String key) {
        String value;
        lock.readLock().lock();
        try {
            value = userProperties.getProperty(key);
        } finally {
            lock.readLock().unlock();
        }

        if (value == null) {
            LOGGER.warn("No config entry found for: {}", key);
            return null;
        }

        return value;
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
            return;
        }

        if (Files.isDirectory(configFile)) {
            LOGGER.warn("Configuration not saved: config file set to illegal value.");
            return;
        }

        lock.writeLock().lock();
        try (OutputStream stream = Files.newOutputStream(configFile)){
            userProperties.store(stream, "Writing user properties file.");
            changed = false;
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
        set(key, value.toString());
    }

    @Override
    public void set(@Nonnull String key, @Nonnull String value) {
        lock.writeLock().lock();
        try {
            if (value.equals(userProperties.getProperty(key))) {
                return;
            }
            userProperties.setProperty(key, value);
            reportChangedKey(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Set the default value for a key. In this case the value is a boolean value. Setting default values does
     * basically the same as setting the normal values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(@Nonnull String key, String value) {
        if (!(userProperties.get(key) instanceof String)) {
            set(key, value);
        }
    }

    /**
     * Set the default value for a key. In this case the value is a double value. Setting default values does
     * basically the same as setting the normal values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(@Nonnull String key, double value) {
        if (!(userProperties.get(key) instanceof Double)) {
            set(key, value);
        }
    }

    /**
     * Set the default value for a key. In this case the value is a Path value. Setting default values does basically
     * the same as setting the normal values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(@Nonnull String key, @Nonnull Path value) {
        if (!(userProperties.get(key) instanceof String)) {
            set(key, value);
        }
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
