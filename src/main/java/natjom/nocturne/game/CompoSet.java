package natjom.nocturne.game;

import natjom.nocturne.game.role.Role;
import natjom.nocturne.registry.NocturneRegistries;
import java.util.List;
import java.util.function.Supplier;

public enum CompoSet {

    PREMIERE_NUIT("§6Première Nuit", 3, 5, List.of(
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.VOYANTE,
            NocturneRegistries.VOLEUR,
            NocturneRegistries.NOISEUSE,
            NocturneRegistries.VILLAGEOIS,
            NocturneRegistries.VILLAGEOIS, // 4
            NocturneRegistries.VILLAGEOIS  // 5
    )),

    PLEINE_LUNE("§4Pleine Lune", 3, 6, List.of(
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.INSOMNIAQUE,
            NocturneRegistries.VOLEUR,
            NocturneRegistries.NOISEUSE,
            NocturneRegistries.VILLAGEOIS,
            NocturneRegistries.VILLAGEOIS, // 4
            NocturneRegistries.VOYANTE,    // 5
            NocturneRegistries.VILLAGEOIS  // 6
    )),

    NUIT_SOLITAIRE("§7Nuit Solitaire", 3, 4, List.of(
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.VOYANTE,
            NocturneRegistries.VOLEUR,
            NocturneRegistries.NOISEUSE,
            NocturneRegistries.VILLAGEOIS,
            NocturneRegistries.VILLAGEOIS,
            NocturneRegistries.VILLAGEOIS  // 4
    )),

    CONFUSION("§4Confusion", 3, 9, List.of(
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.SOULARD,
            NocturneRegistries.VOLEUR,
            NocturneRegistries.NOISEUSE,
            NocturneRegistries.VILLAGEOIS,
            NocturneRegistries.VOYANTE,    // 4
            NocturneRegistries.VILLAGEOIS, // 5
            NocturneRegistries.VILLAGEOIS, // 6
            NocturneRegistries.SOEUR,      // 7
            NocturneRegistries.SBIRE,      // 8
            NocturneRegistries.SOEUR       // 9
    )),




    CHAOS("§cChaos Total", 3, 4, List.of(
            NocturneRegistries.LOUP_GAROU, NocturneRegistries.SOULARD, NocturneRegistries.INSOMNIAQUE,
            NocturneRegistries.VOLEUR, NocturneRegistries.NOISEUSE, NocturneRegistries.VILLAGEOIS,
            NocturneRegistries.VILLAGEOIS
    ));

    private final String displayName;
    private final int minPlayers;
    private final int maxPlayers;
    private final List<Supplier<Role>> roleList;

    CompoSet(String displayName, int minPlayers, int maxPlayers, List<Supplier<Role>> roleList) {
        this.displayName = displayName;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.roleList = roleList;
    }

    public String getDisplayName() { return displayName; }
    public int getMinPlayers() { return minPlayers; }
    public int getMaxPlayers() { return maxPlayers; }
    public List<Supplier<Role>> getRoleList() { return roleList; }
}