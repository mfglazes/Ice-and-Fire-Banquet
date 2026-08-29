package com.github.millions_of_glazes.ice_and_fire_banquet.event.PodiumRenderer;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import com.github.millions_of_glazes.ice_and_fire_banquet.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
public class PodiumFireDragonBloodFlameClientHandler {

    private static final ResourceLocation FIRE_DRAGON_BLOOD_ID = new ResourceLocation("iceandfire", "fire_dragon_blood");
    private static Item fireDragonBlood = null;

    // 可调参数
    private static final int TICK_INTERVAL = 3;          // 每多少 tick 检查一次
    private static final int SCAN_RANGE = 10;            // 扫描半径（方块）
    private static final double PARTICLE_Y_OFFSET = 1.6; // 粒子生成高度（相对方块底部，需要微调）
    private static final double HORIZONTAL_RANDOM = 0.08;// 水平随机偏移
    private static final double VY_BASE = 0.04;          // 基础上升速度
    private static final double VY_RANDOM = 0;        // 上升速度随机增量
    private static final int PARTICLES_PER_TICK = 2;     // 每次生成几个粒子
    private static final float SPAWN_CHANCE = 0.3F;      // 生成概率（0~1）

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        // ===== 总开关检查：如果关闭，不生成粒子 =====
        if (!Config.enablePodiumModels) return;
        // ==========================================

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (mc.player.tickCount % TICK_INTERVAL != 0) return;

        // 惰性加载火龙血物品
        if (fireDragonBlood == null) {
            fireDragonBlood = ForgeRegistries.ITEMS.getValue(FIRE_DRAGON_BLOOD_ID);
            if (fireDragonBlood == null) return;
        }

        BlockPos playerPos = mc.player.blockPosition();

        // 扫描玩家周围区域
        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-SCAN_RANGE, -SCAN_RANGE, -SCAN_RANGE),
                playerPos.offset(SCAN_RANGE, SCAN_RANGE, SCAN_RANGE))) {

            BlockEntity blockEntity = mc.level.getBlockEntity(pos);
            if (!(blockEntity instanceof TileEntityPodium podium)) continue;

            ItemStack stack = podium.getItem(0); // Podium 只有一个槽位
            if (stack.isEmpty() || !stack.is(fireDragonBlood)) continue;

            // 概率控制
            if (mc.level.random.nextFloat() > SPAWN_CHANCE) continue;

            // 粒子位置：方块中心 + 随机水平偏移 + 固定高度
            double x = pos.getX() + 0.5 + (mc.level.random.nextDouble() - 0.5) * HORIZONTAL_RANDOM;
            double z = pos.getZ() + 0.5 + (mc.level.random.nextDouble() - 0.5) * HORIZONTAL_RANDOM;
            double y = pos.getY() + PARTICLE_Y_OFFSET + mc.level.random.nextDouble() * 0.05;

            // 速度：向上为主，轻微水平飘动
            double vx = (mc.level.random.nextDouble() - 0.5) * 0.01;
            double vy = VY_BASE + mc.level.random.nextDouble() * VY_RANDOM;
            double vz = (mc.level.random.nextDouble() - 0.5) * 0.01;

            // 生成自定义火焰粒子
            for (int i = 0; i < PARTICLES_PER_TICK; i++) {
                mc.level.addParticle(ParticleTypes.FLAME, x, y, z, vx, vy, vz);
            }
        }
    }
}
