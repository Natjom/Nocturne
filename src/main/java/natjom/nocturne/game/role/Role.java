package natjom.nocturne.game.role;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.base.LoupRole;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

public abstract class Role {

    public abstract Component getDisplayName();

    public abstract int getNightOrder();

    public abstract String getSkinTexture();

    public boolean hasNightAction() { return false; }

    public int getActionDuration() { return 140; }

    public void onWakeUp(ServerPlayer player, GameSession session) { }

    public boolean didWin(GameSession session, UUID myId, List<UUID> eliminated) {
        boolean wolfDied = false;
        boolean wolfInPlay = false;

        for (net.minecraft.server.level.ServerPlayer p : session.getServerPlayers()) {
            if (session.getBoard().getCurrentRole(p.getUUID()) instanceof LoupRole) {
                wolfInPlay = true;
                if (eliminated.contains(p.getUUID())) {
                    wolfDied = true;
                }
            }
        }

        if (wolfInPlay) {
            return wolfDied;
        } else {
            return eliminated.isEmpty();
        }
    }
}