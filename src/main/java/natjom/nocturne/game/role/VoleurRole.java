package natjom.nocturne.game.role;

import natjom.nocturne.game.GameSession;
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
        return 30;
    }

    @Override
    public String getSkinTexture() {
        return "";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        player.sendSystemMessage(Component.literal("§c[Nuit] Tu te réveilles. Choisis un joueur à qui voler la carte :"));

        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(player.getUUID())) {
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