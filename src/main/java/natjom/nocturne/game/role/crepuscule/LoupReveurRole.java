package natjom.nocturne.game.role.crepuscule;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.game.role.base.LoupRole;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;


public class LoupReveurRole extends LoupRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("Loup Rêveur");
    }

    @Override
    public int getNightOrder() {
        return 14; // embourbé
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzViMGY5MzA1Mjg5NmM3MWM0ZmFkMGNkMjY3ZmRlMDI1M2IzMThiNWFiNDE1NmI5MDMyY2ZlOGRmYzQ3ZWMwIn19fQ==";
    }

    @Override
    public boolean hasNightAction() {
        return false;
    }
}