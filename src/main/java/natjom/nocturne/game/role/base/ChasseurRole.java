package natjom.nocturne.game.role.base;

import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;

public class ChasseurRole extends Role {

    @Override
    public Component getDisplayName() { return Component.literal("§aChasseur"); }

    @Override
    public int getNightOrder() { return 0; }

    @Override
    public String getSkinTexture() { return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjkxNmYxNTdlYWZkMGZhYTZjMzUxY2NiZWYxMmU2NDBmOGI3NzNmY2YxZGU1YzRjZjllMTVkZmI3NWUwYTZlMyJ9fX0="; }

}