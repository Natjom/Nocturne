package natjom.nocturne.command;

import com.mojang.brigadier.CommandDispatcher;
import natjom.nocturne.game.GameSession;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class NocturneCommand {

    public static GameSession currentSession = null;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nocturne")
                .requires(source -> {
                    if (source.getEntity() instanceof ServerPlayer player) {
                        return source.getServer().getPlayerList().isOp(player.nameAndId());
                    }
                    return true;
                })
                .then(Commands.literal("start")
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            List<ServerPlayer> players = source.getServer().getPlayerList().getPlayers();

                            if (players.isEmpty()) {
                                source.sendSystemMessage(Component.literal("§cIl n'y a aucun joueur pour lancer la partie."));
                                return 0;
                            }

                            if (currentSession != null && currentSession.getState() != natjom.nocturne.game.GameState.IDLE && currentSession.getState() != natjom.nocturne.game.GameState.END) {
                                source.sendSystemMessage(Component.literal("§cUne partie est déjà en cours !"));
                                return 0;
                            }

                            currentSession = new GameSession(players);
                            currentSession.start();

                            source.sendSystemMessage(Component.literal("§aCréation de la partie réussie !"));
                            return 1;
                        })
                )
        );
    }
}