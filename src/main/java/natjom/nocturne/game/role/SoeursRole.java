package natjom.nocturne.game.role;

import natjom.nocturne.game.GameSession;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;

public class SoeursRole extends Role {

    @Override
    public Component getDisplayName() { return Component.literal("Soeur"); }

    @Override
    public int getNightOrder() { return 19; }

    @Override
    public String getSkinTexture() { return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I3MzhhZDdmZmE3MTMyODc2ZjI2NmM0YzZlNDhlZTQwMDlmNmM4ZmM0MDczZDJiOGM5ZTU0ZTllNzU2MmZlIn19fQ=="; }

    @Override
    public boolean hasNightAction() { return true; }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        List<String> sisterNames = session.getServerPlayers().stream()
                .filter(p -> !p.getUUID().equals(player.getUUID()))
                .filter(p -> session.getBoard().getInitialRole(p.getUUID()) instanceof SoeursRole)
                .map(ServerPlayer::getPlainTextName)
                .toList();

        player.sendSystemMessage(Component.literal("§c[Nuit] Tu ouvres les yeux pour chercher ta soeur..."));

        if (sisterNames.isEmpty()) {
            player.sendSystemMessage(Component.literal("§dTu es seule, malheureusement."));
            session.addHistory("La Soeur (" + player.getPlainTextName() + ") s'est réveillée seule.");
        } else {
            player.sendSystemMessage(Component.literal("§dTa soeur est : §l" + String.join(", ", sisterNames)));
            session.addHistory("La Soeur (" + player.getPlainTextName() + ") a vu sa soeur : " + String.join(", ", sisterNames) + ".");
        }
    }
}