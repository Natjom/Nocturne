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
            centerCards.add(deck.removeFirst());
        }

        for (UUID player : players) {
            Role assignedRole = deck.removeFirst();
            initialRoles.put(player, assignedRole);
            currentRoles.put(player, assignedRole);
        }

        initialRoles.clear();
        initialRoles.putAll(currentRoles);
    }

    public List<Role> getAllRolesInGame() {
        List<Role> allRoles = new ArrayList<>();
        allRoles.addAll(initialRoles.values());
        allRoles.addAll(centerCards);
        return allRoles;
    }

    public Role getCurrentRole(UUID player) {
        return currentRoles.get(player);
    }

    public Role getInitialRole(UUID player) { return initialRoles.get(player); }

    public Role getCenterCard(int index) {
        if (index >= 0 && index < centerCards.size()) {
            return centerCards.get(index);
        }
        return null;
    }

    public void swapPlayerRoles(java.util.UUID player1, java.util.UUID player2) {
        natjom.nocturne.game.role.Role role1 = this.getCurrentRole(player1);
        natjom.nocturne.game.role.Role role2 = this.getCurrentRole(player2);

        this.currentRoles.put(player1, role2);
        this.currentRoles.put(player2, role1);
    }

}