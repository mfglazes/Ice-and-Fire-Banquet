package com.github.millions_of_glazes.ice_and_fire_banquet.event.DragonSteelKnife;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityDragonLightningCharge;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
public class DragonLightningProjectileHandler {

    private static final Map<ResourceLocation, Supplier<Block>> CRACKLED_MAP = new HashMap<>();
    private static volatile boolean mapsInitialized = false;

    private static final float EXPLOSION_POWER = 2.5F;
    private static final boolean EXPLOSION_FIRE = true;
    private static final int CRACKLED_RADIUS = 2;
    private static final float CRACKLED_CHANCE = 0.7F;
    private static final float CRACKLED_STONE_FACTOR = 0.7F;

    private static void initMaps() {
        if (mapsInitialized) return;
        synchronized (CRACKLED_MAP) {
            if (mapsInitialized) return;

            CRACKLED_MAP.put(ResourceLocation.tryParse("grass_block"), IafBlockRegistry.CRACKLED_GRASS);
            CRACKLED_MAP.put(ResourceLocation.tryParse("dirt"), IafBlockRegistry.CRACKLED_DIRT);
            CRACKLED_MAP.put(ResourceLocation.tryParse("dirt_path"), IafBlockRegistry.CRACKLED_DIRT_PATH);
            CRACKLED_MAP.put(ResourceLocation.tryParse("stone"), IafBlockRegistry.CRACKLED_STONE);
            CRACKLED_MAP.put(ResourceLocation.tryParse("cobblestone"), IafBlockRegistry.CRACKLED_COBBLESTONE);
            CRACKLED_MAP.put(ResourceLocation.tryParse("gravel"), IafBlockRegistry.CRACKLED_GRAVEL);

            mapsInitialized = true;
        }
    }

    @SubscribeEvent
    public static void onLightningProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getEntity() instanceof EntityDragonLightningCharge charge)) return;
        if (!(charge.getOwner() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        initMaps();

        HitResult hit = event.getRayTraceResult();
        Level level = player.level();

        float damage = (float) (IafConfig.dragonAttackDamageLightning * 5.0);
        if (hit.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) hit).getEntity();
            if (target != player) {
                target.hurt(IafDamageRegistry.causeDragonLightningDamage(player), damage);
            }
        }

        BlockPos hitPos = BlockPos.containing(charge.getX(), charge.getY(), charge.getZ());
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(hitPos.getX(), hitPos.getY(), hitPos.getZ());
            bolt.setCause(player instanceof ServerPlayer sp ? sp : null);
            bolt.getTags().add("iceandfire:bolt_dont_destroy_loot");
            level.addFreshEntity(bolt);
        }

        level.explode(player, charge.getX(), charge.getY(), charge.getZ(),
                EXPLOSION_POWER, EXPLOSION_FIRE, Level.ExplosionInteraction.BLOCK);

        crackleArea(level, hitPos);

        charge.discard();
        event.setResult(Event.Result.DENY);
    }

    private static void crackleArea(Level level, BlockPos center) {
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-CRACKLED_RADIUS, -CRACKLED_RADIUS, -CRACKLED_RADIUS),
                center.offset(CRACKLED_RADIUS, CRACKLED_RADIUS, CRACKLED_RADIUS))) {

            BlockState state = level.getBlockState(pos);
            if (state.isAir()) continue;

            ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
            if (blockId == null) continue;

            Supplier<Block> targetSupplier = CRACKLED_MAP.get(blockId);
            if (targetSupplier == null) continue;

            Block target = targetSupplier.get();
            if (target == null || state.getBlock() == target) continue;

            float factor = (blockId.getPath().contains("stone") || blockId.getPath().contains("cobblestone"))
                    ? CRACKLED_STONE_FACTOR
                    : 1.0F;

            if (level.random.nextFloat() > CRACKLED_CHANCE * factor) {
                continue;
            }

            level.setBlock(pos, target.defaultBlockState(), 3);
        }
    }
}