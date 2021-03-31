package org.illarion.engine.ui.stage;

import org.illarion.engine.ui.Action;

public interface LoadingStage {
    void setLoadedElement(String localizationKey);
    void setProgess(float progess);
    void setFailure(String localizationKey, Action failureCallback);
}