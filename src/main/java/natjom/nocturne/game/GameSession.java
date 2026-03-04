package natjom.nocturne.game;

import natjom.nocturne.registry.NocturneRegistries;
import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameSession {
    private final List<ServerPlayer> serverPlayers;
    private final List<UUID> players;
    // private final PlayerCircle circle;
    private final GameBoard board;
    private GameState currentState;
    private NightCycleManager nightCycle;
    private boolean isPaused = false;
    private int dayTimer;
    private final int maxDayTime = 3600;
    private ServerBossEvent dayBossBar;
    private final java.util.Map<java.util.UUID, java.util.UUID> votes = new java.util.HashMap<>();
    private final java.util.Set<java.util.UUID> skipVotes = new java.util.HashSet<>();

    public GameSession(List<ServerPlayer> serverPlayers) {
        this.serverPlayers = serverPlayers;
        this.players = serverPlayers.stream().map(ServerPlayer::getUUID).toList();
        // this.circle = new PlayerCircle(this.players);
        this.board = new GameBoard();
        this.currentState = GameState.IDLE;
    }

    // public PlayerCircle getCircle() { return circle; }

    public GameState getState() {
        return currentState;
    }

    public GameBoard getBoard() {
        return board;
    }

    public List<ServerPlayer> getServerPlayers() { return this.serverPlayers; }

    public boolean isPaused() { return this.isPaused; }

    public void togglePause() { this.isPaused = !this.isPaused; }

    public void registerSkip(net.minecraft.server.level.ServerPlayer player) {
        if (this.currentState != GameState.DAY) {
            return;
        }

        this.skipVotes.add(player.getUUID());
        int required = (int) Math.ceil(this.serverPlayers.size() * 0.66);

        for (net.minecraft.server.level.ServerPlayer sp : this.serverPlayers) {
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§e" + player.getPlainTextName() + " veut passer au vote (" + this.skipVotes.size() + "/" + required + ")."));
        }

        if (this.skipVotes.size() >= required) {
            this.dayTimer = 0;
        }
    }

    public void endNight() {
        this.currentState = GameState.DAY;
        this.dayTimer = this.maxDayTime;

        this.dayBossBar = new ServerBossEvent(
                java.util.UUID.randomUUID(),
                net.minecraft.network.chat.Component.literal("§eTemps de débat"),
                BossEvent.BossBarColor.YELLOW,
                BossEvent.BossBarOverlay.PROGRESS
        );

        for (net.minecraft.server.level.ServerPlayer sp : this.serverPlayers) {
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6Le jour se lève sur le village ! Il est temps de débattre..."));
            this.dayBossBar.addPlayer(sp);
        }
    }

    public void tick() {
        if (this.isPaused) {
            return;
        }

        if (this.currentState == GameState.NIGHT && nightCycle != null) {
            nightCycle.tick();
        } else if (this.currentState == GameState.DAY) {
            if (this.dayTimer > 0) {
                this.dayTimer--;
                this.dayBossBar.setProgress((float) this.dayTimer / this.maxDayTime);
            } else {
                this.endDay();
            }
        }
    }

    public void endDay() {
        this.currentState = GameState.END;

        if (this.dayBossBar != null) {
            this.dayBossBar.removeAllPlayers();
        }

        for (net.minecraft.server.level.ServerPlayer sp : this.serverPlayers) {
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cLe temps est écoulé ! C'est l'heure du vote..."));
            openVoteMenu(sp);
        }
    }

    private void openVoteMenu(net.minecraft.server.level.ServerPlayer player) {
        java.util.List<net.minecraft.world.item.ItemStack> options = new java.util.ArrayList<>();

        for (net.minecraft.server.level.ServerPlayer target : this.serverPlayers) {
            options.add(natjom.nocturne.util.MenuIcons.makePlayerHead(target, "§c"));
        }

        natjom.nocturne.gui.MenuHelper.openChoiceMenu(player, "§8Votez pour éliminer", options, index -> {
            net.minecraft.server.level.ServerPlayer target = this.serverPlayers.get(index);
            this.votes.put(player.getUUID(), target.getUUID());
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aTu as voté contre " + target.getPlainTextName() + "."));
            checkVotes();
        });
    }

    private void checkVotes() {
        if (this.votes.size() >= this.serverPlayers.size()) {
            resolveVotes();
        }
    }

    private void resolveVotes() {
        java.util.Map<java.util.UUID, Integer> voteCounts = new java.util.HashMap<>();

        for (java.util.UUID target : this.votes.values()) {
            voteCounts.put(target, voteCounts.getOrDefault(target, 0) + 1);
        }

        int maxVotes = 0;
        for (int count : voteCounts.values()) {
            if (count > maxVotes) {
                maxVotes = count;
            }
        }

        if (maxVotes <= 1) {
            for (net.minecraft.server.level.ServerPlayer sp : this.serverPlayers) {
                sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§eÉgalité parfaite (1 vote max). Personne n'est éliminé !"));
            }
            return;
        }

        java.util.List<java.util.UUID> eliminated = new java.util.ArrayList<>();
        for (java.util.Map.Entry<java.util.UUID, Integer> entry : voteCounts.entrySet()) {
            if (entry.getValue() == maxVotes) {
                eliminated.add(entry.getKey());
            }
        }

        for (net.minecraft.server.level.ServerPlayer sp : this.serverPlayers) {
            for (java.util.UUID deadId : eliminated) {
                net.minecraft.server.level.ServerPlayer deadPlayer = sp.level().getServer().getPlayerList().getPlayer(deadId);
                if (deadPlayer != null) {
                    sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4" + deadPlayer.getPlainTextName() + " a été éliminé avec " + maxVotes + " votes !"));
                }
            }
        }
    }


    public void start() {
        List<Role> deck = new ArrayList<>();

        deck.add(NocturneRegistries.LOUP_GAROU.get());
        int needed = this.players.size() + 3 - 1;
        for (int i = 0; i < needed; i++) {
            deck.add(NocturneRegistries.VILLAGEOIS.get());
        }

        this.board.setup(this.players, deck);
        this.currentState = GameState.NIGHT;

        for (ServerPlayer sp : serverPlayers) {
            Role role = this.board.getCurrentRole(sp.getUUID());

            sp.sendSystemMessage(Component.literal("§8================================="));
            sp.sendSystemMessage(Component.literal("§bLa partie commence !"));
            sp.sendSystemMessage(Component.literal("§eTon rôle est : §l" + role.getDisplayName().getString()));
            sp.sendSystemMessage(Component.literal("§8================================="));
            sp.sendSystemMessage(Component.literal("§7La nuit tombe sur le village..."));
        }

        this.nightCycle = new NightCycleManager(this);
        this.currentState = GameState.NIGHT;

    }

    public void stop() {
        this.currentState = GameState.END;

        if (this.dayBossBar != null) {
            this.dayBossBar.removeAllPlayers();
        }

        if (this.nightCycle != null) {
            this.nightCycle.stop();
        }

        if (this.serverPlayers != null) {
            for (net.minecraft.server.level.ServerPlayer sp : this.serverPlayers) {
                sp.level().getServer().execute(sp::closeContainer);
            }
        }
    }
}