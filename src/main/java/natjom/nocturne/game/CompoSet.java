package natjom.nocturne.game;

import natjom.nocturne.game.role.Role;

import java.util.List;
import java.util.function.Supplier;

import static natjom.nocturne.registry.NocturneRegistries.*;

public enum CompoSet {

    PREMIERE_NUIT("§6Première Nuit", 3, 5, List.of(
            LOUP_GAROU,
            LOUP_GAROU,
            VOYANTE,
            VOLEUR,
            NOISEUSE,
            VILLAGEOIS,
            VILLAGEOIS, // 4
            VILLAGEOIS  // 5
    )),

    PLEINE_LUNE("§4Pleine Lune", 3, 6, List.of(
            LOUP_GAROU,
            LOUP_GAROU,
            INSOMNIAQUE,
            VOLEUR,
            NOISEUSE,
            VILLAGEOIS,
            VILLAGEOIS, // 4
            VOYANTE,    // 5
            VILLAGEOIS  // 6
    )),

    NUIT_SOLITAIRE("§7Nuit Solitaire", 3, 4, List.of(
            LOUP_GAROU,
            VOYANTE,
            VOLEUR,
            NOISEUSE,
            VILLAGEOIS,
            VILLAGEOIS,
            VILLAGEOIS  // 4
    )),

    CONFUSION("§4Confusion", 3, 9, List.of(
            LOUP_GAROU,
            LOUP_GAROU,
            SOULARD,
            VOLEUR,
            NOISEUSE,
            SOEUR,      // Fait office de villageois placeholder tant qu'ils ne sont pas 9 joueurs.
            VOYANTE,    // 4
            VILLAGEOIS, // 5
            VILLAGEOIS, // 6
            VILLAGEOIS, // 7
            SBIRE,      // 8
            SOEUR       // 9
    )),

    BENEFICE("§3Bénéfice", 4, 7, List.of(
            LOUP_GAROU,
            LOUP_GAROU,
            CHASSEUR,
            VOYANTE,
            VOLEUR,
            SOULARD,
            INSOMNIAQUE,
            NOISEUSE,   // 5
            VILLAGEOIS, // 6
            VILLAGEOIS  // 7
    )),

    MYSTERIEUX_COMPAGNONS("§bMystérieux Compagnons", 6, 7, List.of(
            LOUP_GAROU,
            LOUP_GAROU,
            SBIRE,
            CHASSEUR,
            VOYANTE,
            VOLEUR,
            NOISEUSE,
            SOEUR,
            SOEUR,
            VILLAGEOIS  // 7
    )),

    INCERTITUDE("§2Incertitude", 4, 10, List.of(
            LOUP_GAROU,
            LOUP_GAROU,
            TANNEUR,
            VOYANTE,
            VOLEUR,
            SOULARD,
            INSOMNIAQUE,
            NOISEUSE,    // 5
            SOEUR,       // 6
            SOEUR,       // 7
            CHASSEUR,    // 8
            SBIRE,       // 9
            VILLAGEOIS   // 10

    )),

    REVENANTS("§4Revenants", 8, 10, List.of(
            LOUP_GAROU,
            LOUP_GAROU,
            SOSIE,
            SBIRE,
            CHASSEUR,
            VOYANTE,
            VOLEUR,
            NOISEUSE,
            VILLAGEOIS,
            SOEUR,
            SOEUR,
            INSOMNIAQUE, // 9
            SOULARD      // 10
    )),

    SOMBRE_REVEIL("§8Sombre Réveil", 3, 6, List.of(
            LOUP_GAROU,
            LOUP_SHAMAN,
            DIVINATEUR,
            SORCIERE,
            APPRENTIE_VOYANTE,
            VILLAGEOIS,
            GUETTEUR,    // 4
            VILLAGEOIS,  // 5
            LOUP_REVEUR  // 6
    )),

    LA_NUIT_DU_LOUP_GAROU("§bLa nuit du Loup-Garou", 5, 10, List.of(
            VILLAGEOIS,
            LOUP_SHAMAN,
            LOUP_ALPHA,
            GUETTEUR,
            APPRENTIE_VOYANTE,
            DIVINATEUR,
            SORCIERE,
            COMPTEUSE,
            PROTECTEUR,   // 6
            EXORCISTE,    // 7
            CONSERVATEUR, // 8
            LOUP_REVEUR,  // 9
            POLITICIEN    // 10
    )),

    UN_TERRIBLE_ENNEMI("§cUn terrible ennemi", 3, 4, List.of(
            LOUP_ALPHA,
            SORCIERE,
            LE_TOM,
            APPRENTIE_VOYANTE,
            GUETTEUR,
            PROTECTEUR,
            DIVINATEUR  // 4
    )),

    ALLIANCES_FRAGILES("§3Alliances fragiles", 3, 7, List.of(
            LOUP_ALPHA,
            SORCIERE,
            CONSERVATEUR,
            EXORCISTE,
            DIVINATEUR,
            LE_TOM,
            COMPTEUSE,         // 4
            GUETTEUR,          // 5
            APPRENTIE_VOYANTE, // 6
            LOUP_SHAMAN        // 7
    )),



    CLOSE("A",0,0, List.of());

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