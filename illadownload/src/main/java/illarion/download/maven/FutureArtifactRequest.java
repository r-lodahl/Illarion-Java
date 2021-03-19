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
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class FutureArtifactRequest implements Callable<ArtifactResult> {
    @NotNull
    private final ProgressMonitor progressMonitor;
    @NotNull
    private final ArtifactRequest request;
    @NotNull
    private final RepositorySystemSession session;
    @NotNull
    private final RepositorySystem system;

    public FutureArtifactRequest(
            @NotNull RepositorySystem system,
            @NotNull RepositorySystemSession session,
            @NotNull ArtifactRequest request,
            @NotNull ArtifactRequestTracer requestTracer) {
        this.request = request;
        this.system = system;
        this.session = session;
        progressMonitor = new ProgressMonitor();

        request.setTrace(new RequestTrace(new ArtifactTraceData(requestTracer, progressMonitor)));
    }

    @NotNull
    @Override
    public ArtifactResult call() throws Exception {
        progressMonitor.setProgress(0.f);
        ArtifactResult result = system.resolveArtifact(session, request);
        progressMonitor.setProgress(1.f);
        return result;
    }

    @NotNull
    public ProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    @NotNull
    public ArtifactRequest getRequest() {
        return request;
    }
}
