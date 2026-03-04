package natjom.nocturne.game;

import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NightCycleManager {
    private final GameSession session;
    private final List<Role> wakeUpOrder = new ArrayList<>();
    private int currentPhaseIndex = -1;
    private int timer = 0;

    public NightCycleManager(GameSession session) {
        this.session = session;
        prepareOrder();
    }

    private void prepareOrder() {
        List<Role> sortedRoles = this.session.getBoard().getAllRolesInGame().stream()
                .filter(Role::hasNightAction)
                .distinct()
                .sorted(Comparator.comparingInt(Role::getNightOrder))
                .toList();

        this.wakeUpOrder.addAll(sortedRoles);
    }

    public void tick() {
        if (timer > 0) {
            timer--;
            // TODO : Bossbar
            return;
        }

        nextPhase();
    }

    private void nextPhase() {
        currentPhaseIndex++;

        if (currentPhaseIndex >= wakeUpOrder.size()) {
            session.endNight();
            return;
        }

        Role currentRole = wakeUpOrder.get(currentPhaseIndex);
        timer = currentRole.getActionDuration();

        broadcastWakeUp(currentRole);

        for (ServerPlayer player : session.getServerPlayers()) {
            if (session.getBoard().getInitialRole(player.getUUID()) == currentRole) {
                currentRole.onWakeUp(player, session);
            }
        }
    }

    private void broadcastWakeUp(Role role) {
        Component msg = Component.literal("§eLe village s'endort... sauf : §l" + role.getDisplayName().getString());
        for (ServerPlayer sp : session.getServerPlayers()) {
            sp.sendOverlayMessage(msg);
        }
    }
}