package natjom.nocturne.game.role.crepuscule;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.gui.MenuHelper;
import natjom.nocturne.util.MenuIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ApprentieVoyanteRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aApprentie Voyante");
    }

    @Override
    public int getNightOrder() {
        return 22;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVhZDAzYWI0ZjJmNWQ5M2NhYzk5ZjkzZTExZGJjZDlmNTA0ZjU4NzU2NjZmYzVmMzhlOTI4ZjUyZWMzNjUwMSJ9fX0=";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<ItemStack> options = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            options.add(MenuIcons.makeChoiceCard("Centre " + (i + 1)));
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis une carte du centre à regarder."));

        MenuHelper.openChoiceMenu(player, "§8Regarder le centre", options, index -> {
            Role seenRole = session.getBoard().getCenterCard(index);
            player.sendSystemMessage(Component.literal("§dLa carte du centre n°" + (index + 1) + " est : §l" + seenRole.getDisplayName().getString()));
            session.addHistory("L'Apprentie Voyante (" + player.getPlainTextName() + ") a regardé le centre " + (index + 1) + " (" + seenRole.getDisplayName().getString() + ").");
            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}