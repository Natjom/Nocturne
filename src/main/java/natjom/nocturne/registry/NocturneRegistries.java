package natjom.nocturne.registry;

import natjom.nocturne.Nocturne;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.game.role.VillagerRole;
import natjom.nocturne.game.role.WolfRole;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NocturneRegistries {

    public static final ResourceKey<Registry<Role>> ROLE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(Nocturne.MODID, "roles"));

    public static final DeferredRegister<Role> ROLES =
            DeferredRegister.create(ROLE_REGISTRY_KEY, Nocturne.MODID);

    // public static final Registry<Role> REGISTRY = ROLES.makeRegistry(builder -> { builder.defaultKey(Identifier.fromNamespaceAndPath(Nocturne.MODID, "villageois")); });

    public static final DeferredHolder<Role, Role> VILLAGEOIS =
            ROLES.register("villageois", VillagerRole::new);

    public static final DeferredHolder<Role, Role> LOUP_GAROU =
            ROLES.register("loup_garou", WolfRole::new);
}