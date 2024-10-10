package com.oxtaly.flowerdoubling;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = FlowerDoubling.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue USE_FERTILIZABLE_FLOWER_TAG = BUILDER
            .comment("Whether to use the #minecraft:fertiliser tag.")
            .define("useFertilizableFlowerTag", true);

    private static final ForgeConfigSpec.DoubleValue DOUBLING_CHANCE = BUILDER
            .comment("Chance to double, if 0 or 100 the chance is 100%")
            .defineInRange("doublingChance", 100.0, 0.0, 100.0);

    private static final ForgeConfigSpec.BooleanValue ALLOW_WITHER_ROSES = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("allowWitherRoses", false);

    private static final ForgeConfigSpec.DoubleValue WITHER_ROSES_DOUBLING_CHANCE = BUILDER
            .comment("Chance to double, if 0 or 100 the chance is 100%")
            .defineInRange("witherRosesDoublingChance", 5.0, 0.0, 100.0);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean useFertilizableFlowerTag;
    public static double doublingChance;
    public static boolean allowWitherRoses;
    public static double witherRosesDoublingChance;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        load();
    }

    public static void load() {
        useFertilizableFlowerTag = USE_FERTILIZABLE_FLOWER_TAG.get();
        doublingChance = DOUBLING_CHANCE.get();
        if(doublingChance == 0.0)
            doublingChance = 100.0;
        allowWitherRoses = ALLOW_WITHER_ROSES.get();
        witherRosesDoublingChance = WITHER_ROSES_DOUBLING_CHANCE.get();
        if(witherRosesDoublingChance == 0.0)
            witherRosesDoublingChance = 100.0;

    }
}
