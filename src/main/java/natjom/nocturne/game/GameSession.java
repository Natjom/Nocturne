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

            sp.sendOverlayMessage(Component.literal("§bTon rôle est : §l" + role.getDisplayName().getString()));
            sp.sendSystemMessage(Component.literal("§7La nuit tombe sur le village..."));
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
}