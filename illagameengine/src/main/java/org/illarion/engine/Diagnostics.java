package org.illarion.engine;

public interface Diagnostics {
    boolean isEnabled();
    int getTileCount();
    int getSceneElementCount();
    long getNetPing();
    long getServerPing();
    String getDiagnosticFontName();
}
