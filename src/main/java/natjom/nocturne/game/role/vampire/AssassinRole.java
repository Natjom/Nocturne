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

public class AssassinRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§4Assassin");
    }

    @Override
    public int getNightOrder() {
        return 10;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBmMTkxYjNkZDlhZWM0ZmU4NmM2NDI4YzNmZWYyM2UzYmE1MTQ4OTA5YWZhMGMyOWQ5OTkwMzZmZjExOWMyMyJ9fX0=";
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
                options.add(MenuIcons.makePlayerHead(target, "§4"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis ta cible pour lui donner la Marque de l'Assassin."));

        MenuHelper.openChoiceMenu(player, "§8Placer Marque Assassin", options, index -> {
            ServerPlayer target = validTargets.get(index);

            session.getBoard().setPlayerMarque(target.getUUID(), Marque.ASSASSIN);

            player.sendSystemMessage(Component.literal("§4Ta cible est désormais " + target.getPlainTextName() + "."));
            session.addHistory("L'Assassin (" + player.getPlainTextName() + ") a placé la Marque de l'Assassin sur " + target.getPlainTextName() + ".");

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}