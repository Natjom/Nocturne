package natjom.nocturne.game.role.base;

import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;

public class TanneurRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Tanneur");
    }

    @Override
    public int getNightOrder() {
        return 0;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTczMjUyMDY4ODA0MWU3YzIxMWI5YWNkMjc4YTA0MTZjOWNmNmRmY2U5OGQ0YmZhNTI4NmYxNWViMzg2M2YzOCJ9fX0=";
    }
}