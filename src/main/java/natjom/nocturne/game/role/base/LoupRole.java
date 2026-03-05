package natjom.nocturne.game.role.base;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.gui.MenuHelper;
import natjom.nocturne.util.MenuIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

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
        long wolfCount = session.getServerPlayers().stream()
                .filter(p -> session.getBoard().getInitialRole(p.getUUID()) instanceof LoupRole)
                .count();

        if (wolfCount == 1) {
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
            player.sendSystemMessage(Component.literal("§c[Nuit] Tu te réveilles avec tes alliés Loups. Vous vous regardez silencieusement."));
        }
    }
}