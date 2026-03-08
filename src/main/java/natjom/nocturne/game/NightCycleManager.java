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
    private boolean hasCheckedMarques = false;

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
        List<Role> sortedRoles = new java.util.ArrayList<>(this.session.getBoard().getAllRolesInGame().stream()
                .filter(Role::hasNightAction)
                .distinct()
                .sorted(java.util.Comparator.comparingInt(Role::getNightOrder))
                .toList());

        boolean hasWolves = sortedRoles.stream().anyMatch(r -> r instanceof natjom.nocturne.game.role.base.LoupRole);
        boolean hasBaseWolf = sortedRoles.stream().anyMatch(r -> r.getClass() == natjom.nocturne.game.role.base.LoupRole.class);

        if (hasWolves && !hasBaseWolf) {
            sortedRoles.add(new natjom.nocturne.game.role.base.LoupRole());
            sortedRoles.sort(java.util.Comparator.comparingInt(Role::getNightOrder));
        }

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
        for (ServerPlayer player : session.getServerPlayers()) {
            if (player.level().getServer() != null) {
                player.level().getServer().execute(() -> player.closeContainer());
            }
        }

        currentPhaseIndex++;

        if (currentPhaseIndex >= wakeUpOrder.size()) {
            this.nightBossBar.removeAllPlayers();
            session.endNight();
            return;
        }

        Role currentRole = wakeUpOrder.get(currentPhaseIndex);

        boolean hasVampireDLC = this.session.getBoard().getAllRolesInGame().stream()
                .anyMatch(r -> r instanceof natjom.nocturne.game.role.vampire.VampireExtensionRole);

        if (hasVampireDLC && !this.hasCheckedMarques && currentRole.getNightOrder() >= 12) {
            this.hasCheckedMarques = true;
            this.currentPhaseIndex--;

            this.currentMaxTime = 100;
            this.timer = this.currentMaxTime;
            this.nightBossBar.setName(Component.literal("§5§lPhase de lecture des Marques"));

            for (net.minecraft.server.level.ServerPlayer sp : this.session.getServerPlayers()) {
                natjom.nocturne.game.role.vampire.Marque myMarque = this.session.getBoard().getPlayerMarque(sp.getUUID());

                if (myMarque != null) {
                    sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§8================================="));
                    sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c[Crépuscule] Il est temps de regarder ta Marque."));
                    sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§eTa marque actuelle est : §l" + myMarque.getDisplayName().getString()));
                    sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§8================================="));
                }
            }
            return;
        }

        this.currentMaxTime = currentRole.getActionDuration();
        this.timer = this.currentMaxTime;

        this.nightBossBar.setName(Component.literal("§9Tour de : §l" + currentRole.getDisplayName().getString()));

        broadcastWakeUp(currentRole);

        for (ServerPlayer player : session.getServerPlayers()) {
            Role initialRole = session.getBoard().getInitialRole(player.getUUID());

            boolean isMyTurn = false;

            if (currentRole.getClass() == natjom.nocturne.game.role.base.LoupRole.class) {
                if (initialRole instanceof natjom.nocturne.game.role.base.LoupRole && !(initialRole instanceof natjom.nocturne.game.role.crepuscule.LoupReveurRole)) {
                    isMyTurn = true;
                }
            } else if (initialRole == currentRole) {
                isMyTurn = true;
            }

            if (isMyTurn) {
                natjom.nocturne.game.role.vampire.Marque myMarque = session.getBoard().getPlayerMarque(player.getUUID());

                if (currentRole.getNightOrder() >= 12 && myMarque == natjom.nocturne.game.role.vampire.Marque.PEUR) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§9Tu es complètement paralysé par la Marque de la Peur... Tu passes ton tour cette nuit."));
                } else {
                    currentRole.onWakeUp(player, session);
                }
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