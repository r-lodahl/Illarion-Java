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
package org.illarion.engine.graphic;

import illarion.common.types.ServerCoordinate;
import illarion.common.util.PoolThreadFactory;
import illarion.common.util.Stoppable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Manager class that handles the light. It stores the pre-calculated light rays
 * as well as the light sources that are currently in use. Also it creates and
 * removes the light sources on request.
 * <p>
 * The whole calculations are threaded, so the light map that is the target of
 * all calculation results needs to be thread save.
 * </p>
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LightTracer implements Stoppable {
    private class CalculateLightTask implements Callable<Void> {
        @NotNull
        private final LightSource light;

        private CalculateLightTask(@NotNull LightSource light) {
            this.light = light;
        }

        @Override
        public Void call() throws Exception {
            if (isShutDown) {
                return null;
            }
            applyingLock.readLock().lock();
            try {
                light.getCalculationLock().lock();
                try {
                    for (; ; ) {
                        light.calculateShadows();
                        if (!light.isDirty()) {
                            if (!lights.contains(light)) {
                                lights.add(light);
                            }
                            light.setCalculating(false);
                            break;
                        }
                    }
                } finally {
                    light.getCalculationLock().unlock();
                }
            } finally {
                applyingLock.readLock().unlock();
            }
            notifyLightCalculationDone();
            return null;
        }
    }

    @NotNull
    private static final Logger log = LogManager.getLogger();

    @NotNull
    private final Callable<Void> publishLightsTask = () -> {
        notifyLightCalculationDone();
        return null;
    };

    /**
     * The executor service that takes care for calculating the lights.
     */
    @NotNull
    private final ExecutorService lightCalculationService;

    /**
     * This integer stores the amount of lights that are currently calculated.
     */
    @NotNull
    private final AtomicInteger lightsInProgress;

    /**
     * The lighting map that is the data source and the target for the light
     * calculating results for all light sources handled by this light tracer.
     */
    @NotNull
    private final LightingMap mapSource;

    /**
     * The list of lights that were processed at least once and contain all data to be applied to the map.
     */
    @NotNull
    private final List<LightSource> lights;

    /**
     * Is set true once the shutdown of the light tracer is triggered.
     */
    private boolean isShutDown;

    @NotNull
    private final ReadWriteLock applyingLock;

    /**
     * Default constructor of the light tracer. This tracer handles all light
     * sources that are on the map source that is set with the parameter.
     *
     * @param tracerMapSource the map the lights this tracer handles are on
     */
    public LightTracer(@NotNull LightingMap tracerMapSource) {
        mapSource = tracerMapSource;
        lights = new CopyOnWriteArrayList<>();

        int maxThreads = Runtime.getRuntime().availableProcessors();
        lightCalculationService = new ThreadPoolExecutor(0, maxThreads, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new PoolThreadFactory("LightTracer", true));
        lightsInProgress = new AtomicInteger(0);
        applyingLock = new ReentrantReadWriteLock();
    }

    /**
     * Add a light source to the list of light sources of this tracer. This
     * causes that this light source is taken into account and is rendered by
     * this light tracer if requested.
     *
     * @param light the light that shall be added to the light tracer and so to
     * the game screen
     */
    public void addLight(@NotNull LightSource light) {
        if (isShutDown) {
            return;
        }
        log.info("Adding new light to tracer: {}", light);
        light.setMapSource(mapSource);
        if (light.isDirty()) {
            if (!light.isCalculating()) {
                light.setCalculating(true);
                lightsInProgress.incrementAndGet();
                lightCalculationService.submit(new CalculateLightTask(light));
            }
        } else {
            if (!lights.contains(light)) {
                lights.add(light);
            }
            lightsInProgress.incrementAndGet();
            lightCalculationService.submit(publishLightsTask);
        }
    }

    /**
     * Check if there are no lights set.
     *
     * @return true in case this tracer does not handle any lights currently
     */
    public boolean isEmpty() {
        return lights.isEmpty() && (lightsInProgress.get() == 0);
    }

    /**
     * Notify the light system about a change on the map. This notify is
     * forwarded to all light sources and those only take the notify into
     * account in case its within the range of their rays. So every change on
     * the map should be reported to the tracer no matter if a light is around
     * this location or not.
     *
     * @param loc the location the change occurred at
     */
    public void notifyChange(@NotNull ServerCoordinate loc) {
        if (isShutDown) {
            return;
        }
        log.info("Got notification about change at {}", loc);
        for (LightSource light : lights) {
            light.notifyChange(loc);
            if (light.isDirty()) {
                log.trace("Light {} requires a update now.", light);
                refreshLight(light);
            }
        }
    }

    /**
     * Refresh the light tracer and force all lights to recalculate the values.
     */
    public void refresh() {
        if (isShutDown) {
            return;
        }
        log.info("Refreshing all lights.");
        lights.forEach(this::refreshLight);
    }

    private void notifyLightCalculationDone() {
        if (lightsInProgress.decrementAndGet() == 0) {
            publishTidyLights();
        }
    }

    public void updateLightLocation(@NotNull LightSource light, @NotNull ServerCoordinate newLocation) {
        if (isShutDown) {
            return;
        }
        log.info("Updating light {} location to: {}", light, newLocation);

        light.setLocation(newLocation);
        if (light.isDirty()) {
            refreshLight(light);
        }
    }

    /**
     * Move a light to the dirty lights list to have it updated at the next run.
     *
     * @param light the light that shall be updated.
     */
    public void refreshLight(@NotNull LightSource light) {
        if (isShutDown) {
            return;
        }
        log.info("Refreshing light {}", light);

        light.refresh();
        if (light.getCalculationLock().tryLock()) {
            try {
                light.refresh();
                addLight(light);
            } finally {
                light.getCalculationLock().unlock();
            }
        }
    }

    public void replace(@NotNull LightSource oldSource, @NotNull LightSource newSource) {
        if (isShutDown) {
            return;
        }
        log.info("Replacing {} with {}", oldSource, newSource);
        oldSource.dispose();
        addLight(newSource);
    }

    /**
     * Remove a light source from this tracer. This causes that the light is not
     * any longer calculated and rendered.
     *
     * @param light the light source that shall be removed
     */
    public void remove(@NotNull LightSource light) {
        if (isShutDown) {
            return;
        }
        log.info("Removing {} from tracer.", light);
        light.dispose();
        lightsInProgress.incrementAndGet();
        lightCalculationService.submit(publishLightsTask);
    }

    /**
     * Publish all tidy lights.
     */
    private void publishTidyLights() {
        if (isShutDown) {
            return;
        }
        List<LightSource> disposedList = null;
        applyingLock.writeLock().lock();
        try {
            log.info("Publishing lights now!");
            for (LightSource light : lights) {
                if (light.isDisposed()) {
                    if (disposedList == null) {
                        disposedList = new ArrayList<>();
                    }
                    disposedList.add(light);
                } else {
                    light.apply();
                }
            }
        } finally {
            applyingLock.writeLock().unlock();
        }
        mapSource.renderLights();

        if (disposedList != null) {
            lights.removeAll(disposedList);
        }
    }

    /**
     * Stop the thread as soon as possible.
     */
    @Override
    public void saveShutdown() {
        log.info("LightTracer is shutting down now.");
        isShutDown = true;
        lightCalculationService.shutdown();
        while (!lightCalculationService.isTerminated()) {
            try {
                lightCalculationService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        lights.clear();
    }
}
