package natjom.nocturne.game.role.base;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;

public class SoeursRole extends Role {

    @Override
    public Component getDisplayName() { return Component.literal("§aSoeur"); }

    @Override
    public int getNightOrder() { return 19; }

    @Override
    public String getSkinTexture() { return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I3MzhhZDdmZmE3MTMyODc2ZjI2NmM0YzZlNDhlZTQwMDlmNmM4ZmM0MDczZDJiOGM5ZTU0ZTllNzU2MmZlIn19fQ=="; }

    @Override
    public int getActionDuration() { return 60; }

    @Override
    public boolean hasNightAction() { return true; }

    @Override
    public void onWakeUp(net.minecraft.server.level.ServerPlayer player, natjom.nocturne.game.GameSession session) {
        java.util.List<String> sisterNames = session.getServerPlayers().stream()
                .filter(p -> !p.getUUID().equals(player.getUUID()))
                .filter(p -> {
                    Role initialRole = session.getBoard().getInitialRole(p.getUUID());
                    boolean isSoeur = initialRole instanceof SoeursRole;
                    boolean isSosieSoeur = initialRole instanceof SosieRole &&
                            ((SosieRole) initialRole).getCopiedRole() instanceof SoeursRole;
                    return isSoeur || isSosieSoeur;
                })
                .map(net.minecraft.server.level.ServerPlayer::getPlainTextName)
                .toList();

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c[Nuit] Tu ouvres les yeux pour chercher ta soeur..."));

        if (sisterNames.isEmpty()) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§dTu es seule, malheureusement."));
            session.addHistory("La Soeur (" + player.getPlainTextName() + ") s'est réveillée seule.");
        } else {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§dTa soeur est : §l" + String.join(", ", sisterNames)));
            session.addHistory("La Soeur (" + player.getPlainTextName() + ") a vu sa soeur : " + String.join(", ", sisterNames) + ".");
        }
    }
}