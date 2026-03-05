package natjom.nocturne.game;

import natjom.nocturne.game.role.Role;
import natjom.nocturne.registry.NocturneRegistries;
import java.util.List;
import java.util.function.Supplier;

public enum CompoSet {

    CLASSIQUE("§6Classique", 3, 5, List.of(
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.LOUP_GAROU,
            NocturneRegistries.VOLEUR,
            NocturneRegistries.NOISEUSE,
            NocturneRegistries.VOYANTE,
            NocturneRegistries.VILLAGEOIS, // 3
            NocturneRegistries.VILLAGEOIS, // 4
            NocturneRegistries.VILLAGEOIS  // 5
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