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

public class PestifereeRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aPestiférée");
    }

    @Override
    public int getNightOrder() {
        return 6;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI3NmVjMjlmNzA0MjdiMTllYmUwNDVlMjQ2ZmNhYjdmM2IyMjU2MzIzZDA3MTQxYjBlZGY1ZjU4ZTk4Yjg2MCJ9fX0=";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<UUID> circle = session.getBoard().getCircleOrder();
        int myIndex = circle.indexOf(player.getUUID());

        int leftIndex = (myIndex + 1) % circle.size();
        int rightIndex = (myIndex - 1 + circle.size()) % circle.size();

        UUID leftId = circle.get(leftIndex);
        UUID rightId = circle.get(rightIndex);

        ServerPlayer leftPlayer = session.getServerPlayers().stream().filter(p -> p.getUUID().equals(leftId)).findFirst().orElse(null);
        ServerPlayer rightPlayer = session.getServerPlayers().stream().filter(p -> p.getUUID().equals(rightId)).findFirst().orElse(null);

        List<ItemStack> options = new ArrayList<>();
        options.add(MenuIcons.makeChoiceCard("§2Voisin de Gauche" + (leftPlayer != null ? " (" + leftPlayer.getPlainTextName() + ")" : "")));
        options.add(MenuIcons.makeChoiceCard("§2Voisin de Droite" + (rightPlayer != null ? " (" + rightPlayer.getPlainTextName() + ")" : "")));

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis le voisin à qui transmettre la Marque de la Maladie."));

        MenuHelper.openChoiceMenu(player, "§8Transmettre la Maladie", options, index -> {
            UUID targetId = (index == 0) ? leftId : rightId;
            ServerPlayer targetPlayer = (index == 0) ? leftPlayer : rightPlayer;

            if (session.getBoard().isShielded(targetId)) {
                player.sendSystemMessage(Component.literal("§cCe joueur est protégé par un bouclier ! La maladie ne l'atteint pas."));
                session.addHistory("La Pestiférée (" + player.getPlainTextName() + ") a tenté d'infecter " + (targetPlayer != null ? targetPlayer.getPlainTextName() : "quelqu'un") + " mais un bouclier l'a bloqué.");
            } else {
                session.getBoard().setPlayerMarque(targetId, Marque.PESTE);
                player.sendSystemMessage(Component.literal("§2Tu as placé la Marque de la Maladie sur " + (targetPlayer != null ? targetPlayer.getPlainTextName() : "ton voisin") + "."));
                session.addHistory("La Pestiférée (" + player.getPlainTextName() + ") a infecté " + (targetPlayer != null ? targetPlayer.getPlainTextName() : "son voisin") + ".");
            }

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}