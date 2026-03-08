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

public class PickpocketRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§7Pickpocket");
    }

    @Override
    public int getNightOrder() {
        return 28;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk5YWFkNDY5NDIyMDE4OTlhOTU1ZDlmYzIyNmViYWNjZDhiNWQ3NGQxYTBiODk5YjZkOGI4MmVlZTRiZGYwYiJ9fX0=";
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
                options.add(MenuIcons.makePlayerHead(target, "§7"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un joueur avec qui échanger ta marque. Tu regarderas ensuite ta nouvelle marque."));

        MenuHelper.openChoiceMenu(player, "§8Voler une Marque", options, index -> {
            ServerPlayer target = validTargets.get(index);

            session.getBoard().swapPlayerMarques(player.getUUID(), target.getUUID());
            Marque newMarque = session.getBoard().getPlayerMarque(player.getUUID());

            player.sendSystemMessage(Component.literal("§7Tu as échangé ta marque avec " + target.getPlainTextName() + "."));
            player.sendSystemMessage(Component.literal("§7Ta nouvelle marque est : §l" + newMarque.getDisplayName().getString()));
            session.addHistory("Le Pickpocket (" + player.getPlainTextName() + ") a échangé sa marque avec " + target.getPlainTextName() + ".");

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}