package com.oxtaly.flowerdoubling.procedures;

import com.oxtaly.flowerdoubling.Config;

import java.util.Objects;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber
public class MainProcedure {
    public MainProcedure() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == event.getEntity().getUsedItemHand()) {
            execute(event, event.getLevel(), (double)event.getPos().getX(), (double)event.getPos().getY(), (double)event.getPos().getZ(), event.getEntity());
        }
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        execute((Event)null, world, x, y, z, entity);
    }

    private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity != null) {

            try {

                if(Objects.isNull(Config.doublingChance)) {
                    return;
                }

                boolean useFertilizableFlowerTag = Config.useFertilizableFlowerTag;
                double doublingChance = Config.doublingChance;
                boolean allowWitherRoses = Config.allowWitherRoses;
                double witherRosesDoublingChance = Config.witherRosesDoublingChance;

                double random = Math.random() * 100.0;
                double random2 = Math.random() * 100.0;
                ItemStack handItem;
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity)entity;
                    handItem = livingEntity.getMainHandItem();
                } else {
                    handItem = ItemStack.EMPTY;
                }

                // Checks
                if(!handItem.is(ItemTags.create(new ResourceLocation("minecraft:fertiliser"))) || !useFertilizableFlowerTag)
                    return;

                boolean isWitherRose = Objects.equals(world.getBlockState(new BlockPos(x, y, z)).getBlock(), Blocks.WITHER_ROSE);

                if(!world.getBlockState(new BlockPos(x, y, z)).is(BlockTags.create(new ResourceLocation("minecraft:small_flowers"))) && !isWitherRose)
                    return;

                if(isWitherRose && !allowWitherRoses)
                    return;


                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity)entity;
                    livingEntity.swing(InteractionHand.MAIN_HAND, true);
                }
                //

                world.addParticle(ParticleTypes.COMPOSTER, x + 1.0 * Math.random(), y + 1.0 * Math.random(), z + 1.0 * Math.random(), 0.0, 1.0, 0.0);
                Level level;
                if (world instanceof Level) {
                    level = (Level)world;
                    if (level.isClientSide) {
                        level.playLocalSound(x, y, z, (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.grass.hit")), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
                    } else {
                        level.playSound((Player)null, new BlockPos(x, y, z), (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.grass.hit")), SoundSource.NEUTRAL, 1.0F, 1.0F);


                        ItemEntity entityToSpawn;
                        if(isWitherRose) {
                            if(witherRosesDoublingChance == 100.0 || random2 >= random - witherRosesDoublingChance / 2.0 && random2 <= random + witherRosesDoublingChance / 2.0) {
                                entityToSpawn = new ItemEntity(level, x + 0.5, y + 0.5, z + 0.5, new ItemStack(world.getBlockState(new BlockPos(x, y, z)).getBlock()));
                                entityToSpawn.setPickUpDelay(10);
                                level.addFreshEntity(entityToSpawn);
                            }
                        } else {
                            if(doublingChance == 100.0 || random2 >= random - doublingChance / 2.0 && random2 <= random + doublingChance / 2.0) {
                                entityToSpawn = new ItemEntity(level, x + 0.5, y + 0.5, z + 0.5, new ItemStack(world.getBlockState(new BlockPos(x, y, z)).getBlock()));
                                entityToSpawn.setPickUpDelay(10);
                                level.addFreshEntity(entityToSpawn);
                            }
                        }

                    }
                }

                if (entity instanceof Player) {
                    Player player = (Player)entity;
                    if (player.getAbilities().instabuild) {
                        return;
                    }
                }

                if (entity instanceof Player) {
                    Player player = (Player)entity;
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity)entity;
                        handItem = livingEntity.getMainHandItem();
                    } else {
                        handItem = ItemStack.EMPTY;
                    }

                    ItemStack itemToRemove = handItem;
                    player.getInventory().clearOrCountMatchingItems((p) -> {
                        return itemToRemove.getItem() == p.getItem();
                    }, 1, player.inventoryMenu.getCraftSlots());
                }
            } catch (Exception error) {
                error.printStackTrace();
            }

        }
    }
}
