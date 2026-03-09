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

public class LeComteRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§5Le Comte");
    }

    @Override
    public int getNightOrder() {
        return 4;
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
            player.sendSystemMessage(Component.literal("§cLes autres Vampires sont : " + String.join(", ", vampireNames)));
        } else {
            player.sendSystemMessage(Component.literal("§cTu es le seul Vampire ce soir."));
        }

        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            Role targetRole = session.getBoard().getInitialRole(target.getUUID());
            boolean isTargetVampire = targetRole instanceof VampireRole || targetRole instanceof LeMaitreRole || targetRole instanceof LeComteRole;

            if (!target.getUUID().equals(player.getUUID()) && !isTargetVampire && !session.getBoard().isShielded(target.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§4"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un joueur non-vampire pour lui donner la Marque de la Peur."));

        MenuHelper.openChoiceMenu(player, "§8Placer la Marque de Peur", options, index -> {
            ServerPlayer target = validTargets.get(index);

            session.getBoard().setPlayerMarque(target.getUUID(), Marque.PEUR);

            player.sendSystemMessage(Component.literal("§cTu as placé la Marque de la Peur sur " + target.getPlainTextName() + "."));
            session.addHistory("Le Comte (" + player.getPlainTextName() + ") a placé la Marque de la Peur sur " + target.getPlainTextName() + ".");

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }

    @Override
    public boolean didWin(natjom.nocturne.game.GameSession session, java.util.UUID myId, java.util.List<java.util.UUID> eliminated) {
        if (eliminated.contains(myId)) {
            return false;
        }

        boolean vampireDied = false;
        for (java.util.UUID deadId : eliminated) {
            natjom.nocturne.game.role.Role deadRole = session.getBoard().getCurrentRole(deadId);
            natjom.nocturne.game.role.vampire.Marque deadMarque = session.getBoard().getPlayerMarque(deadId);

            if (deadRole instanceof natjom.nocturne.game.role.vampire.VampireRole || deadRole instanceof natjom.nocturne.game.role.vampire.LeMaitreRole || deadRole instanceof natjom.nocturne.game.role.vampire.LeComteRole || deadMarque == natjom.nocturne.game.role.vampire.Marque.VAMPIRE) {
                vampireDied = true;
            }
        }

        return !vampireDied;
    }
}