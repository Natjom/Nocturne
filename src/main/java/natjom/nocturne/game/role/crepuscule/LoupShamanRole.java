package natjom.nocturne.game.role.crepuscule;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.game.role.base.LoupRole;
import natjom.nocturne.gui.MenuHelper;
import natjom.nocturne.util.MenuIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LoupShamanRole extends LoupRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§cLoup Shaman");
    }

    @Override
    public int getNightOrder() {
        return 16;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjViMDM1MTM2ZTMxY2MxN2YzNTgxMTQ2NDYyYTVkOGU3MjhmOTM0ZWYwYmJhMDY3MzRmNThhOTZhZDcxMGQ0ZiJ9fX0=";
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
                options.add(MenuIcons.makePlayerHead(target, "§c"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un joueur pour voir sa carte."));

        MenuHelper.openChoiceMenu(player, "§8Regarder une carte", options, index -> {
            ServerPlayer target = validTargets.get(index);
            Role seenRole = session.getBoard().getCurrentRole(target.getUUID());

            player.sendSystemMessage(Component.literal("§dLa carte de " + target.getPlainTextName() + " est : §l" + seenRole.getDisplayName().getString()));
            session.addHistory("Le Loup Shaman (" + player.getPlainTextName() + ") a regardé la carte de " + target.getPlainTextName() + ".");
        });
    }
}