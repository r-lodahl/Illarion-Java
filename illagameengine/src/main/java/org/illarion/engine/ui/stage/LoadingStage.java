package org.illarion.engine.ui.stage;

public interface LoadingStage extends Stage {
    void setText(String localizationKey);
    void setProgess(float progess);
}