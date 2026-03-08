package natjom.nocturne.registry;

import com.mojang.datafixers.kinds.App;
import natjom.nocturne.Nocturne;
import natjom.nocturne.game.role.*;
import natjom.nocturne.game.role.base.*;
import natjom.nocturne.game.role.crepuscule.*;
import natjom.nocturne.game.role.vampire.*;
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

    public static final Registry<Role> REGISTRY = ROLES.makeRegistry(builder -> { builder.defaultKey(Identifier.fromNamespaceAndPath(Nocturne.MODID, "villageois")); });

    // ----------------------------------------------------------------- //

    public static final DeferredHolder<Role, Role> VILLAGEOIS =
            ROLES.register("villageois", VillageoisRole::new);

    public static final DeferredHolder<Role, Role> CHASSEUR =
            ROLES.register("chasseur", ChasseurRole::new);

    public static final DeferredHolder<Role, Role> TANNEUR =
            ROLES.register("tanneur", TanneurRole::new);

    public static final DeferredHolder<Role, Role> POLITICIEN =
            ROLES.register("politicien", PoliticienRole::new);

    public static final DeferredHolder<Role, Role> PROTECTEUR =
            ROLES.register("protecteur", ProtecteurRole::new);

    // ----------------------------------------------------------------- //

    public static final DeferredHolder<Role, Role> SOSIE =
            ROLES.register("sosie", SosieRole::new);

    public static final DeferredHolder<Role, Role> VAMPIRE =
            ROLES.register("vampire", VampireRole::new);

    public static final DeferredHolder<Role, Role> LE_MAITRE =
            ROLES.register("le_maitre", LeMaitreRole::new);

    public static final DeferredHolder<Role, Role> LE_COMTE =
            ROLES.register("le_comte", LeComteRole::new);

    public static final DeferredHolder<Role, Role> RENFIELD =
            ROLES.register("renfield", RenfieldRole::new);

    public static final DeferredHolder<Role, Role> PESTIFEREE =
            ROLES.register("pestiferee", PestifereeRole::new);

    public static final DeferredHolder<Role, Role> CUPITON =
            ROLES.register("cupiton", CupidonRole::new);

    public static final DeferredHolder<Role, Role> COMPLOTEUSE =
            ROLES.register("completeuse", ComploteuseRole::new);

    public static final DeferredHolder<Role, Role> MOINE =
            ROLES.register("moine", MoineRole::new);

    public static final DeferredHolder<Role, Role> ASSASSIN =
            ROLES.register("assassin", AssassinRole::new);

    public static final DeferredHolder<Role, Role> APPRENTIE_ASSASSIN =
            ROLES.register("apprentie_assassin", ApprentieAssassinRole::new);



    // ----------------------------------------------------------------- //

    public static final DeferredHolder<Role, Role> GUETTEUR =
            ROLES.register("guetteur", GuetteurRole::new);

    public static final DeferredHolder<Role, Role> LOUP_GAROU =
            ROLES.register("loup_garou", LoupRole::new);

    public static final DeferredHolder<Role, Role> LOUP_REVEUR =
            ROLES.register("loup_reveur", LoupReveurRole::new);

    public static final DeferredHolder<Role, Role> LOUP_ALPHA =
            ROLES.register("loup_alpha", LoupAlphaRole::new);

    public static final DeferredHolder<Role, Role> LOUP_SHAMAN =
            ROLES.register("loup_shaman", LoupShamanRole::new);

    public static final DeferredHolder<Role, Role> SBIRE =
            ROLES.register("sbire", SbireRole::new);

    public static final DeferredHolder<Role, Role> APPRENTI_TANNEUR =
            ROLES.register("apprenti_tanneur", ApprentiTanneurRole::new);

    public static final DeferredHolder<Role, Role> SOEUR =
            ROLES.register("soeur", SoeursRole::new);

    public static final DeferredHolder<Role, Role> LA_CHOSE =
            ROLES.register("la_chose", LaChoseRole::new);

    public static final DeferredHolder<Role, Role> VOYANTE =
            ROLES.register("voyante", VoyanteRole::new);

    public static final DeferredHolder<Role, Role> APPRENTIE_VOYANTE =
            ROLES.register("apprentie_voyante", ApprentieVoyanteRole::new);

    public static final DeferredHolder<Role, Role> EXORCISTE =
            ROLES.register("exorciste", ExorcisteRole::new);

    public static final DeferredHolder<Role, Role> TRAPPEUR =
            ROLES.register("trappeur", TrappeurRole::new);

    public static final DeferredHolder<Role, Role> VOLEUR =
            ROLES.register("voleur", VoleurRole::new);

    public static final DeferredHolder<Role, Role> SORCIERE =
            ROLES.register("sorciere", SorciereRole::new);

    public static final DeferredHolder<Role, Role> PICKPOCKET =
            ROLES.register("pickpocket", PickpocketRole::new);

    public static final DeferredHolder<Role, Role> NOISEUSE =
            ROLES.register("noiseuse", NoiseuseRole::new);

    public static final DeferredHolder<Role, Role> LE_TOM =
            ROLES.register("le_tom", LeTomRole::new);

    public static final DeferredHolder<Role, Role> COMPTEUSE =
            ROLES.register("compteuse", CompteuseRole::new);

    public static final DeferredHolder<Role, Role> FARFADET =
            ROLES.register("farfadet", FarfadetRole::new);

    public static final DeferredHolder<Role, Role> SOULARD =
            ROLES.register("soulard", SoulardRole::new);

    public static final DeferredHolder<Role, Role> INSOMNIAQUE =
            ROLES.register("insomniaque", InsomniaqueRole::new);

    public static final DeferredHolder<Role, Role> DIVINATEUR =
            ROLES.register("divinateur", DivinateurRole::new);

    public static final DeferredHolder<Role, Role> CONSERVATEUR =
            ROLES.register("conservateur", ConservateurRole::new);

    // ----------------------------------------------------------------- //
}