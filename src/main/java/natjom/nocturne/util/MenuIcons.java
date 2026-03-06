package natjom.nocturne.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

public class MenuIcons {

    public static net.minecraft.world.item.ItemStack makePlayerHead(net.minecraft.server.level.ServerPlayer target, String prefixColor) {

        net.minecraft.world.item.ItemStack head = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD);

        head.set(net.minecraft.core.component.DataComponents.PROFILE, net.minecraft.world.item.component.ResolvableProfile.createResolved(target.getGameProfile()));
        head.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal(prefixColor + target.getPlainTextName()));

        return head;
    }

    public static ItemStack makeChoiceCard(String label) {
        ItemStack card = new ItemStack(Items.PAPER);
        card.set(DataComponents.CUSTOM_NAME, Component.literal("§d" + label));
        return card;
    }
}