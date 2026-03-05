package natjom.nocturne.game.role.base;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;
import java.util.UUID;

public class SbireRole extends Role {

    @Override
    public Component getDisplayName() { return Component.literal("Sbire"); }

    @Override
    public int getNightOrder() { return 17; }

    @Override
    public String getSkinTexture() { return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVhOWQ3NmMwY2NlNjhhNTNhNGJiNDUwMmU0YzUzYWE2OTFjZTZkMmExYzI3YzZhZTY4MGEwZTdjZDUzY2Y5ZSJ9fX0="; }

    @Override
    public boolean hasNightAction() { return true; }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<String> wolfNames = session.getServerPlayers().stream()
                .filter(p -> !p.getUUID().equals(player.getUUID()))
                .filter(p -> session.getBoard().getInitialRole(p.getUUID()) instanceof LoupRole)
                .map(ServerPlayer::getPlainTextName)
                .toList();

        player.sendSystemMessage(Component.literal("§c[Nuit] Tu te réveilles et cherches tes maîtres..."));

        if (wolfNames.isEmpty()) {
            player.sendSystemMessage(Component.literal("§dIl n'y a aucun Loup-Garou dans le village !"));
            session.addHistory("Le Sbire (" + player.getPlainTextName() + ") n'a vu aucun Loup.");
        } else {
            player.sendSystemMessage(Component.literal("§dLes Loups-Garous sont : §l" + String.join(", ", wolfNames)));
            session.addHistory("Le Sbire (" + player.getPlainTextName() + ") a vu les Loups : " + String.join(", ", wolfNames) + ".");
        }
    }

    @Override
    public boolean didWin(GameSession session, UUID myId, List<UUID> eliminated) {
        boolean wolfDied = false;
        boolean wolfInPlay = false;

        for (net.minecraft.server.level.ServerPlayer p : session.getServerPlayers()) {
            if (session.getBoard().getCurrentRole(p.getUUID()) instanceof LoupRole) {
                wolfInPlay = true;
                if (eliminated.contains(p.getUUID())) {
                    wolfDied = true;
                }
            }
        }

        if (wolfInPlay) {
            return !wolfDied;
        } else {
            return !eliminated.contains(myId);
        }
    }

}