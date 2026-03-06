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
            NocturneRegistries.SOEUR,      // Fait office de villageois placeholder tant qu'ils ne sont pas 9 joueurs.
            NocturneRegistries.VOYANTE,    // 4
            NocturneRegistries.VILLAGEOIS, // 5
            NocturneRegistries.VILLAGEOIS, // 6
            NocturneRegistries.VILLAGEOIS, // 7
            NocturneRegistries.SBIRE,      // 8
            NocturneRegistries.SOEUR       // 9
    )),

    BENEFICE("§3Bénéfice", 4, 7, List.of(
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.CHASSEUR,
            NocturneRegistries.VOYANTE,
            NocturneRegistries.VOLEUR,
            NocturneRegistries.SOULARD,
            NocturneRegistries.INSOMNIAQUE,
            NocturneRegistries.NOISEUSE,   // 5
            NocturneRegistries.VILLAGEOIS, // 6
            NocturneRegistries.VILLAGEOIS  // 7
    )),

    MYSTERIEUX_COMPAGNONS("§bMystérieux Compagnons", 6, 7, List.of(
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.SBIRE,
            NocturneRegistries.CHASSEUR,
            NocturneRegistries.VOYANTE,
            NocturneRegistries.VOLEUR,
            NocturneRegistries.NOISEUSE,
            NocturneRegistries.SOEUR,
            NocturneRegistries.SOEUR,
            NocturneRegistries.VILLAGEOIS  // 7
    )),

    INCERTITUDE("§2Incertitude", 4, 10, List.of(
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.TANNEUR,
            NocturneRegistries.VOYANTE,
            NocturneRegistries.VOLEUR,
            NocturneRegistries.SOULARD,
            NocturneRegistries.INSOMNIAQUE,
            NocturneRegistries.NOISEUSE,    // 5
            NocturneRegistries.SOEUR,       // 6
            NocturneRegistries.SOEUR,       // 7
            NocturneRegistries.CHASSEUR,    // 8
            NocturneRegistries.SBIRE,       // 9
            NocturneRegistries.VILLAGEOIS   // 10

    )),

    REVENANTS("§4Revenants", 8, 10, List.of(
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.SOSIE,
            NocturneRegistries.SBIRE,
            NocturneRegistries.CHASSEUR,
            NocturneRegistries.VOYANTE,
            NocturneRegistries.VOLEUR,
            NocturneRegistries.NOISEUSE,
            NocturneRegistries.VILLAGEOIS,
            NocturneRegistries.SOEUR,
            NocturneRegistries.SOEUR,
            NocturneRegistries.INSOMNIAQUE, // 9
            NocturneRegistries.SOULARD      // 10
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