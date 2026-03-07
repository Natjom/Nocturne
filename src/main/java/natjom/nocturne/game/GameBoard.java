package natjom.nocturne.game;

import natjom.nocturne.game.role.Role;

import java.util.*;

public class GameBoard {
    private final Map<UUID, Role> initialRoles = new HashMap<>();
    private final Map<UUID, Role> currentRoles = new HashMap<>();
    private final List<Role> centerCards = new ArrayList<>();
    private final Set<UUID> shieldedCards = new HashSet<>();
    private final Set<UUID> revealedCards = new HashSet<>();

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
        shieldedCards.clear();
        revealedCards.clear();
        initialRoles.putAll(currentRoles);
    }

    public List<Role> getAllRolesInGame() {
        List<Role> allRoles = new ArrayList<>();
        allRoles.addAll(initialRoles.values());
        allRoles.addAll(centerCards);
        return allRoles;
    }

    public Role getCurrentRole(UUID player) { return currentRoles.get(player); }

    public Role getInitialRole(UUID player) { return initialRoles.get(player); }

    public Role getCenterCard(int index) {
        if (index >= 0 && index < centerCards.size()) {
            return centerCards.get(index);
        }
        return null;
    }

    public void swapPlayerRoles(UUID player1, UUID player2) {
        Role role1 = this.getCurrentRole(player1);
        Role role2 = this.getCurrentRole(player2);

        this.currentRoles.put(player1, role2);
        this.currentRoles.put(player2, role1);
    }

    public void swapPlayerWithCenter(UUID player, int centerIndex) {
        Role playerRole = this.getCurrentRole(player);
        Role centerRole = this.getCenterCard(centerIndex);

        this.currentRoles.put(player, centerRole);
        this.centerCards.set(centerIndex, playerRole);
    }

    public void addShield(UUID playerId) {
        this.shieldedCards.add(playerId);
    }

    public boolean isShielded(UUID playerId) {
        return this.shieldedCards.contains(playerId);
    }

    public Set<UUID> getShieldedCards() {
        return this.shieldedCards;
    }

    public void setCurrentRole(UUID playerId, Role newRole) {
        this.currentRoles.put(playerId, newRole);
    }

    public void addRevealedCard(UUID playerId) { this.revealedCards.add(playerId); }

    public Set<UUID> getRevealedCards() { return this.revealedCards; }

}