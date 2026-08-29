package com.github.millions_of_glazes.ice_and_fire_banquet.event.Coaster;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
public class CoasterIceDragonBloodSnowClientHandler {
    private static Item fireDragonBlood = null;

    // 状态映射：槽位索引 -> 总物品数量 -> 偏移 {x, z}
    private static final Map<Integer, Map<Integer, double[]>> STATE_OFFSETS = new HashMap<>();

    static {
        // 槽位 0
        Map<Integer, double[]> slot0 = new HashMap<>();
        slot0.put(1, new double[]{0, 0});           // 总物品数=1：只有一瓶火龙血
        slot0.put(2, new double[]{-0.2, -0.2});     // 总物品数=2
        slot0.put(3, new double[]{0, 0.25});        // 总物品数=3（实例：火龙血+其他+火龙血）
        slot0.put(4, new double[]{0.2, 0.2});       // 总物品数=4
        STATE_OFFSETS.put(0, slot0);

        // 槽位 1
        Map<Integer, double[]> slot1 = new HashMap<>();
        slot1.put(2, new double[]{0.2, 0.2});       // 总物品数=2
        slot1.put(3, new double[]{-0.25, -0.15});   // 总物品数=3
        slot1.put(4, new double[]{-0.275, 0.18});   // 总物品数=4
        STATE_OFFSETS.put(1, slot1);

        // 槽位 2
        Map<Integer, double[]> slot2 = new HashMap<>();
        slot2.put(3, new double[]{0.25, -0.3});     // 总物品数=3
        slot2.put(4, new double[]{0.25, -0.2});     // 总物品数=4
        STATE_OFFSETS.put(2, slot2);

        // 槽位 3
        Map<Integer, double[]> slot3 = new HashMap<>();
        slot3.put(4, new double[]{-0.2, -0.25});    // 总物品数=4
        STATE_OFFSETS.put(3, slot3);
    }

    // 默认槽位基础偏移（用于未定义的状态或火龙血数量 > 1 时的回退）
    private static final double[][] DEFAULT_SLOT_OFFSETS = {
            { 0.3,  0.3 },
            {-0.3,  0.3 },
            {-0.3, -0.3 },
            { 0.3, -0.3 }
    };

    // 粒子参数（可调整）
    private static final int TICK_INTERVAL = 3;          // 每多少 tick 检查一次
    private static final int SCAN_RANGE = 10;            // 扫描半径（方块）
    private static final double MIN_Y = 0.05;               // 粒子最低生成高度（相对方块底部）
    private static final double MAX_Y = 0.25;            // 粒子最高生成高度
    private static final double HORIZONTAL_RANDOM = 0.1; // 水平随机偏移范围
    private static final double VY_BASE = 0.04;          // 基础上升速度
    private static final double VY_RANDOM = 0;           // 上升速度随机增量
    private static final int PARTICLES_PER_SLOT = 2;     // 每个槽位每次生成的粒子数量
    private static final float SPAWN_CHANCE = 0.3F;     // 每个槽位生成粒子的概率（0~1）

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (mc.player.tickCount % TICK_INTERVAL != 0) return;

        if (fireDragonBlood == null) {
            fireDragonBlood = ForgeRegistries.ITEMS.getValue(
                    new ResourceLocation("iceandfire", "ice_dragon_blood")
            );
            if (fireDragonBlood == null) return;
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

            // 统计总物品数、火龙血数量、以及火龙血槽位索引（可选）
            int totalItems = 0;
            int flameCount = 0;
            for (int i = 0; i < coaster.getItems().size(); i++) {
                ItemStack stack = coaster.getItems().get(i);
                if (stack.isEmpty()) continue;
                totalItems++;
                if (stack.is(fireDragonBlood)) {
                    flameCount++;
                }
            }

            if (flameCount == 0) continue;

            // 遍历所有槽位，只处理火龙血槽位
            for (int i = 0; i < coaster.getItems().size(); i++) {
                ItemStack stack = coaster.getItems().get(i);
                if (stack.isEmpty() || !stack.is(fireDragonBlood)) continue;

                // 根据槽位索引和总物品数获取偏移
                double[] offset;
                Map<Integer, double[]> slotMap = STATE_OFFSETS.get(i);
                if (slotMap != null && slotMap.containsKey(totalItems)) {
                    offset = slotMap.get(totalItems);
                } else {
                    offset = DEFAULT_SLOT_OFFSETS[i];
                }

                // 概率控制
                if (mc.level.random.nextFloat() > SPAWN_CHANCE) continue;

                // 旋转偏移（根据方块旋转角度）
                int rotation = state.getValue(CoasterBlock.ROTATION);
                double angle = rotation * Math.PI * 2.0 / 16.0;
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                double rotatedX = offset[0] * cos - offset[1] * sin;
                double rotatedZ = offset[0] * sin + offset[1] * cos;

                // 粒子生成位置
                double x = pos.getX() + 0.5 + rotatedX + (mc.level.random.nextDouble() - 0.5) * HORIZONTAL_RANDOM;
                double z = pos.getZ() + 0.5 + rotatedZ + (mc.level.random.nextDouble() - 0.5) * HORIZONTAL_RANDOM;
                double y = pos.getY() + MIN_Y + mc.level.random.nextDouble() * (MAX_Y - MIN_Y);

                // 速度向量：向上为主，轻微水平飘动
                double vx = (mc.level.random.nextDouble() - 0.5) * 0.01;
                double vy = VY_BASE + mc.level.random.nextDouble() * VY_RANDOM;
                double vz = (mc.level.random.nextDouble() - 0.5) * 0.02;

                // 生成粒子
                for (int p = 0; p < PARTICLES_PER_SLOT; p++) {
                    mc.level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, vx, vy, vz);
                }
            }
        }
    }
}
