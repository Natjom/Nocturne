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

public class MoineRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aMoine");
    }

    @Override
    public int getNightOrder() {
        return 9;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y2NmQxMTVlNzE5MWY4MzU5NThmYzUyNWQ0MWUyNzUzMzEyMTAxNGVjNzIxZDZmMzUzMjU0NDA3N2YyMmQ2ZSJ9fX0=";
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
                options.add(MenuIcons.makePlayerHead(target, "§e"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un joueur. Vos deux marques seront purifiées (Clarté)."));

        MenuHelper.openChoiceMenu(player, "§8Purifier une Marque", options, index -> {
            ServerPlayer target = validTargets.get(index);

            session.getBoard().setPlayerMarque(player.getUUID(), Marque.CLARTE);
            session.getBoard().setPlayerMarque(target.getUUID(), Marque.CLARTE);

            player.sendSystemMessage(Component.literal("§eTu as purifié ta marque et celle de " + target.getPlainTextName() + "."));
            session.addHistory("Le Moine (" + player.getPlainTextName() + ") a purifié sa marque et celle de " + target.getPlainTextName() + ".");

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}