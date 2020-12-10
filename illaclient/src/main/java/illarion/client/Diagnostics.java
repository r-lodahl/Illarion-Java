package illarion.client;

import illarion.client.graphics.FontLoader;
import illarion.client.util.ConnectionPerformanceClock;
import illarion.client.world.World;

public class Diagnostics implements org.illarion.engine.Diagnostics {
    public final boolean enabled;

    public Diagnostics(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public int getTileCount() {
        return World.isInitDone()? World.getMap().getTileCount() : 0;
    }

    @Override
    public int getSceneElementCount() {
        return World.isInitDone()? World.getMapDisplay().getGameScene().getElementCount() : 0;
    }

    @Override
    public long getNetPing() {
        long serverPing = ConnectionPerformanceClock.getServerPing();
        long netCommPing = ConnectionPerformanceClock.getNetCommPing();
        return Math.max(0L, netCommPing - serverPing);
    }

    @Override
    public long getServerPing() {
        return ConnectionPerformanceClock.getServerPing();
    }

    @Override
    public String getDiagnosticFontName() {
        return FontLoader.CONSOLE_FONT;
    }
}
