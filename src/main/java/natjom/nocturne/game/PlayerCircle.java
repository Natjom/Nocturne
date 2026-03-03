package natjom.nocturne.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerCircle {
    private final List<UUID> order;

    public PlayerCircle(List<UUID> players) {
        this.order = new ArrayList<>(players);
    }

    public UUID getLeftNeighbor(UUID player) {
        int idx = order.indexOf(player);
        return order.get((idx - 1 + order.size()) % order.size());
    }

    public UUID getRightNeighbor(UUID player) {
        int idx = order.indexOf(player);
        return order.get((idx + 1) % order.size());
    }
}