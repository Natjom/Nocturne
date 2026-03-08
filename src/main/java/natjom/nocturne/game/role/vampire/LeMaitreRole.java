package natjom.nocturne.game.role.vampire;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class LeMaitreRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§5Le Maître");
    }

    @Override
    public int getNightOrder() {
        return 3;
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
        java.util.List<String> vampireNames = session.getServerPlayers().stream()
                .filter(p -> !p.getUUID().equals(player.getUUID()))
                .filter(p -> {
                    Role r = session.getBoard().getInitialRole(p.getUUID());
                    return r instanceof VampireRole || r instanceof LeMaitreRole || r instanceof LeComteRole;
                })
                .map(net.minecraft.world.entity.player.Player::getPlainTextName)
                .toList();

        if (!vampireNames.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cLes autres Vampires sont : " + String.join(", ", vampireNames)));
        } else {
            player.sendSystemMessage(Component.literal("§cTu es le seul Vampire ce soir."));
        }

        session.getBoard().addPlayerAction(player.getUUID());
    }
}