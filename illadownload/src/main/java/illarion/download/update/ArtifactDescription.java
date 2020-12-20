package illarion.download.update;

public class ArtifactDescription {
    private String artifactName;
    private String artifactUrl;
    private String artifactHash;

    public String getArtifactHash() {
        return artifactHash;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public String getArtifactUrl() {
        return artifactUrl;
    }

    public void setArtifactHash(String artifactHash) {
        this.artifactHash = artifactHash;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public void setArtifactUrl(String artifactUrl) {
        this.artifactUrl = artifactUrl;
    }
}
