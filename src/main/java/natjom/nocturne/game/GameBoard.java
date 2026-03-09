package natjom.nocturne.game;

import natjom.nocturne.game.role.Role;
import natjom.nocturne.game.role.crepuscule.Artefact;
import natjom.nocturne.game.role.vampire.Marque;

import java.util.*;

public class GameBoard {
    private final Map<UUID, Role> initialRoles = new HashMap<>();
    private final Map<UUID, Role> currentRoles = new HashMap<>();
    private final List<Role> centerCards = new ArrayList<>();
    private final Set<UUID> shieldedCards = new HashSet<>();
    private final Set<UUID> revealedCards = new HashSet<>();
    private final Set<UUID> playersWhoActed = new HashSet<>();
    private final List<UUID> circleOrder = new ArrayList<>();
    private final Map<UUID, Artefact> playerArtifacts = new HashMap<>();
    private final Map<UUID, Marque> playerMarques = new HashMap<>();

    public void setup(List<UUID> players, List<Role> deck) {
        Collections.shuffle(deck);

        initialRoles.clear();
        shieldedCards.clear();
        revealedCards.clear();
        playersWhoActed.clear();
        playerArtifacts.clear();
        playerMarques.clear();
        circleOrder.clear();
        circleOrder.addAll(players);

        for (int i = 0; i < 3; i++) {
            centerCards.add(deck.removeFirst());
        }

        for (UUID player : players) {
            Role assignedRole = deck.removeFirst();
            initialRoles.put(player, assignedRole);
            currentRoles.put(player, assignedRole);

            this.playerMarques.put(player, Marque.CLARTE);
        }

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

    public void addPlayerAction(UUID playerId) { this.playersWhoActed.add(playerId); }

    public Set<UUID> getPlayersWhoActed() { return this.playersWhoActed; }

    public List<UUID> getCircleOrder() { return this.circleOrder; }

    public void setArtifact(UUID player, Artefact artifact) { this.playerArtifacts.put(player, artifact); }

    public Artefact getArtifact(UUID player) { return this.playerArtifacts.get(player); }

    public boolean hasPowerCancelled(UUID player) {
        Artefact artefact = this.getArtifact(player);
        return artefact != null && artefact.isCancelsPowers();
    }

    public void setPlayerMarque(UUID player, Marque marque) {
        this.playerMarques.put(player, marque);
    }

    public Marque getPlayerMarque(UUID player) {
        return this.playerMarques.get(player);
    }

    public void swapPlayerMarques(UUID player1, UUID player2) {
        Marque m1 = this.getPlayerMarque(player1);
        Marque m2 = this.getPlayerMarque(player2);
        this.playerMarques.put(player1, m2);
        this.playerMarques.put(player2, m1);
    }

    public UUID getLeftNeighbor(UUID playerId) {
        int index = this.circleOrder.indexOf(playerId);
        if (index == -1) return null;
        int leftIndex = (index - 1 + this.circleOrder.size()) % this.circleOrder.size();
        return this.circleOrder.get(leftIndex);
    }

    public UUID getRightNeighbor(UUID playerId) {
        int index = this.circleOrder.indexOf(playerId);
        if (index == -1) return null;
        int rightIndex = (index + 1) % this.circleOrder.size();
        return this.circleOrder.get(rightIndex);
    }
}