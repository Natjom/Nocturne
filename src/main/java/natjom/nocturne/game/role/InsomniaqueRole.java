package natjom.nocturne.game.role;

import natjom.nocturne.game.GameSession;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class InsomniaqueRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Insomniaque");
    }

    @Override
    public int getNightOrder() {
        return 70;
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
        Role currentRole = session.getBoard().getCurrentRole(player.getUUID());

        player.sendSystemMessage(Component.literal("§c[Nuit] Impossible de dormir... Tu regardes ta propre carte :"));
        player.sendSystemMessage(Component.literal("§dTu es actuellement : §l" + currentRole.getDisplayName().getString()));

        session.addHistory("L'Insomniaque (" + player.getPlainTextName() + ") s'est regardé et a vu : " + currentRole.getDisplayName().getString() + ".");
    }
}