package natjom.nocturne.game.role.crepuscule;

import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;

public class ProtecteurRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aProtecteur");
    }

    @Override
    public int getNightOrder() {
        return 0;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQzYjgzMTNjY2VlYTU3NzcwZDg4MTUxOGVkODkyZDJmYzllMTU3NDE0NmUzZGVhODk3YjY3YTIzODNiMTRkOSJ9fX0=";
    }
}