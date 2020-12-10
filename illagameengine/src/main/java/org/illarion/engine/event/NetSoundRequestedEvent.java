package org.illarion.engine.event;

import illarion.common.types.ServerCoordinate;

public class NetSoundRequestedEvent {
    public final ServerCoordinate soundLocation;
    public final int soundEffectId;

    public NetSoundRequestedEvent(ServerCoordinate soundLocation, int soundEffectId) {
        this.soundLocation = soundLocation;
        this.soundEffectId = soundEffectId;
    }
}
