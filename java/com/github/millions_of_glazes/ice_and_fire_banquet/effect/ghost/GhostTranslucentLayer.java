package com.github.millions_of_glazes.ice_and_fire_banquet.effect.ghost;

import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class GhostTranslucentLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public GhostTranslucentLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (player.hasEffect(ModEffects.GHOST.get())) {
            RenderType renderType = RenderType.entityTranslucent(player.getSkinTextureLocation());
            VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
            // 直接绘制淡绿色半透明模型
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight,
                    OverlayTexture.NO_OVERLAY, 0.6F, 3.0F, 0.6F, 0.75F);
        }
    }
}
