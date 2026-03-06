package natjom.nocturne.game.role.base;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.gui.MenuHelper;
import natjom.nocturne.util.MenuIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoupRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Loup");
    }

    @Override
    public int getNightOrder() {
        return 13;
    }

    @Override
    public String getSkinTexture() { return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY1NTVhOTAwOWUyZmZhNjY5Nzg2YWI4YWViZGZlZTM3ZmE1MWY3OWRiODM0MDgwNWZlNjk3MDk0Zjk2YjMwMSJ9fX0="; }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<ServerPlayer> wolfPack = session.getServerPlayers().stream()
                .filter(p -> session.getBoard().getInitialRole(p.getUUID()) instanceof LoupRole)
                .toList();

        if (wolfPack.size() == 1) {
            player.sendSystemMessage(Component.literal("§c[Nuit] Tu te réveilles seul. Choisis une carte du centre à regarder :"));

            List<ItemStack> options = List.of(
                    MenuIcons.makeChoiceCard("Carte 1"),
                    MenuIcons.makeChoiceCard("Carte 2"),
                    MenuIcons.makeChoiceCard("Carte 3")
            );

            MenuHelper.openChoiceMenu(player, "§8Regarder le centre", options, index -> {
                Role seenRole = session.getBoard().getCenterCard(index);
                if (seenRole != null) {
                    player.sendSystemMessage(Component.literal("§dTu as regardé la carte du centre n°" + (index + 1) + "."));
                    player.sendSystemMessage(Component.literal("§dC'est un(e) : §l" + seenRole.getDisplayName().getString()));

                    session.addHistory("Le Loup (" + player.getPlainTextName() + ") a regardé le Centre " + (index + 1) + " (" + seenRole.getDisplayName().getString() + ").");
                }
            });

        } else {
            List<String> wolfNames = wolfPack.stream()
                    .filter(p -> !p.getUUID().equals(player.getUUID()))
                    .map(net.minecraft.world.entity.player.Player::getPlainTextName)
                    .toList();

            player.sendSystemMessage(Component.literal("§cLes autres Loups sont : " + String.join(", ", wolfNames)));
            session.addHistory("Le Loup (" + player.getPlainTextName() + ") a vu sa meute.");
        }
    }

    @Override
    public boolean didWin(GameSession session, UUID myId, List<UUID> eliminated) {
        if (eliminated.contains(myId)) {
            return false;
        }

        boolean wolfDied = false;
        for (UUID deadId : eliminated) {
            if (session.getBoard().getCurrentRole(deadId) instanceof LoupRole) {
                wolfDied = true;
            }
        }

        return !wolfDied;
    }
}