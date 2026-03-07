package natjom.nocturne.game.role.base;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.gui.MenuHelper;
import natjom.nocturne.util.MenuIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;

import java.util.ArrayList;
import java.util.List;

public class VoyanteRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Voyante");
    }

    @Override
    public int getNightOrder() {
        return 21;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIzNDI2MmQ2OWU3NmQ5ODAyYTM1ZGE2Y2NkYjgzODM2YzliNGQ5MjFhNjhmMTA4ZWY0NDUwN2E4YjRjMjVlIn19fQ==";
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

        ItemStack centerOption = new ItemStack(Items.ENDER_EYE);
        centerOption.set(DataComponents.CUSTOM_NAME, Component.literal("§dVoir 2 cartes du centre"));
        options.add(centerOption);

        MenuHelper.openChoiceMenu(player, "§8Voyante : Qui regarder ?", options, index -> {
            if (index < validTargets.size()) {
                ServerPlayer target = validTargets.get(index);
                Role seenRole = session.getBoard().getCurrentRole(target.getUUID());
                player.sendSystemMessage(Component.literal("§dLe rôle de " + target.getPlainTextName() + " est : §l" + seenRole.getDisplayName().getString()));
                session.addHistory("La Voyante (" + player.getPlainTextName() + ") a regardé la carte de " + target.getPlainTextName() + " (" + seenRole.getDisplayName().getString() + ").");
            } else {
                if (player.level().getServer() != null) {
                    player.level().getServer().execute(() -> openCenterMenu(player, session, new ArrayList<>()));
                }
            }
        });
    }

    private void openCenterMenu(ServerPlayer player, GameSession session, List<Integer> seenIndexes) {
        List<ItemStack> centerOptions = new ArrayList<>();
        List<Integer> availableIndexes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            if (!seenIndexes.contains(i)) {
                centerOptions.add(MenuIcons.makeChoiceCard("Carte " + (i + 1)));
                availableIndexes.add(i);
            }
        }

        MenuHelper.openChoiceMenu(player, "§8Voyante : Centre (" + (seenIndexes.size() + 1) + "/2)", centerOptions, index -> {
            int chosenIndex = availableIndexes.get(index);
            Role seenRole = session.getBoard().getCenterCard(chosenIndex);

            player.sendSystemMessage(Component.literal("§dCarte du centre n°" + (chosenIndex + 1) + " : §l" + seenRole.getDisplayName().getString()));
            seenIndexes.add(chosenIndex);

            if (seenIndexes.size() < 2) {
                if (player.level().getServer() != null) {
                    player.level().getServer().execute(() -> openCenterMenu(player, session, seenIndexes));
                }
            } else {
                session.addHistory("La Voyante (" + player.getPlainTextName() + ") a regardé le centre " + (seenIndexes.get(0) + 1) + " et " + (seenIndexes.get(1) + 1) + ".");
            }
        });
    }
}