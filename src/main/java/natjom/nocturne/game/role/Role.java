package natjom.nocturne.game.role;

import natjom.nocturne.game.GameSession;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public abstract class Role {

    public abstract Component getDisplayName();

    public abstract int getNightOrder();

    public abstract String getSkinTexture();

    public boolean hasNightAction() {
        return false;
    }

    public void onWakeUp(ServerPlayer player, GameSession session) {
        // TODO:
    }

    public void onSleep(ServerPlayer player, GameSession session) {
        // TODO:
    }
}