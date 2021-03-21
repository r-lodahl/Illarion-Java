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
package illarion.client.resources.loaders;

import illarion.client.resources.ResourceFactory;
import illarion.client.util.IdWrapper;
import illarion.common.util.TableLoaderSink;
import illarion.common.util.TableLoaderSound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;


/**
 * This class is used to load the sound definitions from the resource table that was created using the configuration
 * tool. The class will create the required sound objects and send them to the sound factory that takes care for
 * distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SoundLoader extends AbstractResourceLoader<IdWrapper<String>>
        implements TableLoaderSink<TableLoaderSound> {
    /**
     * The logger that is used to report error messages.
     */
    @NotNull
    private static final Logger log = LogManager.getLogger();

    /**
     * Trigger the loading sequence for this loader.
     */
    @NotNull
    @Override
    public ResourceFactory<IdWrapper<String>> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        ResourceFactory<IdWrapper<String>> factory = getTargetFactory();

        factory.init();
        new TableLoaderSound(this);
        factory.loadingFinished();

        loadingDone();

        return factory;
    }

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(int line, @NotNull TableLoaderSound loader) {
        int clipID = loader.getSoundId();
        String filename = loader.getSoundFile();

        try {
            getTargetFactory().storeResource(new IdWrapper<>(clipID, filename));
        } catch (IllegalStateException ex) {
            log.error("Failed adding sound to internal factory. ID: {} - Filename: {}", clipID, filename);
        }

        return true;
    }
}
