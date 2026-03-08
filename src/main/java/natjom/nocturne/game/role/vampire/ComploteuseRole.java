package natjom.nocturne.game.role.vampire;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.gui.MenuHelper;
import natjom.nocturne.util.MenuIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ComploteuseRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§5Comploteuse");
    }

    @Override
    public int getNightOrder() {
        return 8;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODAzYzJmZWNkZjcwNjZmYjE2N2QzN2ViMzNmMDhlOWQwMzNmY2E1MGMyYWFkYTg0MmY5NzU4ZDFkNzQ3YTg3NCJ9fX0=";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(player.getUUID()) && !session.getBoard().isShielded(target.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§5"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un joueur pour lui donner la Marque du Traître."));

        MenuHelper.openChoiceMenu(player, "§8Placer Marque Traître", options, index -> {
            ServerPlayer target = validTargets.get(index);

            session.getBoard().setPlayerMarque(target.getUUID(), Marque.TRAITRE);

            player.sendSystemMessage(Component.literal("§5Tu as placé la Marque du Traître sur " + target.getPlainTextName() + "."));
            session.addHistory("La Comploteuse (" + player.getPlainTextName() + ") a placé la Marque du Traître sur " + target.getPlainTextName() + ".");

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}