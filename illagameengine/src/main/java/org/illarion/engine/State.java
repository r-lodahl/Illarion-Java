package org.illarion.engine;

public enum State {
    NONE,      // Null state
    LOGIN,     // Login
    LOADING,   // Loading screen
    PLAYING,   // Game screen
    ENDING,    // Last screen before shutdown
    LOGOUT,    // Last screen before shutdown
    DISCONNECT // Lost connection screen
}
