package natjom.nocturne.game.role.vampire;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.gui.MenuHelper;
import natjom.nocturne.util.MenuIcons;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LaChoseRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aLa Chose");
    }

    @Override
    public int getNightOrder() {
        return 20;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGJiNGNjNzhjMDEyYjhmYzQ5YmMyZjU5NjI5MmY3NzI4OWNiYjZhNWMyNzJlNDU1MGRkZDZmMGNmZTZlYzQ4MSJ9fX0=";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        UUID leftId = session.getBoard().getLeftNeighbor(player.getUUID());
        UUID rightId = session.getBoard().getRightNeighbor(player.getUUID());

        ServerPlayer leftPlayer = session.getServerPlayers().stream().filter(p -> p.getUUID().equals(leftId)).findFirst().orElse(null);
        ServerPlayer rightPlayer = session.getServerPlayers().stream().filter(p -> p.getUUID().equals(rightId)).findFirst().orElse(null);

        List<ItemStack> options = new ArrayList<>();
        options.add(MenuIcons.makeChoiceCard("§8Toucher Voisin Gauche" + (leftPlayer != null ? " (" + leftPlayer.getPlainTextName() + ")" : "")));
        options.add(MenuIcons.makeChoiceCard("§8Toucher Voisin Droite" + (rightPlayer != null ? " (" + rightPlayer.getPlainTextName() + ")" : "")));

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis le voisin que tu vas discrètement toucher."));

        MenuHelper.openChoiceMenu(player, "§8Toucher un voisin", options, index -> {
            ServerPlayer targetPlayer = (index == 0) ? leftPlayer : rightPlayer;
            String shoulder = (index == 0) ? "droite" : "gauche";

            if (targetPlayer != null) {
                targetPlayer.sendSystemMessage(Component.literal("§8[Nuit] *Tu sens une légère tape sur ton épaule " + shoulder + "...*"));
                targetPlayer.playSound(SoundEvents.PLAYER_ATTACK_WEAK, 1.0F, 1.0F);

                player.sendSystemMessage(Component.literal("§8Tu as touché " + targetPlayer.getPlainTextName() + "."));
                session.addHistory("La Chose (" + player.getPlainTextName() + ") a touché " + targetPlayer.getPlainTextName() + ".");
            }

            session.getBoard().addPlayerAction(player.getUUID());
        });
    }
}