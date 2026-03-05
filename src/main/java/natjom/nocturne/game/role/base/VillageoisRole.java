package natjom.nocturne.game.role.base;

import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;

public class VillageoisRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Villageois");
    }

    @Override
    public int getNightOrder() {
        return 0;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjY5YWYxYTk5M2I1NGQwMGUxOTQwZDA5YjM5MmNkMzY1MDFiMTlkOWJjOWVmNDk5Y2I2YTY5MmM3MTU5OGJlMCJ9fX0=";
    }
}