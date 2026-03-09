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

public class ImitateurRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§7Imitateur");
    }

    @Override
    public int getNightOrder() {
        return 1;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2FmODhlODhiZWU4NWQ2NDg2MDc0NThkMjlmYjlhMzVkMzYyYTE1ZmUwZTBhODUzN2UyMTA4NjU0YjU4ZWE4NCJ9fX0=";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<ItemStack> options = new ArrayList<>();
        options.add(MenuIcons.makeChoiceCard("§eCarte du Centre 1"));
        options.add(MenuIcons.makeChoiceCard("§eCarte du Centre 2"));
        options.add(MenuIcons.makeChoiceCard("§eCarte du Centre 3"));

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis une carte du centre à regarder. Tu deviendras ce rôle."));

        MenuHelper.openChoiceMenu(player, "§8Copier un rôle", options, index -> {
            Role seenRole = session.getBoard().getCenterCard(index);

            session.getBoard().setCurrentRole(player.getUUID(), seenRole);

            player.sendSystemMessage(Component.literal("§dLa carte est un(e) : §l" + seenRole.getDisplayName().getString() + "§r§d. Tu rejoins ce camp !"));
            session.addHistory("L'Imitateur (" + player.getPlainTextName() + ") a regardé la carte du centre " + (index + 1) + " et est devenu : " + seenRole.getDisplayName().getString() + ".");

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}