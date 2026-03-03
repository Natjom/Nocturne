package natjom.nocturne.game;

import natjom.nocturne.game.role.Role;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameBoard {
    private final Map<UUID, Role> initialRoles = new HashMap<>();
    private final Map<UUID, Role> currentRoles = new HashMap<>();
    private final List<Role> centerCards = new ArrayList<>();

    public void setup(List<UUID> players, List<Role> deck) {
        Collections.shuffle(deck);

        for (int i = 0; i < 3; i++) {
            centerCards.add(deck.remove(0));
        }

        for (UUID player : players) {
            Role assignedRole = deck.remove(0);
            initialRoles.put(player, assignedRole);
            currentRoles.put(player, assignedRole);
        }
    }

    public Role getCurrentRole(UUID player) {
        return currentRoles.get(player);
    }

    public List<Role> getCenterCards() {
        return centerCards;
    }
}