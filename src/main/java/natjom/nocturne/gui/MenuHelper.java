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
}