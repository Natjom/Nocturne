package natjom.nocturne.game.role.vampire;

import natjom.nocturne.game.GameSession;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.game.role.base.TanneurRole;
import natjom.nocturne.game.role.crepuscule.Artefact;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ApprentiTanneurRole extends Role implements VampireExtensionRole {

    @Override
    public Component getDisplayName() {
        return Component.literal("§6Apprenti Tanneur");
    }

    @Override
    public int getNightOrder() {
        return 18;
    }

    @Override
    public String getSkinTexture() {
        return "";
    }

    @Override
    public boolean hasNightAction() {
        return true;
    }

    @Override
    public void onWakeUp(ServerPlayer player, GameSession session) {
        Optional<ServerPlayer> tannerPlayer = session.getServerPlayers().stream()
                .filter(p -> session.getBoard().getInitialRole(p.getUUID()) instanceof TanneurRole)
                .findFirst();

        if (tannerPlayer.isPresent()) {
            player.sendSystemMessage(Component.literal("§6Le Tanneur de ce village est : " + tannerPlayer.get().getPlainTextName()));
        } else {
            player.sendSystemMessage(Component.literal("§6Il n'y a pas de Tanneur en jeu cette nuit. Tu es désormais le Tanneur."));
        }

        session.getBoard().addPlayerAction(player.getUUID());
    }

    @Override
    public boolean didWin(GameSession session, UUID myId, List<UUID> eliminated) {
        boolean tannerInPlay = false;
        boolean tannerDied = false;

        for (ServerPlayer p : session.getServerPlayers()) {
            Role currentRole = session.getBoard().getCurrentRole(p.getUUID());
            Artefact currentArt = session.getBoard().getArtifact(p.getUUID());

            if (currentRole instanceof TanneurRole || currentArt == Artefact.GOURDIN_TANNEUR) {
                tannerInPlay = true;
                if (eliminated.contains(p.getUUID())) {
                    tannerDied = true;
                }
            }
        }

        if (tannerInPlay) {
            return tannerDied;
        } else {
            return eliminated.contains(myId);
        }
    }
}