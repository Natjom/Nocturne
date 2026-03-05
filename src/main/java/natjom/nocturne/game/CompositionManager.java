package natjom.nocturne.game;

import natjom.nocturne.game.role.base.LoupRole;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.game.role.base.SoeursRole;
import natjom.nocturne.game.role.base.VillageoisRole;
import natjom.nocturne.registry.NocturneRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositionManager {
    public static final Map<Role, Integer> COMPOSITION = new HashMap<>();
    public static CompoSet activeCompoSet = null;
    public static int activeCompoPlayerCount = 0;

    public static void initDefault() {
        if (COMPOSITION.isEmpty()) {
            natjom.nocturne.registry.NocturneRegistries.ROLES.getEntries().forEach(entry -> {
                COMPOSITION.put(entry.get(), 0);
            });
        }
    }

    public static void applyCompoSet(CompoSet set, int playerCount) {
        COMPOSITION.clear();
        activeCompoSet = set;
        activeCompoPlayerCount = playerCount;

        NocturneRegistries.ROLES.getEntries().forEach(entry -> COMPOSITION.put(entry.get(), 0));

        int cardsNeeded = playerCount + 3;
        List<java.util.function.Supplier<Role>> roles = set.getRoleList();

        for (int i = 0; i < Math.min(cardsNeeded, roles.size()); i++) {
            Role role = roles.get(i).get();
            COMPOSITION.put(role, COMPOSITION.getOrDefault(role, 0) + 1);
        }
    }

    public static void cycleRole(Role role) {
        activeCompoSet = null;

        int current = COMPOSITION.getOrDefault(role, 0);

        if (role instanceof VillageoisRole || role instanceof LoupRole) {
            COMPOSITION.put(role, (current + 1) % 4);
        } else if (role instanceof SoeursRole) {
            COMPOSITION.put(role, current == 0 ? 2 : 0);
        } else {
            COMPOSITION.put(role, current == 0 ? 1 : 0);
        }
    }

    public static List<Role> buildDeck() {
        List<Role> deck = new ArrayList<>();
        for (Map.Entry<Role, Integer> entry : COMPOSITION.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                deck.add(entry.getKey());
            }
        }
        return deck;
    }
}
