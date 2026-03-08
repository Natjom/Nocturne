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

public class RenfieldRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aRenfield");
    }

    @Override
    public int getNightOrder() {
        return 5;
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
        List<String> vampireNames = session.getServerPlayers().stream()
                .filter(p -> !p.getUUID().equals(player.getUUID()))
                .filter(p -> {
                    Role r = session.getBoard().getInitialRole(p.getUUID());
                    return r instanceof VampireRole || r instanceof LeMaitreRole || r instanceof LeComteRole;
                })
                .map(net.minecraft.world.entity.player.Player::getPlainTextName)
                .toList();

        if (!vampireNames.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cTu aperçois les Vampires : " + String.join(", ", vampireNames)));
        } else {
            player.sendSystemMessage(Component.literal("§cIl n'y a aucun Vampire en vue ce soir..."));
        }

        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!session.getBoard().isShielded(target.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§a"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un joueur à qui donner la Marque de la Chauve-souris."));

        MenuHelper.openChoiceMenu(player, "§8Donner Chauve-souris", options, index -> {
            ServerPlayer target = validTargets.get(index);

            session.getBoard().setPlayerMarque(target.getUUID(), Marque.CHAUVE_SOURIS);

            player.sendSystemMessage(Component.literal("§aTu as placé la Marque de la Chauve-souris sur " + target.getPlainTextName() + "."));
            session.addHistory("Renfield (" + player.getPlainTextName() + ") a placé la Marque de la Chauve-souris sur " + target.getPlainTextName() + ".");

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}