package natjom.nocturne.game.role.vampire;

import net.minecraft.network.chat.Component;

public enum Marque {
    CLARTE(Component.literal("§eMarque de la Clarté")),
    ASSASSIN(Component.literal("§4Marque de l'Assassin")),
    TRAITRE(Component.literal("§5Marque du Traître")),
    AMOUR(Component.literal("§dMarque de l'Amour")),
    PEUR(Component.literal("§9Marque de la Peur")),
    PESTE(Component.literal("§2Marque de la Peste")),
    CHAUVE_SOURIS(Component.literal("§8Marque de la Chauve-souris")),
    VAMPIRE(Component.literal("§cMarque du Vampire"));

    private final Component displayName;

    Marque(Component displayName) {
        this.displayName = displayName;
    }

    public Component getDisplayName() {
        return this.displayName;
    }
}