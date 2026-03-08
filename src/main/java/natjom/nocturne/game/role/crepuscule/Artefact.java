package natjom.nocturne.game.role.crepuscule;

public enum Artefact {
    GRIFFE_LOUP_GAROU("§cGriffe du Loup-Garou", true),
    MARQUE_VILLAGEOIS("§aMarque du Villageois", true),
    GOURDIN_TANNEUR("§6Gourdin du Tanneur", true),
    BROUILLARD_NEANT("§7Brouillard du Néant", false),
    MASQUE_SILENCE("§8Masque du Silence", false);

    private final String displayName;
    private final boolean cancelsPowers;

    Artefact(String displayName, boolean cancelsPowers) {
        this.displayName = displayName;
        this.cancelsPowers = cancelsPowers;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCancelsPowers() {
        return cancelsPowers;
    }
}