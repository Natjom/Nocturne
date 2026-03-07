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

public class LoupAlphaRole extends LoupRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§cLoup Alpha");
    }

    @Override
    public int getNightOrder() {
        return 15;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTMxYTg0MGM4ZDkzMTUzNWRhZjkzMGFlMzMyMDVkZjQwZmRlZjM3MTUwNTcyZTI4OTVmZTQ5YTBjODViODNjMiJ9fX0=";
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

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un joueur à infecter."));

        MenuHelper.openChoiceMenu(player, "§8Infecter un joueur", options, index -> {
            ServerPlayer target = validTargets.get(index);

            session.getBoard().setCurrentRole(target.getUUID(), new LoupRole());

            player.sendSystemMessage(Component.literal("§cTu as transformé " + target.getPlainTextName() + " en Loup."));
            session.addHistory("Le Loup Alpha (" + player.getPlainTextName() + ") a infecté " + target.getPlainTextName() + ".");
        });
    }
}