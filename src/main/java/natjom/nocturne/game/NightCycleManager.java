package natjom.nocturne.game;

import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NightCycleManager {
    private final GameSession session;
    private final List<Role> wakeUpOrder = new ArrayList<>();
    private int currentPhaseIndex = -1;
    private int timer = 0;
    private int currentMaxTime = 1;
    private final ServerBossEvent nightBossBar;

    public NightCycleManager(GameSession session) {
        this.session = session;

        this.nightBossBar = new ServerBossEvent(
                java.util.UUID.randomUUID(),
                Component.literal("§8La nuit tombe..."),
                BossEvent.BossBarColor.BLUE,
                BossEvent.BossBarOverlay.PROGRESS
        );

        for (ServerPlayer sp : session.getServerPlayers()) {
            this.nightBossBar.addPlayer(sp);
        }

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
            this.nightBossBar.setProgress((float) timer / currentMaxTime);
            return;
        }

        nextPhase();
    }

    private void nextPhase() {
        currentPhaseIndex++;

        if (currentPhaseIndex >= wakeUpOrder.size()) {
            this.nightBossBar.removeAllPlayers();
            session.endNight();
            return;
        }

        Role currentRole = wakeUpOrder.get(currentPhaseIndex);
        this.currentMaxTime = currentRole.getActionDuration();
        this.timer = this.currentMaxTime;

        this.nightBossBar.setName(Component.literal("§9Tour de : §l" + currentRole.getDisplayName().getString()));

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

    public void stop() {
        if (this.nightBossBar != null) {
            this.nightBossBar.removeAllPlayers();
        }
    }
}