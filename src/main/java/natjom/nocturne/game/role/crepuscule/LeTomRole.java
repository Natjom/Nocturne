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

public class LeTomRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aLe Tom");
    }

    @Override
    public int getNightOrder() {
        return 30;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzhjYzhiM2IxNmRmZmRhMTZiZWEzNmUxZWY0OTBmY2QwNzc4ZTE2YTE1NTMxNjAzZGM3ZDdlMDI0MzI4ZWYzNyJ9fX0=";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<ItemStack> options = new ArrayList<>();
        options.add(MenuIcons.makeChoiceCard("Vers la Gauche"));
        options.add(MenuIcons.makeChoiceCard("Vers la Droite"));

        player.sendSystemMessage(Component.literal("§c[Nuit] Dans quel sens veux-tu décaler les cartes du village ?"));

        MenuHelper.openChoiceMenu(player, "§8Décaler les cartes", options, index -> {
            boolean toRight = (index == 1);

            List<ServerPlayer> targets = session.getServerPlayers().stream()
                    .filter(p -> !p.getUUID().equals(player.getUUID()))
                    .filter(p -> !session.getBoard().isShielded(p.getUUID()))
                    .toList();

            if (targets.size() > 1) {
                List<Role> currentRoles = new ArrayList<>();
                for (ServerPlayer t : targets) {
                    currentRoles.add(session.getBoard().getCurrentRole(t.getUUID()));
                }

                for (int i = 0; i < targets.size(); i++) {
                    int newIndex;
                    if (toRight) {
                        newIndex = (i - 1 + targets.size()) % targets.size();
                    } else {
                        newIndex = (i + 1) % targets.size();
                    }
                    session.getBoard().setCurrentRole(targets.get(i).getUUID(), currentRoles.get(newIndex));
                }
            }

            String sens = toRight ? "la droite" : "la gauche";
            player.sendSystemMessage(Component.literal("§dTu as décalé les cartes non protégées vers " + sens + "."));
            session.addHistory("Le Tom (" + player.getPlainTextName() + ") a décalé toutes les cartes vers " + sens + ".");
            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}