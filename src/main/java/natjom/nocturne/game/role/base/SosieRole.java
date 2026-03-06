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

public class SosieRole extends Role {

    @Override
    public Component getDisplayName() {
        return Component.literal("Sosie");
    }

    @Override
    public int getNightOrder() {
        return 1;
    }

    @Override
    public String getSkinTexture() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODJiM2IwOTM5NjI1YWY4YzJmNWVkOGMyNjlkM2JhNjZmNWI2NWNlM2I1MjVkNmMxZTA1ZjRlN2NkZWJkMzljYiJ9fX0=";
    }

    @Override
    public int getActionDuration() { return 280; }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<ItemStack> options = new ArrayList<>();
        List<ServerPlayer> validTargets = new ArrayList<>();

        for (ServerPlayer target : session.getServerPlayers()) {
            if (!target.getUUID().equals(player.getUUID())) {
                options.add(MenuIcons.makePlayerHead(target, "§e"));
                validTargets.add(target);
            }
        }

        MenuHelper.openChoiceMenu(player, "§8Sosie : Copier qui ?", options, index -> {
            ServerPlayer target = validTargets.get(index);
            Role copiedRole = session.getBoard().getCurrentRole(target.getUUID());

            player.sendSystemMessage(Component.literal("§dTu as copié le rôle de " + target.getPlainTextName() + "."));
            player.sendSystemMessage(Component.literal("§dTu es maintenant : §l" + copiedRole.getDisplayName().getString()));

            session.addHistory("Le Sosie (" + player.getPlainTextName() + ") a copié " + target.getPlainTextName() + " (" + copiedRole.getDisplayName().getString() + ").");

            if (copiedRole.hasNightAction() && copiedRole.getNightOrder() > this.getNightOrder()) {
                if (player.level().getServer() != null) {
                    player.level().getServer().execute(() -> copiedRole.onWakeUp(player, session));
                }
            }
        });
    }
}
