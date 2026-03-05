package natjom.nocturne.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

public class MenuHelper {

    public static void openChoiceMenu(ServerPlayer player, String title, List<ItemStack> options, Consumer<Integer> onChoice) {
        int startSlot = 13 - (options.size() / 2);
        if (options.size() % 2 == 0) {
            startSlot = 14 - (options.size() / 2);
        }
        final int finalStartSlot = startSlot;

        SimpleContainer container = new SimpleContainer(27) {

            @Override
            public @NonNull ItemStack removeItem(int index, int count) {
                triggerChoice(index);
                return ItemStack.EMPTY;
            }

            @Override
            public @NonNull ItemStack removeItemNoUpdate(int index) {
                triggerChoice(index);
                return ItemStack.EMPTY;
            }

            @Override
            public void setItem(int index, @NonNull ItemStack stack) {
                if (this.getItem(index).isEmpty()) {
                    super.setItem(index, stack);
                }
            }

            private void triggerChoice(int index) {
                if (super.getItem(index).isEmpty()) return;

                int optionIndex = index - finalStartSlot;

                if (player.level().getServer() != null) {
                    player.level().getServer().execute(() -> {
                        player.closeContainer();
                        if (optionIndex >= 0 && optionIndex < options.size()) {
                            onChoice.accept(optionIndex);
                        }
                    });
                }
            }
        };

        for (int i = 0; i < options.size(); i++) {
            container.setItem(startSlot + i, options.get(i));
        }

        player.openMenu(new SimpleMenuProvider(
                (id, inv, _) -> new ChoiceMenu(id, inv, container),
                Component.literal(title)
        ));
    }

    public static void openCompoMenu(ServerPlayer player) {
        natjom.nocturne.game.CompositionManager.initDefault();

        List<natjom.nocturne.game.role.Role> allRoles = new java.util.ArrayList<>();
        natjom.nocturne.registry.NocturneRegistries.ROLES.getEntries().forEach(entry -> allRoles.add(entry.get()));

        List<ItemStack> options = new java.util.ArrayList<>();
        for (natjom.nocturne.game.role.Role role : allRoles) {
            int count = natjom.nocturne.game.CompositionManager.COMPOSITION.getOrDefault(role, 0);

            ItemStack icon;
            if (role.getSkinTexture().isEmpty()) {
                icon = new ItemStack(net.minecraft.world.item.Items.PAPER);
            } else {
                icon = new ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD);

                com.google.common.collect.ImmutableMultimap<String, com.mojang.authlib.properties.Property> properties = com.google.common.collect.ImmutableMultimap.of(
                        "textures", new com.mojang.authlib.properties.Property("textures", role.getSkinTexture())
                );
                com.mojang.authlib.properties.PropertyMap propertyMap = new com.mojang.authlib.properties.PropertyMap(properties);

                com.mojang.authlib.GameProfile profile = new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "RoleIcon", propertyMap);
                icon.set(net.minecraft.core.component.DataComponents.PROFILE, net.minecraft.world.item.component.ResolvableProfile.createResolved(profile));
            }

            if (count > 0) {
                icon.setCount(count);
            }

            icon.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§e" + role.getDisplayName().getString() + " §7(Actuel : §b" + count + "§7)"));
            icon.set(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS, net.minecraft.world.item.component.ItemAttributeModifiers.EMPTY);
            options.add(icon);
        }

        SimpleContainer container = new SimpleContainer(27) {
            @Override
            public ItemStack removeItem(int index, int count) {
                triggerChoice(index);
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack removeItemNoUpdate(int index) {
                triggerChoice(index);
                return ItemStack.EMPTY;
            }

            @Override
            public void setItem(int index, ItemStack stack) {
                if (this.getItem(index).isEmpty()) {
                    super.setItem(index, stack);
                }
            }

            private void triggerChoice(int index) {
                if (super.getItem(index).isEmpty()) return;

                if (player.level().getServer() != null) {
                    player.level().getServer().execute(() -> {
                        if (index >= 0 && index < allRoles.size()) {
                            natjom.nocturne.game.CompositionManager.cycleRole(allRoles.get(index));
                            openCompoMenu(player);
                        }
                    });
                }
            }
        };

        for (int i = 0; i < options.size(); i++) {
            container.setItem(i, options.get(i));
        }

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ChoiceMenu(id, inv, container),
                Component.literal("§8Composition de la partie")
        ));
    }
}