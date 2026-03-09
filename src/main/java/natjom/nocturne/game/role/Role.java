package natjom.nocturne.game.role;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.base.LoupRole;
import natjom.nocturne.game.role.vampire.Marque;
import natjom.nocturne.game.role.vampire.VampireRole;
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
        boolean vampireInPlay = false;
        boolean vampireDied = false;
        for (ServerPlayer p : session.getServerPlayers()) {
            Role currentRole = session.getBoard().getCurrentRole(p.getUUID());
            Marque currentMarque = session.getBoard().getPlayerMarque(p.getUUID());
            if (currentRole instanceof LoupRole) {
                wolfInPlay = true;
                if (eliminated.contains(p.getUUID())) {
                    wolfDied = true;
                }
            }
            if (currentRole instanceof natjom.nocturne.game.role.vampire.VampireRole || currentRole instanceof natjom.nocturne.game.role.vampire.LeMaitreRole || currentRole instanceof natjom.nocturne.game.role.vampire.LeComteRole || currentMarque == natjom.nocturne.game.role.vampire.Marque.VAMPIRE) {
                vampireInPlay = true;
                if (eliminated.contains(p.getUUID())) {
                    vampireDied = true;
                }
            }
        }
        if (wolfInPlay) {
            return wolfDied;
        } else if (vampireInPlay) {
            return vampireDied;
        } else {
            return eliminated.isEmpty();
        }
    }
}