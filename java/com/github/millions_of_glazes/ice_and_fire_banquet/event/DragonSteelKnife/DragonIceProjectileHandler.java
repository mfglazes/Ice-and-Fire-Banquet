package com.github.millions_of_glazes.ice_and_fire_banquet.event.DragonSteelKnife;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityDragonIceCharge;
import com.github.alexthe666.iceandfire.entity.props.EntityDataProvider;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = "ice_and_fire_banquet", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DragonIceProjectileHandler {

    private static final Map<ResourceLocation, Supplier<Block>> ICE_CONVERSION_MAP = new HashMap<>();
    private static final Map<ResourceLocation, Float> ICE_FACTORS = new HashMap<>();
    private static volatile boolean mapsInitialized = false;

    private static final float SPIKE_CHANCE = 0.8f;
    private static final float BASE_CONVERT_CHANCE = 0.7f;
    private static final int CONVERT_RADIUS = 2;
    private static final float CENTER_DRAGON_ICE = 0.3f;
    private static final float EXPLOSION_POWER = 2.5f;

    private static void initMaps() {
        if (mapsInitialized) return;
        synchronized (ICE_CONVERSION_MAP) {
            if (mapsInitialized) return;

            ICE_CONVERSION_MAP.put(ResourceLocation.tryParse("grass_block"), IafBlockRegistry.FROZEN_GRASS);
            ICE_CONVERSION_MAP.put(ResourceLocation.tryParse("dirt"), IafBlockRegistry.FROZEN_DIRT);
            ICE_CONVERSION_MAP.put(ResourceLocation.tryParse("dirt_path"), IafBlockRegistry.FROZEN_DIRT_PATH);
            ICE_CONVERSION_MAP.put(ResourceLocation.tryParse("stone"), IafBlockRegistry.FROZEN_STONE);
            ICE_CONVERSION_MAP.put(ResourceLocation.tryParse("cobblestone"), IafBlockRegistry.FROZEN_COBBLESTONE);
            ICE_CONVERSION_MAP.put(ResourceLocation.tryParse("gravel"), IafBlockRegistry.FROZEN_GRAVEL);
            ICE_CONVERSION_MAP.put(ResourceLocation.tryParse("water"), () -> Blocks.ICE);

            ICE_FACTORS.put(ResourceLocation.tryParse("stone"), 0.6f);
            ICE_FACTORS.put(ResourceLocation.tryParse("cobblestone"), 0.6f);

            mapsInitialized = true;
        }
    }

    @SubscribeEvent
    public static void onIceProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getEntity() instanceof EntityDragonIceCharge iceCharge)) return;
        if (!(iceCharge.getOwner() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        initMaps();

        HitResult hit = event.getRayTraceResult();
        Level level = player.level();

        float damage = (float) (IafConfig.dragonAttackDamageIce * 5.0);
        if (hit.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) hit).getEntity();
            if (target == player) return;

            DamageSource source = IafDamageRegistry.causeDragonIceDamage(player);
            target.hurt(source, damage);

            if (target instanceof LivingEntity living) {
                EntityDataProvider.getCapability(living).ifPresent(data ->
                        data.frozenData.setFrozen(living, 300)
                );
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 2));
                living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1));
            }
        }

        level.explode(player,
                iceCharge.getX(), iceCharge.getY(), iceCharge.getZ(),
                EXPLOSION_POWER, false, Level.ExplosionInteraction.BLOCK);

        BlockPos center = BlockPos.containing(iceCharge.getX(), iceCharge.getY(), iceCharge.getZ());
        freezeArea(level, center);

        iceCharge.discard();
        event.setResult(Event.Result.DENY);
    }

    private static void freezeArea(Level level, BlockPos center) {
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-CONVERT_RADIUS, -CONVERT_RADIUS, -CONVERT_RADIUS),
                center.offset(CONVERT_RADIUS, CONVERT_RADIUS, CONVERT_RADIUS))) {

            BlockState state = level.getBlockState(pos);
            if (state.isAir()) continue;

            ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
            if (blockId == null) continue;

            Supplier<Block> targetSupplier = ICE_CONVERSION_MAP.get(blockId);
            if (targetSupplier == null) continue;

            Block target = targetSupplier.get();
            if (target == null || state.getBlock() == target) continue;

            float factor = ICE_FACTORS.getOrDefault(blockId, 1.0f);
            if (level.random.nextFloat() > BASE_CONVERT_CHANCE * factor) {
                continue;
            }

            boolean converted = false;

            if (pos.equals(center) && level.random.nextFloat() < CENTER_DRAGON_ICE) {
                Block dragonIce = IafBlockRegistry.DRAGON_ICE.get();
                if (dragonIce != null) {
                    level.setBlock(pos, dragonIce.defaultBlockState(), 3);
                    converted = true;
                } else {
                    level.setBlock(pos, target.defaultBlockState(), 3);
                    converted = true;
                }
            } else {
                level.setBlock(pos, target.defaultBlockState(), 3);
                converted = true;
            }

            if (converted && level.random.nextFloat() < SPIKE_CHANCE) {
                BlockPos above = pos.above();
                if (level.getBlockState(above).canBeReplaced()) {
                    Block spike = IafBlockRegistry.DRAGON_ICE_SPIKES.get();
                    if (spike != null) {
                        level.setBlock(above, spike.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}