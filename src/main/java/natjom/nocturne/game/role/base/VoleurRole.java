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

public class VoleurRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Voleur");
    }

    @Override
    public int getNightOrder() {
        return 26;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVkZGI4ZTE2Nzc5MmY3ODc1NDk2MjU0NThlMmVhNTVjOTJlNzBiYmRlNTVlYjUyZDk1NTQ5N2IyMTQ4NTZjOCJ9fX0=";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {

        if (session.getBoard().isShielded(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§c[Nuit] Tu essaies de te lever... mais un bouclier protège ta carte ! Tu ne peux rien faire."));
            session.addHistory("Le voleur (" + player.getPlainTextName() + ") n'a pas pu échanger sa carte car elle était protégée.");
            return;
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Tu te réveilles. Choisis un joueur à qui voler la carte :"));

        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(player.getUUID()) && !session.getBoard().isShielded(target.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§e"));
                validTargets.add(target);
            }
        }

        MenuHelper.openChoiceMenu(player, "§8Voler qui ?", options, index -> {
            ServerPlayer target = validTargets.get(index);

            Role stolenRole = session.getBoard().getCurrentRole(target.getUUID());

            session.getBoard().swapPlayerRoles(player.getUUID(), target.getUUID());

            player.sendSystemMessage(Component.literal("§dTu as volé la carte de " + target.getPlainTextName() + "."));
            player.sendSystemMessage(Component.literal("§dTu es maintenant : §l" + stolenRole.getDisplayName().getString()));

            session.addHistory("Le Voleur (" + player.getPlainTextName() + ") a volé la carte de " + target.getPlainTextName() + " (" + stolenRole.getDisplayName().getString() + ").");
        });
    }
}