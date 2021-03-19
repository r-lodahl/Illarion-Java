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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static illarion.download.maven.MavenDownloaderCallback.State.ResolvingDependencies;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class MavenRepositoryListener implements RepositoryListener {
    @NotNull
    private static final Logger log = LogManager.getLogger();

    private boolean offline;
    @Nullable
    private MavenDownloaderCallback callback;



    @Override
    public void artifactDescriptorInvalid(@NotNull RepositoryEvent event) {
        log.warn(event.toString());
    }

    @Override
    public void artifactDescriptorMissing(@NotNull RepositoryEvent event) {
        log.warn(event.toString());
    }

    @Override
    public void metadataInvalid(@NotNull RepositoryEvent event) {
        log.warn(event.toString());
    }

    @Override
    public void artifactResolving(@NotNull RepositoryEvent event) {
        if ((callback != null) && "pom".equals(event.getArtifact().getExtension())) {
            callback.reportNewState(ResolvingDependencies, null, offline,
                                    event.getArtifact().getGroupId() + ':' + event.getArtifact().getArtifactId());
        }
        log.info(event.toString());
    }

    @Override
    public void artifactResolved(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataResolving(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataResolved(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactDownloading(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactDownloaded(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataDownloading(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataDownloaded(@NotNull RepositoryEvent event) {

    }

    @Override
    public void artifactInstalling(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactInstalled(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataInstalling(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataInstalled(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactDeploying(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactDeployed(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataDeploying(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataDeployed(@NotNull RepositoryEvent event) {
        log.info(event.toString());
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public void setCallback(@Nullable MavenDownloaderCallback callback) {
        this.callback = callback;
    }
}
