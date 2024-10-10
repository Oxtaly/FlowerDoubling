package com.oxtaly.flowerdoubling.procedures;


import com.oxtaly.flowerdoubling.Config;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class TooltipsProcedure {
    public TooltipsProcedure() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        execute(event, event.getItemStack(), event.getToolTip());
    }

    public static void execute(ItemStack itemstack, List<Component> tooltip) {
        execute((Event)null, itemstack, tooltip);
    }

    private static void execute(@Nullable Event event, ItemStack itemstack, List<Component> tooltip) {
        if (tooltip != null) {
            try {
                if(Objects.isNull(Config.doublingChance)) {
                    return;
                }

                boolean useFertilizableFlowerTag = Config.useFertilizableFlowerTag;
                double doublingChance = Config.doublingChance;
                boolean allowWitherRoses = Config.allowWitherRoses;
                double witherRosesDoublingChance = Config.witherRosesDoublingChance;

                if(itemstack.getItem() == Blocks.WITHER_ROSE.asItem()) {
                    if(!allowWitherRoses) {
                        return;
                    }
                    if(!Screen.hasShiftDown()) {
                        tooltip.add(Component.literal("§8press shift for info§r"));
                        return;
                    }
                    tooltip.add(Component.literal(" "));
                    tooltip.add(Component.literal("§7Can be doubled with bonemeal.§r"));
                    tooltip.add(Component.literal("§7The doubling probability is §2" + (new DecimalFormat("##.##")).format(witherRosesDoublingChance) + "%§7.§r"));
                    return;
                }

                if(!itemstack.is(ItemTags.create(new ResourceLocation("minecraft:small_flowers"))) || !useFertilizableFlowerTag) {
                    return;
                }

                if(!Screen.hasShiftDown()) {
                    tooltip.add(Component.literal("§8press shift for info§r"));
                    return;
                }
                tooltip.add(Component.literal(" "));
                tooltip.add(Component.literal("§7Can be doubled with bonemeal.§r"));
                tooltip.add(Component.literal("§7The doubling probability is §2" + (new DecimalFormat("##.##")).format(doublingChance) + "%§7.§r"));
                return;
            } catch (Exception error) {
                error.printStackTrace();
            }

        }
    }
}
