package com.github.millions_of_glazes.ice_and_fire_banquet.event.DragonSteelKnife;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityDragonFireCharge;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
public class DragonFireProjectileHandler {

    private static final Map<ResourceLocation, Supplier<Block>> CONVERSION_MAP = new HashMap<>();
    private static final Map<ResourceLocation, Float> CONVERSION_FACTORS = new HashMap<>();
    private static volatile boolean mapsInitialized = false;

    private static final float ASH_CHANCE = 0.3f;
    private static final float CONVERT_CHANCE = 0.7f;
    private static final int CONVERT_RADIUS = 2;

    private static void initMaps() {
        if (mapsInitialized) return;
        synchronized (CONVERSION_MAP) {
            if (mapsInitialized) return;

            CONVERSION_MAP.put(ResourceLocation.tryParse("grass_block"), IafBlockRegistry.CHARRED_GRASS);
            CONVERSION_MAP.put(ResourceLocation.tryParse("dirt"), IafBlockRegistry.CHARRED_DIRT);
            CONVERSION_MAP.put(ResourceLocation.tryParse("dirt_path"), IafBlockRegistry.CHARRED_DIRT_PATH);
            CONVERSION_MAP.put(ResourceLocation.tryParse("cobblestone"), IafBlockRegistry.CHARRED_COBBLESTONE);
            CONVERSION_MAP.put(ResourceLocation.tryParse("stone"), IafBlockRegistry.CHARRED_STONE);
            CONVERSION_MAP.put(ResourceLocation.tryParse("gravel"), IafBlockRegistry.CHARRED_GRAVEL);

            CONVERSION_FACTORS.put(ResourceLocation.tryParse("stone"), 0.7f);
            CONVERSION_FACTORS.put(ResourceLocation.tryParse("cobblestone"), 0.7f);

            mapsInitialized = true;
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getEntity() instanceof EntityDragonFireCharge charge)) return;
        if (!(charge.getOwner() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        initMaps();   // 懒加载映射表，保证游戏已完全启动

        HitResult hit = event.getRayTraceResult();
        Level level = player.level();

        // 伤害处理
        if (hit.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) hit).getEntity();
            if (target == player) return;

            float damage = (float) (IafConfig.dragonAttackDamageFire * 5.0);
            DamageSource source = IafDamageRegistry.causeDragonFireDamage(player);
            target.hurt(source, damage);
            target.setSecondsOnFire(6);
        }

        // 爆炸
        level.explode(player,
                charge.getX(), charge.getY(), charge.getZ(),
                2.5F, true, Level.ExplosionInteraction.BLOCK);

        // 方块转化
        BlockPos center = BlockPos.containing(charge.getX(), charge.getY(), charge.getZ());

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-CONVERT_RADIUS, -CONVERT_RADIUS, -CONVERT_RADIUS),
                center.offset(CONVERT_RADIUS, CONVERT_RADIUS, CONVERT_RADIUS))) {

            BlockState state = level.getBlockState(pos);
            if (state.isAir()) continue;

            if (state.getBlock() == Blocks.SAND) {
                level.setBlock(pos, Blocks.GLASS.defaultBlockState(), 3);
                continue;
            }

            ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
            if (blockId == null) continue;

            Supplier<Block> targetSupplier = CONVERSION_MAP.get(blockId);
            if (targetSupplier == null) continue;

            Block target = targetSupplier.get();
            if (target == null || state.getBlock() == target) continue;

            float factor = CONVERSION_FACTORS.getOrDefault(blockId, 1.0f);
            if (level.random.nextFloat() > CONVERT_CHANCE * factor) {
                continue;
            }

            if (pos.equals(center) && level.random.nextFloat() < ASH_CHANCE) {
                Block ash = IafBlockRegistry.ASH.get();
                if (ash != null) {
                    level.setBlock(pos, ash.defaultBlockState(), 3);
                }
            } else {
                level.setBlock(pos, target.defaultBlockState(), 3);
            }
        }

        charge.discard();
        event.setResult(Event.Result.DENY);
    }
}