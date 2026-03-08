package natjom.nocturne.gui;

import natjom.nocturne.game.CompoSet;
import natjom.nocturne.game.CompositionManager;
import natjom.nocturne.game.role.Role;
import natjom.nocturne.registry.NocturneRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ResolvableProfile;
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
                (id, inv, p) -> new ChoiceMenu(id, inv, container),
                Component.literal(title)
        ));
    }

    public static void openCompoMenu(ServerPlayer player) {
        openCompoMenu(player, 0);
    }

    public static void openCompoMenu(ServerPlayer player, int page) {
        natjom.nocturne.game.CompositionManager.initDefault();

        List<Role> allRoles = new java.util.ArrayList<>();
        NocturneRegistries.ROLES.getEntries().forEach(entry -> allRoles.add(entry.get()));

        int maxPerPage = 18;
        int maxPages = (int) Math.ceil((double) allRoles.size() / maxPerPage);

        final int finalPage = Math.max(0, Math.min(page, maxPages - 1));
        final int start = finalPage * maxPerPage;
        final int end = Math.min(start + maxPerPage, allRoles.size());

        List<ItemStack> options = new java.util.ArrayList<>();
        for (int i = start; i < end; i++) {
            Role role = allRoles.get(i);
            int count = natjom.nocturne.game.CompositionManager.COMPOSITION.getOrDefault(role, 0);
            MutableComponent itemName = Component.literal("");

            ItemStack icon;
            if (role.getSkinTexture().isEmpty()) {
                icon = new ItemStack(Items.PAPER);
            } else {
                icon = new ItemStack(Items.PLAYER_HEAD);

                com.google.common.collect.ImmutableMultimap<String, com.mojang.authlib.properties.Property> properties = com.google.common.collect.ImmutableMultimap.of(
                        "textures", new com.mojang.authlib.properties.Property("textures", role.getSkinTexture())
                );
                com.mojang.authlib.properties.PropertyMap propertyMap = new com.mojang.authlib.properties.PropertyMap(properties);

                com.mojang.authlib.GameProfile profile = new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "RoleIcon", propertyMap);
                icon.set(DataComponents.PROFILE, ResolvableProfile.createResolved(profile));
            }

            if (count > 0) {
                icon.setCount(count);
                itemName.append("§a§l" + role.getDisplayName().getString() + " §2[x" + count + "]");
            } else {
                icon.setCount(1);
                itemName.append("§c" + role.getDisplayName().getString() + " §8[Inactif]");
            }

            icon.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, itemName);
            icon.set(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            options.add(icon);
        }

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

                if (player.level().getServer() != null) {
                    player.level().getServer().execute(() -> {
                        if (index == 18 && finalPage > 0) {
                            openCompoMenu(player, finalPage - 1);
                        } else if (index == 26 && end < allRoles.size()) {
                            openCompoMenu(player, finalPage + 1);
                        } else if (index >= 0 && index < options.size()) {
                            CompositionManager.cycleRole(allRoles.get(start + index));
                            openCompoMenu(player, finalPage);
                        }
                    });
                }
            }
        };

        for (int i = 0; i < options.size(); i++) {
            container.setItem(i, options.get(i));
        }

        if (finalPage > 0) {
            ItemStack prev = new ItemStack(Items.ARROW);
            prev.set(DataComponents.CUSTOM_NAME, Component.literal("§ePage précédente"));
            container.setItem(18, prev);
        }

        if (end < allRoles.size()) {
            ItemStack next = new ItemStack(Items.ARROW);
            next.set(DataComponents.CUSTOM_NAME, Component.literal("§ePage suivante"));
            container.setItem(26, next);
        }

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ChoiceMenu(id, inv, container),
                Component.literal("§8Composition (" + (finalPage + 1) + "/" + maxPages + ")")
        ));
    }

    public static void openCompoSetMenu(ServerPlayer player) {
        List<ItemStack> options = new java.util.ArrayList<>();
        CompoSet[] sets = CompoSet.values();

        for (CompoSet set : sets) {
            ItemStack icon = new ItemStack(Items.BOOK);
            boolean isActive = (CompositionManager.activeCompoSet == set);
            int count = isActive ? CompositionManager.activeCompoPlayerCount : set.getMinPlayers();

            if (count > 0 && count <= 64) {
                icon.setCount(count);
            }

            if (isActive) {
                icon.set(net.minecraft.core.component.DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            }

            MutableComponent name = Component.literal(set.getDisplayName());
            if (isActive) {
                name.append(" §a(Sélectionné : " + count + " joueurs)");
            } else {
                name.append(" §7(" + set.getMinPlayers() + "-" + set.getMaxPlayers() + " joueurs)");
            }

            icon.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, name);
            options.add(icon);
        }

        net.minecraft.world.SimpleContainer container = new net.minecraft.world.SimpleContainer(27) {
            @Override
            public @NonNull ItemStack removeItem(int index, int count) { triggerChoice(index); return ItemStack.EMPTY; }
            @Override
            public @NonNull ItemStack removeItemNoUpdate(int index) { triggerChoice(index); return ItemStack.EMPTY; }
            @Override
            public void setItem(int index, @NonNull ItemStack stack) {
                if (this.getItem(index).isEmpty()) super.setItem(index, stack);
            }

            private void triggerChoice(int index) {
                if (super.getItem(index).isEmpty()) return;

                if (player.level().getServer() != null) {
                    player.level().getServer().execute(() -> {
                        if (index >= 0 && index < sets.length) {
                            CompoSet clickedSet = sets[index];
                            int newCount = clickedSet.getMinPlayers();

                            if (CompositionManager.activeCompoSet == clickedSet) {
                                newCount = CompositionManager.activeCompoPlayerCount + 1;
                                if (newCount > clickedSet.getMaxPlayers()) {
                                    newCount = clickedSet.getMinPlayers();
                                }
                            }

                            CompositionManager.applyCompoSet(clickedSet, newCount);
                            openCompoSetMenu(player);
                        }
                    });
                }
            }
        };

        for (int i = 0; i < options.size(); i++) {
            container.setItem(i, options.get(i));
        }

        player.openMenu(new net.minecraft.world.SimpleMenuProvider(
                (id, inv, p) -> new ChoiceMenu(id, inv, container),
                Component.literal("§8Sélection du Set de Partie")
        ));
    }
}