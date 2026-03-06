package natjom.nocturne.game.role.base;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.gui.MenuHelper;
import natjom.nocturne.util.MenuIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NoiseuseRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Noiseuse");
    }

    @Override
    public int getNightOrder() {
        return 29;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTg5MmQ2N2VhODRkNTJhNTQxMjU1OGM3YzMxZmQzNGYzNmM3M2Y2OThjNWY0MjRhZGE1ZDg3MzJhNjMyNWI1MyJ9fX0=";
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
            if (!target.getUUID().equals(player.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§e"));
                validTargets.add(target);
            }
        }

        MenuHelper.openChoiceMenu(player, "§8Noiseuse : 1er joueur", options, index -> {
            ServerPlayer firstTarget = validTargets.get(index);
            openSecondPick(player, session, firstTarget);
        });
    }

    private void openSecondPick(ServerPlayer player, GameSession session, ServerPlayer firstTarget) {
        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(player.getUUID()) && !target.getUUID().equals(firstTarget.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§e"));
                validTargets.add(target);
            }
        }

        MenuHelper.openChoiceMenu(player, "§8Noiseuse : 2ème joueur", options, index -> {
            ServerPlayer secondTarget = validTargets.get(index);

            session.getBoard().swapPlayerRoles(firstTarget.getUUID(), secondTarget.getUUID());

            player.sendSystemMessage(Component.literal("§dTu as échangé les cartes de " + firstTarget.getPlainTextName() + " et " + secondTarget.getPlainTextName() + "."));
            session.addHistory("La Noiseuse (" + player.getPlainTextName() + ") a échangé " + firstTarget.getPlainTextName() + " et " + secondTarget.getPlainTextName() + ".");
        });
    }
}
