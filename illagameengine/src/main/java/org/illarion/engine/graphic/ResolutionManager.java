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

import org.illarion.engine.Window;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is used to supply and compare possible display resolutions.
 */
public final class ResolutionManager {
    public final class WindowSize {
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

    private final class ResolutionOptions {
        public final Set<Integer> bitsPerPoints, refreshRates;

        public ResolutionOptions() {
            this.bitsPerPoints = new HashSet<>();
            this.refreshRates = new HashSet<>();
        }
    }

    private final Map<WindowSize, ResolutionOptions> resolutions;

    public ResolutionManager() {
        this.resolutions = new HashMap<>(20);
    }

    public void addResolution(int width, int height, int bitsPerPoint, int refreshRate) {
        WindowSize windowSize = new WindowSize(width, height);

        if (!resolutions.containsKey(windowSize)) {
            resolutions.put(windowSize, new ResolutionOptions());
        }

        ResolutionOptions options = resolutions.get(windowSize);
        options.bitsPerPoints.add(bitsPerPoint);
        options.refreshRates.add(refreshRate);
    }

    public WindowSize[] getResolutions() {
        return resolutions.keySet().toArray(WindowSize[]::new);
    }

    public int[] getRefreshRates(WindowSize windowSize) {
        return resolutions.get(windowSize).refreshRates.stream().mapToInt(x -> x).toArray();
    }

    public int[] getBitsPerPoints(WindowSize windowSize) {
        return resolutions.get(windowSize).bitsPerPoints.stream().mapToInt(x -> x).toArray();
    }
}
