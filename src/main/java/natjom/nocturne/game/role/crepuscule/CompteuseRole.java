package natjom.nocturne.game.role.crepuscule;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompteuseRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aCompteuse");
    }

    @Override
    public int getNightOrder() {
        return 31;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzU1MWY4NjM2Y2Y1MzVjOTM2NjY0YWRiNDZkNjc1NWMzYjJkNmE3YWVkYTg5NTk4YTE2MTM3ZjMxZjhhNjJhNiJ9fX0=";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<String> actingPlayers = new ArrayList<>();

        for (UUID actorId : session.getBoard().getPlayersWhoActed()) {
            if (!actorId.equals(player.getUUID())) {
                ServerPlayer actor = session.getServerPlayers().stream()
                        .filter(p -> p.getUUID().equals(actorId))
                        .findFirst()
                        .orElse(null);

                if (actor != null) {
                    actingPlayers.add(actor.getPlainTextName());
                }
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Tu observes les joueurs qui agissent..."));

        if (actingPlayers.isEmpty()) {
            player.sendSystemMessage(Component.literal("§dPersonne d'autre n'a fait d'action cette nuit."));
        } else {
            player.sendSystemMessage(Component.literal("§dJoueurs ayant agi (pouce levé) : §l" + String.join(", ", actingPlayers)));
        }

        session.addHistory("La Compteuse (" + player.getPlainTextName() + ") a vu qui s'est réveillé.");
        session.getBoard().addPlayerAction(player.getUUID());
    }
}