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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class MavenTransferListener implements TransferListener {
    @NotNull
    private static final Logger log = LogManager.getLogger();

    @Override
    public void transferInitiated(@NotNull TransferEvent event) throws TransferCancelledException {
        log.info(event.toString());
    }

    @Override
    public void transferStarted(@NotNull TransferEvent event) throws TransferCancelledException {
        log.info(event.toString());
        @Nullable ArtifactTraceData data = getTraceData(event);
        if (data != null) {
            reportTrace(data, event);
        }
    }

    @Override
    public void transferProgressed(@NotNull TransferEvent event) throws TransferCancelledException {
        log.debug(event.toString());
        @Nullable ArtifactTraceData data = getTraceData(event);
        if (data != null) {
            reportTrace(data, event);
        }
    }

    @Override
    public void transferCorrupted(@NotNull TransferEvent event) throws TransferCancelledException {
        log.error(event.toString());
    }

    @Override
    public void transferSucceeded(@NotNull TransferEvent event) {
        log.info(event.toString());
        @Nullable ArtifactTraceData data = getTraceData(event);
        if (data != null) {
            reportTrace(data, event);
        }
    }

    @Override
    public void transferFailed(@NotNull TransferEvent event) {
        log.info(event.toString());
    }

    @Nullable
    private static ArtifactTraceData getTraceData(@NotNull TransferEvent event) {
        @Nullable RequestTrace trace = event.getResource().getTrace();
        while (true) {
            if (trace == null) {
                break;
            }
            if (trace.getData() instanceof ArtifactTraceData) {
                return (ArtifactTraceData) trace.getData();
            }
            trace = trace.getParent();
        }
        return null;
    }

    private static void reportTrace(@NotNull ArtifactTraceData data, @NotNull TransferEvent event) {
        ArtifactRequestTracer requestTracer = data.getTracer();
        ProgressMonitor monitor = data.getMonitor();
        long totalSize = event.getResource().getContentLength();
        requestTracer.trace(monitor, event.getResource().getResourceName(), totalSize, event.getTransferredBytes());
    }
}
