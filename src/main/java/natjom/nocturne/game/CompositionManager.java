package natjom.nocturne.game;

import natjom.nocturne.game.role.LoupRole;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.game.role.SoeursRole;
import natjom.nocturne.game.role.VillageoisRole;
import natjom.nocturne.registry.NocturneRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositionManager {
    public static final Map<Role, Integer> COMPOSITION = new HashMap<>();

    public static void initDefault() {
        if (COMPOSITION.isEmpty()) {
            NocturneRegistries.ROLES.getEntries().forEach(entry -> {
                Role role = entry.get();
                if (role instanceof LoupRole || role instanceof VillageoisRole) {
                    COMPOSITION.put(role, 1);
                } else {
                    COMPOSITION.put(role, 0);
                }
            });
        }
    }

    public static void cycleRole(Role role) {
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
