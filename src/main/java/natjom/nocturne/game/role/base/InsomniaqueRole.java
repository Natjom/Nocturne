package natjom.nocturne.game.role.base;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class InsomniaqueRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Insomniaque");
    }

    @Override
    public int getNightOrder() {
        return 34;
    }

    @Override
    public int getActionDuration() { return 60; }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODU2NjdjMzU1MmZhNmY4OGZkYjc1MDNjMjhjZDE1YTA3MDE1Y2JjNzA0ZmMzMTk4YTQ4ZDE5YjU5ZGFmNjUyOSJ9fX0=";
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