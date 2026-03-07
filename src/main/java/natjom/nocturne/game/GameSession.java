package natjom.nocturne.game;

import natjom.nocturne.game.role.base.ChasseurRole;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.game.role.base.SosieRole;
import natjom.nocturne.game.role.crepuscule.PoliticienRole;
import natjom.nocturne.game.role.crepuscule.ProtecteurRole;
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
    private final GameBoard board;
    private GameState currentState;
    private NightCycleManager nightCycle;
    private boolean isPaused = false;
    private int dayTimer;
    private final int maxDayTime = 8400;
    private ServerBossEvent dayBossBar;
    private final Map<UUID, UUID> votes = new HashMap<>();
    private final Set<UUID> skipVotes = new HashSet<>();
    private final List<String> gameHistory = new ArrayList<>();
    private final UUID gameMaster;
    private final List<UUID> eliminatedPlayers = new ArrayList<>();
    private boolean isWaitingForReveal = false;
    private final Set<UUID> revealedPlayers = new HashSet<>();
    private net.minecraft.world.scores.Objective compoObjective;

    public GameSession(List<ServerPlayer> serverPlayers, UUID gameMaster) {
        this.serverPlayers = serverPlayers;
        this.players = serverPlayers.stream().map(ServerPlayer::getUUID).toList();
        this.board = new GameBoard();
        this.currentState = GameState.IDLE;
        this.gameMaster = gameMaster;
    }

    public GameState getState() { return currentState; }
    public GameBoard getBoard() { return board; }
    public List<ServerPlayer> getServerPlayers() { return this.serverPlayers; }
    public UUID getGameMaster() { return this.gameMaster; }
    public boolean isPaused() { return this.isPaused; }
    public void togglePause() { this.isPaused = !this.isPaused; }

    public void registerSkip(ServerPlayer player) {
        if (this.currentState != GameState.DAY) {
            return;
        }

        this.skipVotes.add(player.getUUID());
        int required = (int) Math.ceil(this.serverPlayers.size() * 0.66);

        for (ServerPlayer sp : this.serverPlayers) {
            sp.sendSystemMessage(Component.literal("§e" + player.getPlainTextName() + " veut passer au vote (" + this.skipVotes.size() + "/" + required + ")."));
        }

        if (this.skipVotes.size() >= required) {
            this.dayTimer = 0;
        }
    }

    public void endNight() {
        this.currentState = GameState.DAY;
        this.dayTimer = this.maxDayTime;

        this.dayBossBar = new ServerBossEvent(
                UUID.randomUUID(),
                Component.literal("§eTemps de débat"),
                BossEvent.BossBarColor.YELLOW,
                BossEvent.BossBarOverlay.PROGRESS
        );

        for (ServerPlayer sp : this.serverPlayers) {
            sp.sendSystemMessage(Component.literal("§6Le jour se lève sur le village ! Il est temps de débattre..."));
            this.dayBossBar.addPlayer(sp);
            sp.playSound(net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);

            for (UUID shieldedId : this.board.getShieldedCards()) {
                ServerPlayer shieldedPlayer = sp.level().getServer().getPlayerList().getPlayer(shieldedId);
                if (shieldedPlayer != null) {
                    sp.sendSystemMessage(Component.literal("§2Un jeton Bouclier a été trouvé sur la carte de " + shieldedPlayer.getPlainTextName() + " !"));
                }
            }

            for (UUID revealedId : this.board.getRevealedCards()) {
                ServerPlayer revealedPlayer = sp.level().getServer().getPlayerList().getPlayer(revealedId);
                if (revealedPlayer != null) {
                    natjom.nocturne.game.role.Role r = this.board.getCurrentRole(revealedId);
                    sp.sendSystemMessage(Component.literal("§bLa carte de " + revealedPlayer.getPlainTextName() + " a été retournée face visible ! C'est un(e) : §l" + r.getDisplayName().getString()));
                }
            }
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

        if (this.currentState == GameState.VOTE) {
            this.dayTimer++;
            if (this.dayTimer % 20 == 0) {
                for (ServerPlayer player : this.serverPlayers) {
                    if (!this.votes.containsKey(player.getUUID())) {
                        if (player.containerMenu == player.inventoryMenu) {
                            this.openVoteMenu(player);
                        }
                    }
                }
            }
        }
    }

    public void endDay() {
        this.currentState = GameState.VOTE;

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
        List<ItemStack> options = new ArrayList<>();
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
        this.currentState = GameState.END;

        Map<UUID, Integer> voteCounts = new HashMap<>();

        for (UUID target : this.votes.values()) {
            voteCounts.put(target, voteCounts.getOrDefault(target, 0) + 1);
        }

        Set<UUID> protectedPlayers = new HashSet<>();
        for (ServerPlayer sp : this.serverPlayers) {
            Role role = this.board.getCurrentRole(sp.getUUID());

            if (role instanceof PoliticienRole || (role instanceof SosieRole && ((SosieRole) role).getCopiedRole() instanceof PoliticienRole)) {
                protectedPlayers.add(sp.getUUID());
            }

            if (role instanceof ProtecteurRole || (role instanceof SosieRole && ((SosieRole) role).getCopiedRole() instanceof ProtecteurRole)) {
                UUID protectedByBodyguard = this.votes.get(sp.getUUID());
                if (protectedByBodyguard != null) {
                    protectedPlayers.add(protectedByBodyguard);
                }
            }
        }

        List<Integer> distinctVoteCounts = voteCounts.values().stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        List<UUID> eliminated = new ArrayList<>();
        for (int count : distinctVoteCounts) {
            if (count <= 1) break;

            List<UUID> candidates = new ArrayList<>();
            for (Map.Entry<UUID, Integer> entry : voteCounts.entrySet()) {
                if (entry.getValue() == count) {
                    candidates.add(entry.getKey());
                }
            }

            List<UUID> unprotectedCandidates = candidates.stream()
                    .filter(id -> !protectedPlayers.contains(id))
                    .toList();

            if (!unprotectedCandidates.isEmpty()) {
                eliminated.addAll(unprotectedCandidates);
                break;
            }
        }

        if (eliminated.isEmpty()) {
            for (ServerPlayer sp : this.serverPlayers) {
                sp.sendSystemMessage(Component.literal("§eÉgalité parfaite. Personne n'est éliminé !"));
            }
        } else {
            List<UUID> extraEliminations = new ArrayList<>();
            for (UUID deadId : eliminated) {
                Role deadRole = this.board.getCurrentRole(deadId);

                boolean isHunter = deadRole instanceof ChasseurRole;
                boolean isSosieHunter = (deadRole instanceof SosieRole) && (((SosieRole) deadRole).getCopiedRole() instanceof ChasseurRole);

                if (isHunter || isSosieHunter) {
                    UUID hunterTarget = this.votes.get(deadId);
                    if (hunterTarget != null && !eliminated.contains(hunterTarget) && !extraEliminations.contains(hunterTarget)) {
                        extraEliminations.add(hunterTarget);
                    }
                }
            }
            eliminated.addAll(extraEliminations);
            this.eliminatedPlayers.addAll(eliminated);

            for (ServerPlayer sp : this.serverPlayers) {
                sp.removeEffect(MobEffects.RESISTANCE);
                sp.removeEffect(MobEffects.SATURATION);
                sp.removeEffect(MobEffects.WEAKNESS);

                for (UUID deadId : eliminated) {
                    ServerPlayer deadPlayer = sp.level().getServer().getPlayerList().getPlayer(deadId);
                    if (deadPlayer != null) {
                        sp.sendSystemMessage(Component.literal("§4" + deadPlayer.getPlainTextName() + " a été éliminé !"));

                        if (sp.getUUID().equals(deadId)) {
                            net.minecraft.server.level.ServerLevel sLevel = deadPlayer.level();
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
        }

        this.isWaitingForReveal = true;

        net.minecraft.network.chat.MutableComponent revealBtn = Component.literal("§a§l[Révéler mon rôle]")
                .withStyle(style -> style.withClickEvent(new net.minecraft.network.chat.ClickEvent.RunCommand("/nocturne _revealRole"))
                        .withHoverEvent(new net.minecraft.network.chat.HoverEvent.ShowText(Component.literal("§eClique pour révéler ton rôle final au village !"))));

        net.minecraft.network.chat.MutableComponent endBtn = Component.literal("§6§l[Afficher les gagnants et l'Historique]")
                .withStyle(style -> style.withClickEvent(new net.minecraft.network.chat.ClickEvent.RunCommand("/nocturne _endGame"))
                        .withHoverEvent(new net.minecraft.network.chat.HoverEvent.ShowText(Component.literal("§eClique pour terminer la partie !"))));

        for (ServerPlayer sp : this.serverPlayers) {
            sp.sendSystemMessage(Component.literal(""));
            sp.sendSystemMessage(revealBtn);
            if (sp.getUUID().equals(this.gameMaster)) {
                sp.sendSystemMessage(endBtn);
            }
        }
    }

    public void revealPlayerRole(ServerPlayer player) {
        if (!this.isWaitingForReveal || this.revealedPlayers.contains(player.getUUID())) {
            return;
        }

        this.revealedPlayers.add(player.getUUID());
        Role finalRole = this.board.getCurrentRole(player.getUUID());

        for (ServerPlayer sp : this.serverPlayers) {
            sp.sendSystemMessage(Component.literal("§d" + player.getPlainTextName() + " révèle son rôle : §l" + finalRole.getDisplayName().getString()));
            sp.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        }

        if (this.compoObjective != null && !this.serverPlayers.isEmpty()) {
            net.minecraft.world.scores.Scoreboard scoreboard = this.serverPlayers.get(0).level().getServer().getScoreboard();
            scoreboard.removeObjective(this.compoObjective);
            this.compoObjective = null;
        }
    }

    public void revealWinnersAndHistory() {
        if (!this.isWaitingForReveal) {
            return;
        }

        this.isWaitingForReveal = false;
        this.addHistory("§e--- Résultats ---");

        for (ServerPlayer sp : this.serverPlayers) {
            Role finalRole = this.board.getCurrentRole(sp.getUUID());
            boolean won = finalRole.didWin(this, sp.getUUID(), this.eliminatedPlayers);

            if (won) {
                sp.sendSystemMessage(Component.literal("§a§lVICTOIRE ! §r§aTu as gagné."));
                this.addHistory(sp.getPlainTextName() + " a GAGNÉ avec le rôle " + finalRole.getDisplayName().getString());
                sp.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
            } else {
                sp.sendSystemMessage(Component.literal("§c§lDÉFAITE ! §r§cTu as perdu."));
                this.addHistory(sp.getPlainTextName() + " a PERDU avec le rôle " + finalRole.getDisplayName().getString());
                sp.playSound(SoundEvents.VILLAGER_NO, 1.0F, 1.0F);
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
                sp.sendSystemMessage(Component.literal("§cImpossible de lancer ! Il y a " + deck.size() + " rôles sélectionnés pour " + this.players.size() + " joueurs."));
                sp.sendSystemMessage(Component.literal("§cIl faut exactement " + requiredCards + " cartes. Modifiez la /nocturne compo."));
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


        net.minecraft.server.MinecraftServer server = this.serverPlayers.get(0).level().getServer();
        net.minecraft.world.scores.Scoreboard scoreboard = server.getScoreboard();

        net.minecraft.world.scores.Objective oldObj = scoreboard.getObjective("nocturne_compo");
        if (oldObj != null) {
            scoreboard.removeObjective(oldObj);
        }

        this.compoObjective = scoreboard.addObjective(
                "nocturne_compo",
                net.minecraft.world.scores.criteria.ObjectiveCriteria.DUMMY,
                Component.literal("§6§lRôles en Jeu"),
                net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER,
                false,
                null
        );

        scoreboard.setDisplayObjective(net.minecraft.world.scores.DisplaySlot.SIDEBAR, this.compoObjective);

        List<Map.Entry<Role, Integer>> activeRoles = new ArrayList<>();
        for (Map.Entry<Role, Integer> entry : natjom.nocturne.game.CompositionManager.COMPOSITION.entrySet()) {
            if (entry.getValue() > 0) {
                activeRoles.add(entry);
            }
        }

        activeRoles.sort((e1, e2) -> {
            int o1 = e1.getKey().getNightOrder();
            int o2 = e2.getKey().getNightOrder();
            if (o1 == 0) o1 = 999;
            if (o2 == 0) o2 = 999;
            return Integer.compare(o1, o2);
        });

        int score = activeRoles.size();
        for (Map.Entry<Role, Integer> entry : activeRoles) {
            Role role = entry.getKey();
            int count = entry.getValue();

            String orderStr = role.getNightOrder() > 0 ? "§8[" + role.getNightOrder() + "] §r" : "§8[-] §r";
            String countStr = count > 1 ? " §ex" + count : "";
            String line = orderStr + role.getDisplayName().getString() + countStr;

            scoreboard.getOrCreatePlayerScore(net.minecraft.world.scores.ScoreHolder.forNameOnly(line), this.compoObjective).set(score--);
        }

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

        if (this.compoObjective != null && !this.serverPlayers.isEmpty()) {
            net.minecraft.world.scores.Scoreboard scoreboard = this.serverPlayers.get(0).level().getServer().getScoreboard();
            scoreboard.removeObjective(this.compoObjective);
            this.compoObjective = null;
        }
    }

    public void addHistory(String event) {
        this.gameHistory.add(event);
    }

    public void displayHistory() {
        this.addHistory("§e--- Rôles Finaux ---");
        for (net.minecraft.server.level.ServerPlayer sp : this.serverPlayers) {
            natjom.nocturne.game.role.Role finalRole = this.board.getCurrentRole(sp.getUUID());
            this.addHistory(sp.getPlainTextName() + " termine en tant que : " + finalRole.getDisplayName().getString());
        }
        for (int i = 0; i < 3; i++) {
            natjom.nocturne.game.role.Role centerRole = this.board.getCenterCard(i);
            this.addHistory("Centre " + (i + 1) + " : " + centerRole.getDisplayName().getString());
        }

        this.addHistory("§e--- Historique des Votes ---");
        for (java.util.Map.Entry<java.util.UUID, java.util.UUID> entry : this.votes.entrySet()) {
            net.minecraft.server.level.ServerPlayer voter = this.serverPlayers.stream().filter(p -> p.getUUID().equals(entry.getKey())).findFirst().orElse(null);
            net.minecraft.server.level.ServerPlayer target = this.serverPlayers.stream().filter(p -> p.getUUID().equals(entry.getValue())).findFirst().orElse(null);
            if (voter != null && target != null) {
                this.addHistory("§7" + voter.getPlainTextName() + " a voté contre " + target.getPlainTextName());
            }
        }
        this.addHistory("§e-----------------------------");

        for (net.minecraft.server.level.ServerPlayer sp : this.serverPlayers) {
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§8=== [ Résumé de la Partie ] ==="));
            for (String event : this.gameHistory) {
                sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7- " + event));
            }
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§8=========================="));
        }
    }
}