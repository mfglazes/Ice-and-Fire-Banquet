package com.github.millions_of_glazes.ice_and_fire_banquet.event.Coaster;

import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import umpaz.brewinandchewin.common.block.CoasterBlock;
import umpaz.brewinandchewin.common.block.entity.CoasterBlockEntity;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "ice_and_fire_banquet", value = Dist.CLIENT)
public class CoasterLightningDragonBloodLightningClientHandler {
    private static Item lightningDragonBlood = null;

    // 状态映射：槽位索引 -> 总物品数量 -> 偏移 {x, z}
    private static final Map<Integer, Map<Integer, double[]>> STATE_OFFSETS = new HashMap<>();

    static {
        // 槽位 0
        Map<Integer, double[]> slot0 = new HashMap<>();
        slot0.put(1, new double[]{0, 0});
        slot0.put(2, new double[]{-0.2, -0.2});
        slot0.put(3, new double[]{0, 0.25});
        slot0.put(4, new double[]{0.2, 0.2});
        STATE_OFFSETS.put(0, slot0);

        // 槽位 1
        Map<Integer, double[]> slot1 = new HashMap<>();
        slot1.put(2, new double[]{0.2, 0.2});
        slot1.put(3, new double[]{-0.25, -0.15});
        slot1.put(4, new double[]{-0.275, 0.18});
        STATE_OFFSETS.put(1, slot1);

        // 槽位 2
        Map<Integer, double[]> slot2 = new HashMap<>();
        slot2.put(3, new double[]{0.25, -0.3});
        slot2.put(4, new double[]{0.25, -0.2});
        STATE_OFFSETS.put(2, slot2);

        // 槽位 3
        Map<Integer, double[]> slot3 = new HashMap<>();
        slot3.put(4, new double[]{-0.2, -0.25});
        STATE_OFFSETS.put(3, slot3);
    }

    // 默认槽位基础偏移（用于未定义的状态）
    private static final double[][] DEFAULT_SLOT_OFFSETS = {
            { 0.3,  0.3 },
            {-0.3,  0.3 },
            {-0.3, -0.3 },
            { 0.3, -0.3 }
    };

    // ------ 粒子参数（针对电龙血，密度接近火焰，速度更快） ------
    private static final int TICK_INTERVAL = 1;           // 每 tick 都检查（原 3，提高频率）
    private static final int SCAN_RANGE = 10;             // 扫描半径（方块）
    private static final double HORIZONTAL_RADIUS = 0.125; // 水平半径 8像素（0.5格直径的一半）
    private static final double VERTICAL_HALF = 0.2;   // 垂直半轴 10像素（5/16 格）
    private static final int PARTICLES_PER_SLOT = 3;      // 每个槽位每次生成 3 个粒子
    private static final float SPAWN_CHANCE = 0.3F;       // 概率生成
    private static final double SPEED_BASE = 0.15;        // 基础速度（比火焰快，火焰约0.02~0.04）
    private static final double SPEED_RANDOM = 0.05;      // 速度随机增量

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (mc.player.tickCount % TICK_INTERVAL != 0) return;

        // 获取电龙血物品（使用 iceandfire 模组的物品）
        if (lightningDragonBlood == null) {
            lightningDragonBlood = ForgeRegistries.ITEMS.getValue(
                    new ResourceLocation("iceandfire", "lightning_dragon_blood")
            );
            if (lightningDragonBlood == null) return;
        }

        BlockPos playerPos = mc.player.blockPosition();

        // 扫描玩家周围区域
        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-SCAN_RANGE, -SCAN_RANGE, -SCAN_RANGE),
                playerPos.offset(SCAN_RANGE, SCAN_RANGE, SCAN_RANGE))) {

            BlockState state = mc.level.getBlockState(pos);
            if (!(state.getBlock() instanceof CoasterBlock)) continue;

            BlockEntity blockEntity = mc.level.getBlockEntity(pos);
            if (!(blockEntity instanceof CoasterBlockEntity coaster)) continue;

            // 统计总物品数、电龙血数量
            int totalItems = 0;
            int bloodCount = 0;
            for (int i = 0; i < coaster.getItems().size(); i++) {
                ItemStack stack = coaster.getItems().get(i);
                if (stack.isEmpty()) continue;
                totalItems++;
                if (stack.is(lightningDragonBlood)) {
                    bloodCount++;
                }
            }

            if (bloodCount == 0) continue;

            // 获取方块旋转角度
            int rotation = state.getValue(CoasterBlock.ROTATION);
            double angle = rotation * Math.PI * 2.0 / 16.0;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            // 遍历所有槽位，只处理电龙血槽位
            for (int slot = 0; slot < coaster.getItems().size(); slot++) {
                ItemStack stack = coaster.getItems().get(slot);
                if (stack.isEmpty() || !stack.is(lightningDragonBlood)) continue;

                // 根据槽位和总物品数获取偏移
                double[] offset;
                Map<Integer, double[]> slotMap = STATE_OFFSETS.get(slot);
                if (slotMap != null && slotMap.containsKey(totalItems)) {
                    offset = slotMap.get(totalItems);
                } else {
                    offset = DEFAULT_SLOT_OFFSETS[slot];
                }

                // 旋转偏移
                double rotatedX = offset[0] * cos - offset[1] * sin;
                double rotatedZ = offset[0] * sin + offset[1] * cos;

                // 中心点（物品位置）
                double centerX = pos.getX() + 0.5 + rotatedX;
                double centerZ = pos.getZ() + 0.5 + rotatedZ;
                double centerY = pos.getY() + 0.1; // 方块中心高度

                // 每个槽位生成多个粒子
                for (int p = 0; p < PARTICLES_PER_SLOT; p++) {
                    // 概率控制（此处为100%，若需降低可调整）
                    if (mc.level.random.nextFloat() > SPAWN_CHANCE) continue;

                    // --- 在椭球体内均匀采样位置（拒绝采样） ---
                    double u, v, w;
                    do {
                        u = mc.level.random.nextDouble() * 2 - 1;
                        v = mc.level.random.nextDouble() * 2 - 1;
                        w = mc.level.random.nextDouble() * 2 - 1;
                    } while (u * u + v * v + w * w > 1.0);

                    double px = centerX + u * HORIZONTAL_RADIUS;
                    double py = centerY + w * VERTICAL_HALF;
                    double pz = centerZ + v * HORIZONTAL_RADIUS;

                    // --- 生成随机方向速度（向所有方向扩散） ---
                    double theta = mc.level.random.nextDouble() * 2 * Math.PI;
                    double phi = Math.acos(2 * mc.level.random.nextDouble() - 1);
                    double speed = SPEED_BASE + mc.level.random.nextDouble() * SPEED_RANDOM;
                    double vx = speed * Math.sin(phi) * Math.cos(theta);
                    double vy = speed * Math.sin(phi) * Math.sin(theta);
                    double vz = speed * Math.cos(phi);

                    // 添加粒子
                    mc.level.addParticle(ModParticleTypes.LIGHTNING.get(), px, py, pz, vx, vy, vz);
                }
            }
        }
    }
}

