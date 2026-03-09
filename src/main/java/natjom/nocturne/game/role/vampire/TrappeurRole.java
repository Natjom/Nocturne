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

public class TrappeurRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aTrappeur");
    }

    @Override
    public int getNightOrder() {
        return 24;
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
        openCardPick(player, session);
    }

    private void openCardPick(ServerPlayer player, GameSession session) {
        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(player.getUUID()) && !session.getBoard().isShielded(target.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§2"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un joueur pour regarder sa CARTE."));

        MenuHelper.openChoiceMenu(player, "§8Trappeur : Regarder Carte", options, index -> {
            ServerPlayer target = validTargets.get(index);
            Role seenRole = session.getBoard().getCurrentRole(target.getUUID());

            player.sendSystemMessage(Component.literal("§2La carte de " + target.getPlainTextName() + " est : §l" + seenRole.getDisplayName().getString()));
            session.addHistory("Le Trappeur (" + player.getPlainTextName() + ") a regardé la carte de " + target.getPlainTextName() + ".");

            openMarkPick(player, session, target);
        });
    }

    private void openMarkPick(ServerPlayer player, GameSession session, ServerPlayer firstTarget) {
        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(player.getUUID()) && !target.getUUID().equals(firstTarget.getUUID()) && !session.getBoard().isShielded(target.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§2"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un AUTRE joueur pour regarder sa MARQUE."));

        MenuHelper.openChoiceMenu(player, "§8Trappeur : Regarder Marque", options, index -> {
            ServerPlayer target = validTargets.get(index);
            Marque seenMarque = session.getBoard().getPlayerMarque(target.getUUID());

            player.sendSystemMessage(Component.literal("§2La marque de " + target.getPlainTextName() + " est : §l" + seenMarque.getDisplayName().getString()));
            session.addHistory("Le Trappeur (" + player.getPlainTextName() + ") a regardé la marque de " + target.getPlainTextName() + ".");

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}