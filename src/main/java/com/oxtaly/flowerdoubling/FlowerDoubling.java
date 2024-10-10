package com.oxtaly.flowerdoubling;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FlowerDoubling.MODID)
public class FlowerDoubling
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "flowerdoubling";

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation("flowerdoubling", "flowerdoubling"), () -> {
        return "1";
    }, "1"::equals, "1"::equals);
    private static int messageID = 0;
    private static final List<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ArrayList<>();


    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public FlowerDoubling() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // // Register the commonSetup method for modloading
        // modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        ++messageID;
    }

    public static void queueServerWork(int tick, Runnable action) {
        workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
            workQueue.forEach((work) -> {
                work.setValue((Integer)work.getValue() - 1);
                if ((Integer)work.getValue() == 0) {
                    actions.add(work);
                }

            });
            actions.forEach((e) -> {
                ((Runnable)e.getKey()).run();
            });
            workQueue.removeAll(actions);
        }

    }

    // private void commonSetup(final FMLCommonSetupEvent event) {
    //     // Some common setup code
    //     LOGGER.info("@FlowerDoubling common setup");
    // }
    //
    // // You can use SubscribeEvent and let the Event Bus discover methods to call
    // @SubscribeEvent
    // public void onServerStarting(ServerStartingEvent event) {
    //     // Do something when the server starts
    //     LOGGER.info("@FlowerDoubling server starting");
    // }
    //
    // // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    // @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    // public static class ClientModEvents {
    //     @SubscribeEvent
    //     public static void onClientSetup(FMLClientSetupEvent event) {
    //         // Some client setup code
    //         LOGGER.info("@FlowerDoubling client starting");
    //     }
    // }
}
