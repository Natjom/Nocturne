package natjom.nocturne.game.role;

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
        return "";
    }
}