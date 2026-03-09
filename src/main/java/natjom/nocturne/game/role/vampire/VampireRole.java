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
import java.util.UUID;

public class VampireRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§5Vampire");
    }

    @Override
    public int getNightOrder() {
        return 2;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWNiYzU2OTk2ZTM3OWE1MmJlZjAzYTY5MjI3Y2I4Y2U0ZmRkY2IwNDMxYTJhNTZjZTcxZjk0OTEwNGZmYmUyOSJ9fX0=";
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
                options.add(MenuIcons.makePlayerHead(target, "§c"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un joueur à mordre pour lui donner la Marque du Vampire."));

        MenuHelper.openChoiceMenu(player, "§8Mordre un joueur", options, index -> {
            ServerPlayer target = validTargets.get(index);

            session.getBoard().setPlayerMarque(target.getUUID(), Marque.VAMPIRE);

            player.sendSystemMessage(Component.literal("§cTu as placé la Marque du Vampire sur " + target.getPlainTextName() + "."));
            session.addHistory("Le Vampire (" + player.getPlainTextName() + ") a placé la Marque du Vampire sur " + target.getPlainTextName() + ".");

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }

    @Override
    public boolean didWin(GameSession session, UUID myId, List<UUID> eliminated) {
        if (eliminated.contains(myId)) {
            return false;
        }

        boolean vampireDied = false;
        for (UUID deadId : eliminated) {
            Role deadRole = session.getBoard().getCurrentRole(deadId);
            Marque deadMarque = session.getBoard().getPlayerMarque(deadId);

            if (deadRole instanceof VampireRole || deadRole instanceof LeMaitreRole || deadRole instanceof LeComteRole || deadMarque == Marque.VAMPIRE) {
                vampireDied = true;
            }
        }

        return !vampireDied;
    }
}