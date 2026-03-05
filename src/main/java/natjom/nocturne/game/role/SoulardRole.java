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
        return 33;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmE4MGYwNzMyZmM0NzVjZGJlYjdhMDA3ZjFjYmE2MjZhYzdkZmRiZDUxZDY5ZmI3N2NiZjUyNDMxZWYxNDcyYyJ9fX0=";
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