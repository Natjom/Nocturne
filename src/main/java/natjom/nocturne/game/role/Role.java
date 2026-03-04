package natjom.nocturne.game.role;

import natjom.nocturne.game.GameSession;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public abstract class Role {

    public abstract Component getDisplayName();

    public abstract int getNightOrder();

    // public abstract String getSkinTexture();

    public abstract String getSkinTexture();

    public boolean hasNightAction() { return false; }

    public int getActionDuration() { return 140; }

    public void onWakeUp(ServerPlayer player, GameSession session) { }
}