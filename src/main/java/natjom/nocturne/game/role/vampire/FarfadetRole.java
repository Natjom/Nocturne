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

public class FarfadetRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aFarfadet");
    }

    @Override
    public int getNightOrder() {
        return 32;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWM5YmFlMGQzMWM4OWM2ODE5ODU3NjhiZGQxNTBjZTc0YmQxZWI2ZjdiMWY1ZDljNWEyOGMyZmE3MTU1MDY5OCJ9fX0=";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<ItemStack> options = new ArrayList<>();
        options.add(MenuIcons.makeChoiceCard("§aÉchanger des Cartes"));
        options.add(MenuIcons.makeChoiceCard("§aÉchanger des Marques"));

        player.sendSystemMessage(Component.literal("§c[Nuit] Que veux-tu échanger entre deux joueurs ?"));

        MenuHelper.openChoiceMenu(player, "§8Farfadet : Action", options, index -> {
            boolean swapCards = (index == 0);
            openFirstTarget(player, session, swapCards);
        });
    }

    private void openFirstTarget(ServerPlayer player, GameSession session, boolean swapCards) {
        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(player.getUUID()) && !session.getBoard().isShielded(target.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§a"));
                validTargets.add(target);
            }
        }

        String typeStr = swapCards ? "cartes" : "marques";
        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis le premier joueur pour l'échange de " + typeStr + "."));

        MenuHelper.openChoiceMenu(player, "§8Farfadet : 1er Joueur", options, index -> {
            ServerPlayer firstTarget = validTargets.get(index);
            openSecondTarget(player, session, swapCards, firstTarget);
        });
    }

    private void openSecondTarget(ServerPlayer player, GameSession session, boolean swapCards, ServerPlayer firstTarget) {
        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(player.getUUID()) && !target.getUUID().equals(firstTarget.getUUID()) && !session.getBoard().isShielded(target.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§a"));
                validTargets.add(target);
            }
        }

        String typeStr = swapCards ? "cartes" : "marques";
        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis le second joueur pour l'échange de " + typeStr + "."));

        MenuHelper.openChoiceMenu(player, "§8Farfadet : 2ème Joueur", options, index -> {
            ServerPlayer secondTarget = validTargets.get(index);

            if (swapCards) {
                session.getBoard().swapPlayerRoles(firstTarget.getUUID(), secondTarget.getUUID());
                player.sendSystemMessage(Component.literal("§aTu as échangé les cartes de " + firstTarget.getPlainTextName() + " et " + secondTarget.getPlainTextName() + "."));
                session.addHistory("Le Farfadet (" + player.getPlainTextName() + ") a échangé les cartes de " + firstTarget.getPlainTextName() + " et " + secondTarget.getPlainTextName() + ".");
            } else {
                session.getBoard().swapPlayerMarques(firstTarget.getUUID(), secondTarget.getUUID());
                player.sendSystemMessage(Component.literal("§aTu as échangé les marques de " + firstTarget.getPlainTextName() + " et " + secondTarget.getPlainTextName() + "."));
                session.addHistory("Le Farfadet (" + player.getPlainTextName() + ") a échangé les marques de " + firstTarget.getPlainTextName() + " et " + secondTarget.getPlainTextName() + ".");
            }

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}