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

public class GuetteurRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("§aGuetteur");
    }

    @Override
    public int getNightOrder() {
        return 12;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2NDY5MTk2NjAyOTkwYTlkZjcwMGYzMGY1YzI5NDMzOGRjN2I2YzljNjU5MTZkZWFmYTBlYmVmM2ZlNjJlOCJ9fX0=";
    }

    @Override
    public boolean hasNightAction() { return true; }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(player.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§2"));
                validTargets.add(target);
            }
        }

        player.sendSystemMessage(Component.literal("§c[Nuit] Choisis un joueur à protéger avec ton jeton bouclier."));

        MenuHelper.openChoiceMenu(player, "§8Placer le bouclier", options, index -> {
            ServerPlayer target = validTargets.get(index);
            session.getBoard().addShield(target.getUUID());
            player.sendSystemMessage(Component.literal("§aTu as placé ton jeton bouclier sur la carte de " + target.getPlainTextName() + "."));
            session.addHistory("Le Guetteur (" + player.getPlainTextName() + ") a protégé la carte de " + target.getPlainTextName() + ".");
        });
    }


}
