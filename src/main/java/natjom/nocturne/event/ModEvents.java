package natjom.nocturne.event;

import natjom.nocturne.Nocturne;
import natjom.nocturne.command.NocturneCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Nocturne.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        NocturneCommand.register(event.getDispatcher());
    }
}