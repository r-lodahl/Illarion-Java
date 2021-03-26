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
package illarion.client.resources;

import com.google.common.collect.ImmutableMap;
import illarion.client.resources.data.ResourceTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractTemplateFactory<T extends ResourceTemplate> implements ResourceFactory<T> {
    @NotNull
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * The ID used in case the requested object does not exist.
     */
    private final int defaultId;
    /**
     * This is the builder that is used to create the resource storage. This variable is only used during the
     * initialization phase of this factory. Once loading is done it is not required anymore.
     */
    @Nullable
    private ImmutableMap.Builder<Integer, T> storageBuilder;

    /**
     * This variable is used during populating the resources to ensure that all keys are unique.
     */
    @Nullable
    private Set<Integer> storageBuilderKeys;

    /**
     * The map that is used to store the resources.
     */
    @NotNull
    private final Map<Integer, T> storage = new HashMap<>();

    /**
     * The default constructor.
     */
    protected AbstractTemplateFactory() {
        this(-1);
    }

    /**
     * The default constructor.
     */
    protected AbstractTemplateFactory(int defaultId) {
        this.defaultId = defaultId;
    }

    @Override
    public void storeResource(@NotNull T resource) {
        if (storageBuilder == null || storageBuilderKeys == null) {
            throw new IllegalStateException("Factory was not initialized yet.");
        }

        if (!storageBuilderKeys.add(resource.getTemplateId())) {
            LOGGER.warn("Located duplicated resource template: {}", resource);
        }

        storageBuilder.put(resource.getTemplateId(), resource);
    }

    @Override
    public void loadingFinished() {}

    @Override
    public void init() {
        storageBuilder = new ImmutableMap.Builder<>();
        storageBuilderKeys = new HashSet<>();
    }

    @Contract(pure = true)
    public boolean hasTemplate(int templateId) {
        return storage.containsKey(templateId);
    }

    @NotNull
    @Contract(pure = true)
    public T getTemplate(int templateId) {
        T object = storage.get(templateId);
        if ((object == null) && (defaultId > -1)) {
            T defaultObject = storage.get(defaultId);
            if (defaultObject == null) {
                throw new IllegalStateException("Requested template " + templateId + " and the default template " +
                        defaultId + " were not found.");
            }
            return defaultObject;
        }
        if (object == null) {
            throw new IllegalStateException("Requested template " + templateId +
                    " was not found and not default template was declared.");
        }
        return object;
    }
}
