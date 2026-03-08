package natjom.nocturne.game.role.crepuscule;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.game.role.base.LoupRole;
import natjom.nocturne.game.role.base.TanneurRole;
import natjom.nocturne.game.role.base.SosieRole;
import natjom.nocturne.gui.MenuHelper;
import natjom.nocturne.util.MenuIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ExorcisteRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aExorciste");
    }

    @Override
    public int getNightOrder() {
        return 23;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmE3YmY3NzNjZDYxOTJhNTYzNDZiNmYxMWFjMjRjZWQyNmY2ZWRhOGJkY2ZjYTI4MjU5M2ZhMmQxMWY3NjcxYiJ9fX0=";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        openFirstPick(player, session);
    }

    private boolean isHostile(Role role) {
        boolean isWolf = role instanceof LoupRole || (role instanceof SosieRole && ((SosieRole) role).getCopiedRole() instanceof LoupRole);
        boolean isTanner = role instanceof TanneurRole || (role instanceof SosieRole && ((SosieRole) role).getCopiedRole() instanceof TanneurRole);
        return isWolf || isTanner;
    }

    private void openFirstPick(ServerPlayer player, GameSession session) {
        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(player.getUUID()) && !session.getBoard().isShielded(target.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§3"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un premier joueur à examiner."));

        MenuHelper.openChoiceMenu(player, "§8Exorciste : 1er joueur", options, index -> {
            ServerPlayer target = validTargets.get(index);
            Role seenRole = session.getBoard().getCurrentRole(target.getUUID());

            player.sendSystemMessage(Component.literal("§dLa carte de " + target.getPlainTextName() + " est : §l" + seenRole.getDisplayName().getString()));

            if (isHostile(seenRole)) {
                session.getBoard().setCurrentRole(player.getUUID(), seenRole);
                player.sendSystemMessage(Component.literal("§cOh non ! Tu as vu un rôle hostile. Tu deviens un " + seenRole.getDisplayName().getString() + " et tu t'arrêtes là."));
                session.addHistory("L'Exorciste (" + player.getPlainTextName() + ") a regardé " + target.getPlainTextName() + ", a vu un " + seenRole.getDisplayName().getString() + " et a rejoint son camp.");

                session.getBoard().addPlayerAction(player.getUUID());
            } else {
                player.sendSystemMessage(Component.literal("§aC'est un rôle du village. Tu peux examiner une deuxième carte."));
                session.addHistory("L'Exorciste (" + player.getPlainTextName() + ") a regardé " + target.getPlainTextName() + " (" + seenRole.getDisplayName().getString() + ").");
                openSecondPick(player, session, target);
            }
        });
    }

    private void openSecondPick(ServerPlayer player, GameSession session, ServerPlayer firstTarget) {
        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(player.getUUID()) && !target.getUUID().equals(firstTarget.getUUID()) && !session.getBoard().isShielded(target.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§3"));
                validTargets.add(target);
            }
        }

        MenuHelper.openChoiceMenu(player, "§8Exorciste : 2ème joueur", options, index -> {
            ServerPlayer target = validTargets.get(index);
            Role seenRole = session.getBoard().getCurrentRole(target.getUUID());

            player.sendSystemMessage(Component.literal("§dLa carte de " + target.getPlainTextName() + " est : §l" + seenRole.getDisplayName().getString()));

            if (isHostile(seenRole)) {
                session.getBoard().setCurrentRole(player.getUUID(), seenRole);
                player.sendSystemMessage(Component.literal("§cTu as vu un rôle hostile. Tu deviens un " + seenRole.getDisplayName().getString() + "."));
                session.addHistory("L'Exorciste (" + player.getPlainTextName() + ") a regardé " + target.getPlainTextName() + " (2ème), a vu un " + seenRole.getDisplayName().getString() + " et a rejoint son camp.");
            } else {
                player.sendSystemMessage(Component.literal("§aC'est encore un rôle du village. Tu restes Exorciste pour cette partie !"));
                session.addHistory("L'Exorciste (" + player.getPlainTextName() + ") a regardé " + target.getPlainTextName() + " (2ème) (" + seenRole.getDisplayName().getString() + ") et reste dans le camp du Village.");
            }

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}