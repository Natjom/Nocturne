package natjom.nocturne.game.role;

import natjom.nocturne.game.GameSession;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.Random;

public class SoulardRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Soûlard");
    }

    @Override
    public int getNightOrder() {
        return 60;
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
        int randomCenterIndex = new Random().nextInt(3);

        session.getBoard().swapPlayerWithCenter(player.getUUID(), randomCenterIndex);

        player.sendSystemMessage(Component.literal("§c[Nuit] Tu te réveilles en titubant..."));
        player.sendSystemMessage(Component.literal("§dTu as échangé ta carte avec une carte du centre au hasard. Tu ne sais pas ce que tu es devenu !"));

        session.addHistory("Le Soûlard (" + player.getPlainTextName() + ") a échangé sa carte avec le centre " + (randomCenterIndex + 1) + ".");
    }
}