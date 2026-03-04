package natjom.nocturne.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

public class MenuIcons {

    public static ItemStack makePlayerHead(ServerPlayer target, String prefixColor) {
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);

        head.set(DataComponents.CUSTOM_NAME, Component.literal(prefixColor + target.getPlainTextName()));

        return head;
    }

    public static ItemStack makeChoiceCard(String label) {
        ItemStack card = new ItemStack(Items.PAPER);
        card.set(DataComponents.CUSTOM_NAME, Component.literal("§d" + label));
        return card;
    }
}