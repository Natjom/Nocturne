package natjom.nocturne;

import natjom.nocturne.registry.NocturneRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Nocturne.MODID)
public class Nocturne {
    public static final String MODID = "nocturne";

    public Nocturne(IEventBus modEventBus) {
        NocturneRegistries.ROLES.register(modEventBus);
    }
}