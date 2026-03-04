package natjom.nocturne.game.role;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.gui.MenuHelper;
import natjom.nocturne.util.MenuIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WolfRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Loup-Garou");
    }

    @Override
    public int getNightOrder() {
        return 10;
    }

    @Override
    public String getSkinTexture() { return ""; }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        long wolfCount = session.getServerPlayers().stream()
                .filter(p -> session.getBoard().getInitialRole(p.getUUID()) instanceof WolfRole)
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
                    player.sendSystemMessage(Component.literal("§dC'est un.e : §l" + seenRole.getDisplayName().getString()));
                }
            });

        } else {
            player.sendSystemMessage(Component.literal("§c[Nuit] Tu te réveilles avec tes alliés Loups. Vous vous regardez silencieusement."));
        }
    }
}