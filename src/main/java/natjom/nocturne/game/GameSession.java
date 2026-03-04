package natjom.nocturne.game;

import natjom.nocturne.registry.NocturneRegistries;
import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameSession {
    private final List<ServerPlayer> serverPlayers;
    private final List<UUID> players;
    private final PlayerCircle circle;
    private final GameBoard board;
    private GameState currentState;
    private NightCycleManager nightCycle;

    public GameSession(List<ServerPlayer> serverPlayers) {
        this.serverPlayers = serverPlayers;
        this.players = serverPlayers.stream().map(ServerPlayer::getUUID).toList();
        this.circle = new PlayerCircle(this.players);
        this.board = new GameBoard();
        this.currentState = GameState.IDLE;
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

    public void tick() {
        if (this.currentState == GameState.NIGHT && nightCycle != null) {
            nightCycle.tick();
        }
    }

    public PlayerCircle getCircle() {
        return circle;
    }

    public GameState getState() {
        return currentState;
    }

    public GameBoard getBoard() {
        return board;
    }

    public List<ServerPlayer> getServerPlayers() { return this.serverPlayers; }

    public void endNight() {
        this.currentState = GameState.DAY;
        for (ServerPlayer sp : this.serverPlayers) {
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6Le jour se lève sur le village ! Il est temps de débattre..."));
        }
    }
}