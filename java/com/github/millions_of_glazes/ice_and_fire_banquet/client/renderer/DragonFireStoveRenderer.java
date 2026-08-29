package com.github.millions_of_glazes.ice_and_fire_banquet.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.items.ItemStackHandler;
import com.github.millions_of_glazes.ice_and_fire_banquet.entity.DragonFireStoveBlockEntity;
import vectorwing.farmersdelight.common.block.StoveBlock;

public class DragonFireStoveRenderer implements BlockEntityRenderer<DragonFireStoveBlockEntity> {
    private static final float ITEM_SIZE = 0.375F;

    public DragonFireStoveRenderer(BlockEntityRendererProvider.Context context) {
    }


    @Override
    public void render(DragonFireStoveBlockEntity stove, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Direction direction = stove.getBlockState().getValue(StoveBlock.FACING).getOpposite();
        ItemStackHandler inventory = stove.getInventory();
        int posLong = (int) stove.getBlockPos().asLong();

        for (int i = 0; i < inventory.getSlots(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            poseStack.pushPose();

            // 将物品中心对准炉灶顶面中心
            poseStack.translate(0.5, 1.02, 0.5);

            // 旋转物品使其朝向炉灶正面
            float yRot = -direction.toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(yRot));

            // 将物品平放（原本站立，转90度）
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

            // 根据槽位偏移到具体位置
            Vec2 offset = stove.getStoveItemOffset(i);
            poseStack.translate(offset.x, offset.y, 0.0);

            // 缩小物品尺寸
            poseStack.scale(ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);

            // 计算方块上方光照
            int light = LevelRenderer.getLightColor(stove.getLevel(), stove.getBlockPos().above());

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.FIXED,
                    light,
                    combinedOverlay,
                    poseStack,
                    buffer,
                    stove.getLevel(),
                    posLong + i   // 每个槽位给一个独立ID，避免闪烁
            );

            poseStack.popPose();
        }
    }
}
