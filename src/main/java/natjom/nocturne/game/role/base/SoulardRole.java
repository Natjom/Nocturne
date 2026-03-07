package natjom.nocturne.game.role.base;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.gui.MenuHelper;
import natjom.nocturne.util.MenuIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SoulardRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aSoûlard");
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

        if (session.getBoard().isShielded(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§c[Nuit] Tu essaies de te lever... mais un bouclier protège ta carte ! Tu ne peux rien faire."));
            session.addHistory("Le Soûlard (" + player.getPlainTextName() + ") n'a pas pu échanger sa carte car elle était protégée.");
            return;
        }

        List<ItemStack> options = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            options.add(MenuIcons.makeChoiceCard("Carte " + (i + 1)));
        }

        MenuHelper.openChoiceMenu(player, "§8Soûlard : Quel centre ?", options, index -> {
            session.getBoard().swapPlayerWithCenter(player.getUUID(), index);

            player.sendSystemMessage(Component.literal("§c[Nuit] Tu te réveilles en titubant..."));
            player.sendSystemMessage(Component.literal("§dTu as échangé ta carte avec la carte du centre n°" + (index + 1) + ". Tu ne sais pas ce que tu es devenu !"));

            session.addHistory("Le Soûlard (" + player.getPlainTextName() + ") a échangé sa carte avec le centre " + (index + 1) + ".");
        });
    }
}