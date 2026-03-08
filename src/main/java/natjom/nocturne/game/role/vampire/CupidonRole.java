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

public class CupidonRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§dCupidon");
    }

    @Override
    public int getNightOrder() {
        return 7;
    }

    @Override
    public String getSkinTexture() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkyMTdiNDgyNGQ0M2M5NTNhYjMxMGFjMWQyYTNiYTMzMmU3NWFkMWE1Zjc2YzdjOTlmZGE5MjhjOTc5N2E2MTcifX19";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        openFirstPick(player, session);
    }

    private void openFirstPick(ServerPlayer player, GameSession session) {
        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!session.getBoard().isShielded(target.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§d"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis le premier joueur à marquer d'Amour."));

        MenuHelper.openChoiceMenu(player, "§8Cupidon : 1er Amoureux", options, index -> {
            ServerPlayer target = validTargets.get(index);
            session.getBoard().setPlayerMarque(target.getUUID(), Marque.AMOUR);
            player.sendSystemMessage(Component.literal("§dTu as touché " + target.getPlainTextName() + " avec ta première flèche."));

            openSecondPick(player, session, target);
        });
    }

    private void openSecondPick(ServerPlayer player, GameSession session, ServerPlayer firstTarget) {
        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(firstTarget.getUUID()) && !session.getBoard().isShielded(target.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§d"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis le second joueur à marquer d'Amour."));

        MenuHelper.openChoiceMenu(player, "§8Cupidon : 2ème Amoureux", options, index -> {
            ServerPlayer target = validTargets.get(index);
            session.getBoard().setPlayerMarque(target.getUUID(), Marque.AMOUR);

            player.sendSystemMessage(Component.literal("§dTu as touché " + target.getPlainTextName() + " avec ta seconde flèche. Les deux sont liés !"));
            session.addHistory("Cupidon (" + player.getPlainTextName() + ") a placé la Marque de l'Amour sur " + firstTarget.getPlainTextName() + " et " + target.getPlainTextName() + ".");

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}