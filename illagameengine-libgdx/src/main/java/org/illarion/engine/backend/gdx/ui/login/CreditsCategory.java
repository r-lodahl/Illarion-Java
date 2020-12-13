package org.illarion.engine.backend.gdx.ui.login;

import java.util.ArrayList;
import java.util.List;

public class CreditsCategory {
    private final String nameEn;
    private final String nameDe;
    private final List<String> contributors;

    public CreditsCategory(String nameEn, String nameDe) {
        this.nameDe = nameDe;
        this.nameEn = nameEn;
        this.contributors = new ArrayList<>();
    }

    public void addContributor(String contributor) {
        contributors.add(contributor);
    }

    public List<String> getContributors() {
        return contributors;
    }

    public String getNameEn() {
        return nameEn;
    }
}
