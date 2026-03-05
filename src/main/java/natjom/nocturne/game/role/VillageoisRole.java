package natjom.nocturne.game.role;

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
        return "";
    }
}