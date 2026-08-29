package com.github.millions_of_glazes.ice_and_fire_banquet.client.renderer;

import com.github.millions_of_glazes.ice_and_fire_banquet.entity.EntityGhostKnife;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenderGhostKnife extends EntityRenderer<EntityGhostKnife> {

    public RenderGhostKnife(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityGhostKnife entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(EntityGhostKnife entity, float entityYaw, float partialTicks,
                       PoseStack matrixStack, @NotNull MultiBufferSource buffer, int packedLight) {
        ItemStack renderStack = entity.getRenderStack();
        if (renderStack.isEmpty()) return;

        matrixStack.pushPose();
        matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        matrixStack.translate(0.0F, 0.5F, 0.0F);
        matrixStack.scale(2.0F, 2.0F, 2.0F);
        matrixStack.mulPose(Axis.YP.rotationDegrees(0.0F));
        matrixStack.mulPose(Axis.ZN.rotationDegrees(((float) entity.tickCount + partialTicks) * 30.0F));
        matrixStack.translate(0.0F, -0.15F, 0.0F);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                renderStack,
                ItemDisplayContext.GROUND,
                240,
                OverlayTexture.NO_OVERLAY,
                matrixStack,
                buffer,
                entity.level(),
                0
        );
        matrixStack.popPose();
    }
}
