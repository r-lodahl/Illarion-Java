package illarion.client.state;

import org.illarion.engine.BackendBinding;

public class NoneState implements GameState {
    @Override
    public void create(BackendBinding binding) {}

    @Override
    public void dispose() {}

    @Override
    public void update(int delta) {}

    @Override
    public void render() {}

    @Override
    public boolean isClosingGame() {
        // According to the interface, default reply is false. Consider rewriting docs there or this method.
        return true;
    }

    @Override
    public void enterState() {}

    @Override
    public void leaveState() {}
}
