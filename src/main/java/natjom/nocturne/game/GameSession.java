package natjom.nocturne.game;

import natjom.nocturne.game.role.base.ChasseurRole;
import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

import java.util.*;

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
    private final java.util.List<String> gameHistory = new java.util.ArrayList<>();

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

    public void registerSkip(ServerPlayer player) {
        if (this.currentState != GameState.DAY) {
            return;
        }

        this.skipVotes.add(player.getUUID());
        int required = (int) Math.ceil(this.serverPlayers.size() * 0.66);

        for (ServerPlayer sp : this.serverPlayers) {
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

        for (ServerPlayer sp : this.serverPlayers) {
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6Le jour se lève sur le village ! Il est temps de débattre..."));
            this.dayBossBar.addPlayer(sp);
            sp.playSound(net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);
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


        for (ServerPlayer sp : this.serverPlayers) {
            sp.sendSystemMessage(Component.literal("§cLe temps est écoulé ! C'est l'heure du vote..."));
            sp.playSound(SoundEvents.BELL_BLOCK, 1.0F, 1.0F);
            openVoteMenu(sp);
        }
    }

    private void openVoteMenu(ServerPlayer player) {
        java.util.List<ItemStack> options = new java.util.ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : this.serverPlayers) {
            if (!target.getUUID().equals(player.getUUID())) {
                options.add(natjom.nocturne.util.MenuIcons.makePlayerHead(target, "§c"));
                validTargets.add(target);
            }
        }

        natjom.nocturne.gui.MenuHelper.openChoiceMenu(player, "§8Votez pour éliminer", options, index -> {
            ServerPlayer target = validTargets.get(index);
            this.votes.put(player.getUUID(), target.getUUID());
            player.sendSystemMessage(Component.literal("§aTu as voté contre " + target.getPlainTextName() + "."));
            checkVotes();
        });
    }

    private void checkVotes() {
        if (this.votes.size() >= this.serverPlayers.size()) {
            resolveVotes();
        }
    }

    private void resolveVotes() {
        Map<UUID, Integer> voteCounts = new HashMap<>();

        for (UUID target : this.votes.values()) {
            voteCounts.put(target, voteCounts.getOrDefault(target, 0) + 1);
        }

        int maxVotes = 0;
        for (int count : voteCounts.values()) {
            if (count > maxVotes) {
                maxVotes = count;
            }
        }

        if (maxVotes <= 1) {
            for (ServerPlayer sp : this.serverPlayers) {
                sp.sendSystemMessage(Component.literal("§eÉgalité parfaite (1 vote max). Personne n'est éliminé !"));
            }
            return;
        }

        List<UUID> eliminated = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : voteCounts.entrySet()) {
            if (entry.getValue() == maxVotes) {
                eliminated.add(entry.getKey());
            }
        }

        List<UUID> extraEliminations = new ArrayList<>();
        for (UUID deadId : eliminated) {
            Role deadRole = this.board.getCurrentRole(deadId);
            if (deadRole instanceof ChasseurRole) {
                java.util.UUID hunterTarget = this.votes.get(deadId);
                if (hunterTarget != null && !eliminated.contains(hunterTarget) && !extraEliminations.contains(hunterTarget)) {
                    extraEliminations.add(hunterTarget);
                }
            }
        }
        eliminated.addAll(extraEliminations);

        for (ServerPlayer sp : this.serverPlayers) {
            sp.removeEffect(MobEffects.RESISTANCE);
            sp.removeEffect(MobEffects.SATURATION);
            sp.removeEffect(MobEffects.WEAKNESS);

            for (java.util.UUID deadId : eliminated) {
                net.minecraft.server.level.ServerPlayer deadPlayer = sp.level().getServer().getPlayerList().getPlayer(deadId);
                if (deadPlayer != null) {
                    sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4" + deadPlayer.getPlainTextName() + " a été éliminé !"));

                    if (sp.getUUID().equals(deadId)) {
                        net.minecraft.server.level.ServerLevel sLevel = (net.minecraft.server.level.ServerLevel) deadPlayer.level();
                        net.minecraft.world.entity.LightningBolt lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(sLevel, net.minecraft.world.entity.EntitySpawnReason.COMMAND);

                        if (lightning != null) {
                            lightning.setPos(deadPlayer.getX(), deadPlayer.getY(), deadPlayer.getZ());
                            lightning.setVisualOnly(true);
                            sLevel.addFreshEntity(lightning);
                        }
                    }
                }
            }
        }

        this.displayHistory();
    }


    public void start() {
        natjom.nocturne.game.CompositionManager.initDefault();
        List<natjom.nocturne.game.role.Role> deck = natjom.nocturne.game.CompositionManager.buildDeck();

        int requiredCards = this.players.size() + 3;
        if (deck.size() != requiredCards) {
            for (ServerPlayer sp : this.serverPlayers) {
                sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cImpossible de lancer ! Il y a " + deck.size() + " rôles sélectionnés pour " + this.players.size() + " joueurs."));
                sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cIl faut exactement " + requiredCards + " cartes. Modifiez la /nocturne compo."));
            }
            this.currentState = GameState.IDLE;
            return;
        }

        this.board.setup(this.players, deck);

        this.addHistory("§e--- Distribution Initiale ---");
        for (ServerPlayer sp : this.serverPlayers) {
            natjom.nocturne.game.role.Role initialRole = this.board.getInitialRole(sp.getUUID());
            this.addHistory(sp.getPlainTextName() + " a reçu : " + initialRole.getDisplayName().getString());
        }
        for (int i = 0; i < 3; i++) {
            natjom.nocturne.game.role.Role centerRole = this.board.getCenterCard(i);
            this.addHistory("Centre " + (i + 1) + " : " + centerRole.getDisplayName().getString());
        }
        this.addHistory("§e-----------------------------");

        this.currentState = GameState.NIGHT;

        for (ServerPlayer sp : serverPlayers) {
            Role role = this.board.getCurrentRole(sp.getUUID());

            sp.sendSystemMessage(Component.literal("§8================================="));
            sp.sendSystemMessage(Component.literal("§bLa partie commence !"));
            sp.sendSystemMessage(Component.literal("§eTon rôle est : §l" + role.getDisplayName().getString()));
            sp.sendSystemMessage(Component.literal("§8================================="));
            sp.sendSystemMessage(Component.literal("§7La nuit tombe sur le village..."));

            sp.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.RESISTANCE, -1, 255, false, false, false));
            sp.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.SATURATION, -1, 255, false, false, false));
            sp.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, -1, 255, false, false, false));

            sp.playSound(net.minecraft.sounds.SoundEvents.WOLF_GROWL_BABY.value(), 1.0F, 0.5F);


        }

        this.nightCycle = new NightCycleManager(this);
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
            for (ServerPlayer sp : this.serverPlayers) {
                sp.level().getServer().execute(() -> {
                    sp.closeContainer();
                    sp.removeEffect(net.minecraft.world.effect.MobEffects.RESISTANCE);
                    sp.removeEffect(net.minecraft.world.effect.MobEffects.SATURATION);
                    sp.removeEffect(net.minecraft.world.effect.MobEffects.WEAKNESS);
                });
            }
        }
    }

    public void addHistory(String event) {
        this.gameHistory.add(event);
    }

    public void displayHistory() {
        this.addHistory("§e--- Rôles Finaux ---");
        for (ServerPlayer sp : this.serverPlayers) {
            natjom.nocturne.game.role.Role finalRole = this.board.getCurrentRole(sp.getUUID());
            this.addHistory(sp.getPlainTextName() + " termine en tant que : " + finalRole.getDisplayName().getString());
        }
        for (int i = 0; i < 3; i++) {
            natjom.nocturne.game.role.Role centerRole = this.board.getCenterCard(i);
            this.addHistory("Centre " + (i + 1) + " : " + centerRole.getDisplayName().getString());
        }
        this.addHistory("§e-----------------------------");

        for (ServerPlayer sp : this.serverPlayers) {
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§8=== [ Résumé de la Partie ] ==="));
            for (String event : this.gameHistory) {
                sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7- " + event));
            }
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§8=========================="));
        }
    }
}