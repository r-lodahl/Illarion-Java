package illarion.client.net;

import com.google.common.eventbus.Subscribe;
import illarion.client.resources.SoundFactory;
import illarion.client.world.World;
import illarion.common.types.ServerCoordinate;
import org.illarion.engine.BackendBinding;
import org.illarion.engine.EventBus;
import org.illarion.engine.assets.SoundsManager;
import org.illarion.engine.event.NetSoundRequestedEvent;
import org.illarion.engine.sound.Sound;
import org.illarion.engine.sound.Sounds;

public class NetworkEventConsumer {
    private final BackendBinding binding;

    public NetworkEventConsumer(BackendBinding binding) {
        this.binding = binding;
        EventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void playSound(NetSoundRequestedEvent event) {
        SoundsManager manager = binding.getAssets().getSoundsManager();
        Sound sound  = SoundFactory.getInstance().getSound(event.soundEffectId, manager);

        if (sound == null) {
            return;
        }

        ServerCoordinate playerLocation = World.getPlayer().getLocation();

        Sounds sounds = binding.getSounds();
        sounds.playSound(
                sound,
                sounds.getSoundVolume(),
                event.soundLocation.getX() - playerLocation.getX(),
                event.soundLocation.getY() - playerLocation.getY(),
                event.soundLocation.getZ() - playerLocation.getZ()
        );
    }
}
