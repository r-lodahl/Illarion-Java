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
package illarion.download.maven;

import illarion.common.util.ProgressMonitor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static illarion.download.maven.MavenDownloaderCallback.State.ResolvingArtifacts;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class DefaultArtifactRequestTracer implements ArtifactRequestTracer {
    private final boolean offline;
    @NotNull
    private final MavenDownloaderCallback callback;
    @NotNull
    private final ProgressMonitor progressMonitor;
    @NotNull
    private final AtomicLong totalTransferred;
    @NotNull
    private final AtomicLong recordedTotal;
    @NotNull
    private final Map<String, Long> transferPerArtifact;
    @NotNull
    private final Map<String, Boolean> recordedTotalForArtifact;

    public DefaultArtifactRequestTracer(boolean offline,
                                        @NotNull MavenDownloaderCallback callback,
                                        @NotNull ProgressMonitor progressMonitor) {
        this.offline = offline;
        this.callback = callback;
        this.progressMonitor = progressMonitor;
        totalTransferred = new AtomicLong(0L);
        recordedTotal = new AtomicLong(0L);
        transferPerArtifact = new ConcurrentHashMap<>();
        recordedTotalForArtifact = new ConcurrentHashMap<>();
    }

    @Override
    public void trace(@NotNull ProgressMonitor monitor, @NotNull String artifact, long totalSize, long transferred) {
        Boolean totalRecorded = null;
        if (totalSize >= 0) {
            monitor.setProgress(transferred / (float) totalSize);
            totalRecorded = recordedTotalForArtifact.put(artifact, Boolean.TRUE);
        }

        long fullTotalSize;
        fullTotalSize = (totalRecorded == null) ? recordedTotal.addAndGet(totalSize) : recordedTotal.get();

        Long oldValue = transferPerArtifact.put(artifact, transferred);
        long lastTransferred = (oldValue == null) ? 0 : oldValue;
        long total = totalTransferred.addAndGet(transferred - lastTransferred);

        String text = humanReadableByteCount(total, true);
        if (fullTotalSize >= total) {
            text += '\\' + humanReadableByteCount(fullTotalSize, true);
        }

        callback.reportNewState(ResolvingArtifacts, progressMonitor, offline, text);
    }

    @NotNull
    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (StrictMath.log(bytes) / StrictMath.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / StrictMath.pow(unit, exp), pre);
    }
}
