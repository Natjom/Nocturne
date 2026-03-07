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

public class DivinateurRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("§bDivinateur");
    }

    @Override
    public int getNightOrder() {
        return 37;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjUwNDJlYzZlYmE1YWVlMTM1MmUwNzFkMGEyOTcwZWVlMmRjNjk4NmQwNThmZDViZjI5MGE2NTkzZjRlYzk2NiJ9fX0=";
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
                options.add(MenuIcons.makePlayerHead(target, "§b"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un joueur à examiner."));

        MenuHelper.openChoiceMenu(player, "§8Examiner une carte", options, index -> {
            ServerPlayer target = validTargets.get(index);
            Role seenRole = session.getBoard().getCurrentRole(target.getUUID());

            player.sendSystemMessage(Component.literal("§dLa carte de " + target.getPlainTextName() + " est : §l" + seenRole.getDisplayName().getString()));

            boolean isWolf = seenRole instanceof LoupRole || (seenRole instanceof SosieRole && ((SosieRole) seenRole).getCopiedRole() instanceof LoupRole);
            boolean isTanner = seenRole instanceof TanneurRole || (seenRole instanceof SosieRole && ((SosieRole) seenRole).getCopiedRole() instanceof TanneurRole);

            if (!isWolf && !isTanner) {
                session.getBoard().addRevealedCard(target.getUUID());
                player.sendSystemMessage(Component.literal("§eC'est un rôle du Village ! La carte restera visible ce matin."));
                session.addHistory("Le Divinateur (" + player.getPlainTextName() + ") a regardé et révélé publiquement la carte de " + target.getPlainTextName() + " (" + seenRole.getDisplayName().getString() + ").");
            } else {
                player.sendSystemMessage(Component.literal("§8C'est un rôle hostile (Loup ou Tanneur). La carte restera cachée."));
                session.addHistory("Le Divinateur (" + player.getPlainTextName() + ") a regardé la carte de " + target.getPlainTextName() + " (" + seenRole.getDisplayName().getString() + ") mais elle reste secrète.");
            }
        });
    }
}