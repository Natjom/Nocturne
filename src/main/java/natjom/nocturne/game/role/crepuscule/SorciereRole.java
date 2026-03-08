package natjom.nocturne.game.role.crepuscule;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.gui.MenuHelper;
import natjom.nocturne.util.MenuIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SorciereRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aSorcière");
    }

    @Override
    public int getNightOrder() {
        return 27;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFmMzllNDE1MjViZDc4YjZmMGQzZWY3OWRmYWM5NTAwNzg1YzIyYTRjNGZmM2IyYWMzYTk4MGFlYTFiNDhkNyJ9fX0=";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<ItemStack> centerOptions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            centerOptions.add(MenuIcons.makeChoiceCard("Centre " + (i + 1)));
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis d'abord une carte du centre à regarder."));

        MenuHelper.openChoiceMenu(player, "§81. Regarder le centre", centerOptions, centerIndex -> {
            Role seenRole = session.getBoard().getCenterCard(centerIndex);
            player.sendSystemMessage(Component.literal("§dLa carte du centre n°" + (centerIndex + 1) + " est : §l" + seenRole.getDisplayName().getString()));

            List<ItemStack> playerOptions = new ArrayList<>();
            List<ServerPlayer> validTargets = new ArrayList<>();

            for (ServerPlayer target : session.getServerPlayers()) {
                if (!session.getBoard().isShielded(target.getUUID())) {
                    String prefix = target.getUUID().equals(player.getUUID()) ? "§a" : "§d";
                    playerOptions.add(MenuIcons.makePlayerHead(target, prefix));
                    validTargets.add(target);
                }
            }

            String menuTitle = "§8Donner : " + seenRole.getDisplayName().getString();

            MenuHelper.openChoiceMenu(player, menuTitle, playerOptions, targetIndex -> {
                ServerPlayer target = validTargets.get(targetIndex);

                session.getBoard().swapPlayerWithCenter(target.getUUID(), centerIndex);

                if (target.getUUID().equals(player.getUUID())) {
                    player.sendSystemMessage(Component.literal("§dTu as remplacé ta propre carte par la carte du centre."));
                } else {
                    player.sendSystemMessage(Component.literal("§dTu as donné cette carte à " + target.getPlainTextName() + "."));
                }

                session.addHistory("La Sorcière (" + player.getPlainTextName() + ") a regardé le centre " + (centerIndex + 1) + " et l'a donné à " + target.getPlainTextName() + ".");
                session.getBoard().addPlayerAction(player.getUUID());
            });
        });
    }
}