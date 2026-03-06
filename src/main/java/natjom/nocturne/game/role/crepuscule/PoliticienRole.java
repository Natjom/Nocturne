package natjom.nocturne.game.role.crepuscule;

import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;

public class PoliticienRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Politicien");
    }

    @Override
    public int getNightOrder() {
        return 0;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDAyOTBmYjZhMzQ4OGIzZmNmNzc2NjgxMTAwMGZiY2Q3Mjk3ZTRmMTMzZjZiOGE4YTBjMjQ4NTYzMDFhYTdjNyJ9fX0=";
    }
}