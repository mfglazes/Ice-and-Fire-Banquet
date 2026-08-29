package com.github.millions_of_glazes.ice_and_fire_banquet.client;

import com.github.millions_of_glazes.ice_and_fire_banquet.capability.DragonEyeDataProvider;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * 黄金瞳渲染层：直接在玩家头部模型的眼睛位置绘制金色方块，
 * 支持通过配置文件动态设置眼睛的数量、位置、大小和颜色。
 */
@OnlyIn(Dist.CLIENT)
public class EyeRenderHandler extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    // 保存当前要渲染的眼睛列表
    private static List<EyeRenderData> eyes = new ArrayList<>();

    // 默认值（左眼和右眼）
    static {
        eyes.add(new EyeRenderData(-0.0935f, -0.22f, -0.25f, 0.0625f, 255, 215, 0, 255));
        eyes.add(new EyeRenderData(0.0935f, -0.22f, -0.25f, 0.0625f, 255, 215, 0, 255));
    }

    public EyeRenderHandler(LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        // 只有拥有黄金瞳效果且未处于化龙状态时才渲染
        if (!player.hasEffect(ModEffects.GOLDEN_EYE.get())) return;
        boolean morphActive = player.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA)
                .map(data -> data.isMorphActive())
                .orElse(false);
        if (morphActive) return;

        PlayerModel<AbstractClientPlayer> model = this.getParentModel();
        ModelPart head = model.head;

        stack.pushPose();
        head.translateAndRotate(stack);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lightning());

        for (EyeRenderData eye : eyes) {
            renderEyeQuad(stack, vertexConsumer,
                    eye.x, eye.y, eye.z, eye.size,
                    eye.r, eye.g, eye.b, eye.a,
                    packedLight);
        }

        stack.popPose();
    }

    private void renderEyeQuad(PoseStack stack, VertexConsumer builder,
                               float centerX, float centerY, float centerZ,
                               float size,
                               int r, int g, int b, int a,
                               int light) {
        float half = size / 2.0f;
        float minX = centerX - half;
        float maxX = centerX + half;
        float minY = centerY - half;
        float maxY = centerY + half;
        float z = centerZ;

        var pose = stack.last().pose();
        builder.vertex(pose, minX, minY, z).color(r, g, b, a)
                .uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).endVertex();
        builder.vertex(pose, minX, maxY, z).color(r, g, b, a)
                .uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).endVertex();
        builder.vertex(pose, maxX, maxY, z).color(r, g, b, a)
                .uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).endVertex();
        builder.vertex(pose, maxX, minY, z).color(r, g, b, a)
                .uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).endVertex();
    }

    /**
     * 更新要渲染的眼睛列表（由配置文件加载/重载时调用）。
     */
    public static void setEyes(List<EyeRenderData> newEyes) {
        eyes = new ArrayList<>(newEyes);
    }

    /**
     * 存储单个眼睛的渲染参数。
     */
    public static class EyeRenderData {
        public final float x, y, z, size;
        public final int r, g, b, a;

        public EyeRenderData(float x, float y, float z, float size, int r, int g, int b, int a) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.size = size;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }
}
