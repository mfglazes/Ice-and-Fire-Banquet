package com.github.millions_of_glazes.ice_and_fire_banquet.event.Podium;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModParticleTypes;
import com.github.millions_of_glazes.ice_and_fire_banquet.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = "ice_and_fire_banquet", value = Dist.CLIENT)
public class PodiumLightningDragonBloodLightningClientHandler {

    private static final ResourceLocation LIGHTNING_DRAGON_BLOOD_ID =
            new ResourceLocation("iceandfire", "lightning_dragon_blood");
    private static Item lightningDragonBlood = null;

    // 可调参数
    private static final int TICK_INTERVAL = 1;            // 每 tick 检查一次（提高粒子响应速度）
    private static final int SCAN_RANGE = 10;              // 扫描半径（方块）
    private static final double PARTICLE_Y = 1.5;         // 粒子生成高度（瓶口位置，可微调）
    private static final double HORIZONTAL_RANDOM = 0.08;  // 水平随机偏移
    private static final int PARTICLES_PER_TICK = 2;       // 每次生成几个粒子
    private static final float SPAWN_CHANCE = 0.8F;        // 生成概率（0~1）
    private static final double SPEED_BASE = 0.15;         // 基础速度（比火焰快）
    private static final double SPEED_RANDOM = 0.05;       // 速度随机增量

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        // ===== 总开关检查：如果关闭，不生成粒子 =====
        if (!Config.enablePodiumModels) return;
        // ==========================================

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (mc.player.tickCount % TICK_INTERVAL != 0) return;

        // 惰性加载电龙血物品
        if (lightningDragonBlood == null) {
            lightningDragonBlood = ForgeRegistries.ITEMS.getValue(LIGHTNING_DRAGON_BLOOD_ID);
            if (lightningDragonBlood == null) return;
        }

        BlockPos playerPos = mc.player.blockPosition();

        // 扫描周围 Podium
        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-SCAN_RANGE, -SCAN_RANGE, -SCAN_RANGE),
                playerPos.offset(SCAN_RANGE, SCAN_RANGE, SCAN_RANGE))) {

            BlockEntity blockEntity = mc.level.getBlockEntity(pos);
            if (!(blockEntity instanceof TileEntityPodium podium)) continue;

            ItemStack stack = podium.getItem(0);
            if (stack.isEmpty() || !stack.is(lightningDragonBlood)) continue;

            // 生成粒子
            for (int i = 0; i < PARTICLES_PER_TICK; i++) {
                if (mc.level.random.nextFloat() > SPAWN_CHANCE) continue;

                // 粒子位置：中心 + 水平随机偏移
                double x = pos.getX() + 0.5 + (mc.level.random.nextDouble() - 0.5) * HORIZONTAL_RANDOM;
                double z = pos.getZ() + 0.5 + (mc.level.random.nextDouble() - 0.5) * HORIZONTAL_RANDOM;
                double y = pos.getY() + PARTICLE_Y + mc.level.random.nextDouble() * 0.05;

                // 速度：向随机方向扩散，速度较快
                double theta = mc.level.random.nextDouble() * 2 * Math.PI;
                double phi = Math.acos(2 * mc.level.random.nextDouble() - 1);
                double speed = SPEED_BASE + mc.level.random.nextDouble() * SPEED_RANDOM;
                double vx = speed * Math.sin(phi) * Math.cos(theta);
                double vy = speed * Math.sin(phi) * Math.sin(theta);
                double vz = speed * Math.cos(phi);

                mc.level.addParticle(ModParticleTypes.LIGHTNING.get(), x, y, z, vx, vy, vz);
            }
        }
    }
}
