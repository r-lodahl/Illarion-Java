package illarion.download.update;

public class ApplicationManifest {
    private ArtifactDescription application;
    private ArtifactDescription[] dependencies;

    public ArtifactDescription getApplication() {
        return application;
    }

    public ArtifactDescription[] getDependencies() {
        return dependencies;
    }

    public void setApplication(ArtifactDescription application) {
        this.application = application;
    }

    public void setDependencies(ArtifactDescription[] dependencies) {
        this.dependencies = dependencies;
    }
}
