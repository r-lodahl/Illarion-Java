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

import java.util.*;

/**
 * This class is used to supply and compare possible display resolutions.
 */
public final class ResolutionManager {
    public static final class WindowSize {
        public final int height, width;

        private WindowSize(int width, int height) {
            this.height = height;
            this.width = width;
        }

        @Override
        public String toString() {
            return width + " x " + height;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            WindowSize that = (WindowSize) o;

            if (height != that.height) return false;
            return width == that.width;
        }

        @Override
        public int hashCode() {
            int result = height;
            result = 31 * result + width;
            return result;
        }
    }

    public static final class ResolutionOptions {
        public final Set<Integer> bitsPerPoints, refreshRates;

        public ResolutionOptions() {
            this.bitsPerPoints = new HashSet<>();
            this.refreshRates = new HashSet<>();
        }
    }

    public static final class Device {
        public final int virtualX, virtualY;
        public final String name;

        public Device(int virtualX, int virtualY, String name) {
            this.virtualX = virtualX;
            this.virtualY = virtualY;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Device device = (Device) o;

            if (virtualX != device.virtualX) return false;
            if (virtualY != device.virtualY) return false;
            return name.equals(device.name);
        }

        @Override
        public int hashCode() {
            int result = virtualX;
            result = 31 * result + virtualY;
            result = 31 * result + name.hashCode();
            return result;
        }
    }

    private final Map<Device, Map<WindowSize, ResolutionOptions>> resolutionsPerDevice;

    public ResolutionManager() {
        this.resolutionsPerDevice = new HashMap<>(2);
    }

    public void addResolution(Device device, int width, int height, int bitsPerPoint, int refreshRate) {
        if (width < 720 || height < 720) {
            return; // Tiny resolutions are unsupported
        }

        if (!resolutionsPerDevice.containsKey(device)) {
            resolutionsPerDevice.put(device, new HashMap<>(10));
        }
        var resolutions = resolutionsPerDevice.get(device);

        WindowSize windowSize = new WindowSize(width, height);

        if (!resolutions.containsKey(windowSize)) {
            resolutions.put(windowSize, new ResolutionOptions());
        }

        ResolutionOptions options = resolutions.get(windowSize);
        options.bitsPerPoints.add(bitsPerPoint);
        options.refreshRates.add(refreshRate);
    }

    public Device[] getDevices() {
        if (resolutionsPerDevice.isEmpty()) {
            // LOG WARNING, There has to be at least 1 device!
        }

        return resolutionsPerDevice.keySet().toArray(Device[]::new);
    }

    public WindowSize[] getResolutions(Device device) {
        if (!resolutionsPerDevice.containsKey(device)) {
            return new WindowSize[0];
        }

        return resolutionsPerDevice.get(device).keySet().toArray(WindowSize[]::new);
    }

    public Optional<ResolutionOptions> getFullscreenOptions(Device device, WindowSize windowSize) {
        if (!resolutionsPerDevice.containsKey(device)) {
            return Optional.empty();
        }

        var resolutions = resolutionsPerDevice.get(device);

        if (!resolutions.containsKey(windowSize)) {
            return Optional.empty();
        }

        return Optional.of(resolutions.get(windowSize));
    }
}
