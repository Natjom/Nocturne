package natjom.nocturne.game.role;

import natjom.nocturne.game.GameSession;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class WolfRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Loup-Garou");
    }

    @Override
    public int getNightOrder() {
        return 10;
    }

    @Override
    public String getSkinTexture() {
        return "";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        player.sendSystemMessage(Component.literal("§c[Nuit] Tu te réveilles. Tu es un Loup-Garou !"));
    }
}