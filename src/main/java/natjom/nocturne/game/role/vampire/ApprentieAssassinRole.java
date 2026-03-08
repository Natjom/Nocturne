package natjom.nocturne.game.role.vampire;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class ApprentieAssassinRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§cApprentie Assassin");
    }

    @Override
    public int getNightOrder() {
        return 11;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzVlY2MwMDRhODE4OTgyYzY4N2RiZjA0ODY1YjhkYjczZWZlYTE3OWUyZDRlY2Y0NDc1NDNiMDc3NjgxM2EzYSJ9fX0=";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        Optional<ServerPlayer> targetPlayer = session.getServerPlayers().stream()
                .filter(p -> session.getBoard().getPlayerMarque(p.getUUID()) == Marque.ASSASSIN)
                .findFirst();

        if (targetPlayer.isPresent()) {
            player.sendSystemMessage(Component.literal("§cLa cible marquée par l'Assassin est : " + targetPlayer.get().getPlainTextName()));
        } else {
            player.sendSystemMessage(Component.literal("§cAucune Marque de l'Assassin n'a été placée ce soir."));
        }

        session.getBoard().addPlayerAction(player.getUUID());
    }
}