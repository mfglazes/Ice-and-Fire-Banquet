package com.github.millions_of_glazes.ice_and_fire_banquet.mixin;

import com.github.alexthe666.iceandfire.client.model.ModelTroll;
import com.github.alexthe666.iceandfire.client.render.tile.RenderPodium;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import com.github.millions_of_glazes.ice_and_fire_banquet.api.IPodiumFacing; // 需要自行创建该接口
import com.github.millions_of_glazes.ice_and_fire_banquet.config.Config;
import com.github.millions_of_glazes.ice_and_fire_banquet.event.PodiumRenderer.PodiumRenderEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RenderPodium.class)
public class RenderPodiumMixin {

    // ---------- 物品实例 ----------
    private static final Item FIRE_DRAGON_BLOOD_ITEM =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("iceandfire", "fire_dragon_blood"));
    private static final Item ICE_DRAGON_BLOOD_ITEM =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("iceandfire", "ice_dragon_blood"));
    private static final Item LIGHTNING_DRAGON_BLOOD_ITEM =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("iceandfire", "lightning_dragon_blood"));
    private static final Item TROLL_SKULL_ITEM =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("iceandfire", "troll_skull"));
    private static final Item CANNOLI_ITEM =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("iceandfire", "cannoli"));
    private static final Item GHOST_GUMMY_ITEM =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("ice_and_fire_banquet", "ghost_gummy"));
    private static final Item TANKARD_ITEM =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("brewinandchewin", "tankard"));
    private static final Item BEER_ITEM =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("brewinandchewin", "beer"));
    private static final Item VODKA_ITEM =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("brewinandchewin", "vodka"));
    private static final Item APPLE_GUMMY_ITEM =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("collectorsreap", "apple_gummy"));

    // ---------- 模型/纹理位置 ----------
    private static final ResourceLocation TROLL_SKULL_TEXTURE =
            new ResourceLocation("iceandfire", "textures/models/skulls/skull_troll.png");
    private static final ModelTroll TROLL_MODEL = new ModelTroll();

    private static final ResourceLocation FIRE_DRAGON_BLOOD_MODEL =
            new ResourceLocation("ice_and_fire_banquet", "block/coaster/fire_dragon_blood");
    private static final ResourceLocation ICE_DRAGON_BLOOD_MODEL =
            new ResourceLocation("ice_and_fire_banquet", "block/coaster/ice_dragon_blood");
    private static final ResourceLocation LIGHTNING_DRAGON_BLOOD_MODEL =
            new ResourceLocation("ice_and_fire_banquet", "block/coaster/lightning_dragon_blood");
    private static final ResourceLocation CANNOLI_MODEL =
            new ResourceLocation("ice_and_fire_banquet", "block/coaster/cannoli");
    private static final ResourceLocation GHOST_GUMMY_MODEL =
            new ResourceLocation("ice_and_fire_banquet", "block/coaster/ghost_gummy");
    private static final ResourceLocation TANKARD_MODEL =
            new ResourceLocation("brewinandchewin", "block/coaster_tankard");
    private static final ResourceLocation BEER_MODEL =
            new ResourceLocation("brewinandchewin", "block/coaster_beer");
    private static final ResourceLocation VODKA_MODEL =
            new ResourceLocation("brewinandchewin", "block/coaster_vodka");
    private static final ResourceLocation APPLE_GUMMY_MODEL =
            new ResourceLocation("collectorsreap", "block/coaster/apple_gummy");

    // ---------- 通用渲染参数（用于普通方块模型） ----------
    private static final double TRANSLATE_X = 0.0;
    private static final double TRANSLATE_Y = 1.4375;
    private static final double TRANSLATE_Z = 0.0;
    private static final float SCALE_X = 1.0F;
    private static final float SCALE_Y = 1.0F;
    private static final float SCALE_Z = 1.0F;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRenderPodium(TileEntityPodium entity, float partialTicks, PoseStack matrixStackIn,
                                MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn,
                                CallbackInfo ci) {
        // 总开关：如果关闭，直接返回，使用原版渲染
        if (!Config.enablePodiumModels) {
            return;
        }

        if (entity == null || entity.getContainerSize() == 0) return;

        ItemStack stack = entity.getItem(0);
        if (stack.isEmpty()) return;

        // 火龙血
        if (stack.is(FIRE_DRAGON_BLOOD_ITEM)) {
            PodiumRenderEvent event = new PodiumRenderEvent(
                    (RenderPodium<?>) (Object) this, entity, partialTicks, combinedLightIn, combinedOverlayIn);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                ci.cancel();
                return;
            }
            renderModel(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn,
                    FIRE_DRAGON_BLOOD_MODEL, TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z,
                    SCALE_X, SCALE_Y, SCALE_Z);
            ci.cancel();
        }
        // 冰龙血
        else if (stack.is(ICE_DRAGON_BLOOD_ITEM)) {
            renderModel(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn,
                    ICE_DRAGON_BLOOD_MODEL, TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z,
                    SCALE_X, SCALE_Y, SCALE_Z);
            ci.cancel();
        }
        // 电龙血
        else if (stack.is(LIGHTNING_DRAGON_BLOOD_ITEM)) {
            renderModel(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn,
                    LIGHTNING_DRAGON_BLOOD_MODEL, TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z,
                    SCALE_X, SCALE_Y, SCALE_Z);
            ci.cancel();
        }
        // 奶油甜馅煎饼卷
        else if (stack.is(CANNOLI_ITEM)) {
            renderModel(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn,
                    CANNOLI_MODEL, TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z,
                    SCALE_X, SCALE_Y, SCALE_Z);
            ci.cancel();
        }
        // 啤酒
        else if (stack.is(BEER_ITEM)) {
            renderModel(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn,
                    BEER_MODEL, TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z,
                    SCALE_X, SCALE_Y, SCALE_Z);
            ci.cancel();
        }
        // 伏特加
        else if (stack.is(VODKA_ITEM)) {
            renderModel(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn,
                    VODKA_MODEL, TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z,
                    SCALE_X, SCALE_Y, SCALE_Z);
            ci.cancel();
        }
        // 酒杯
        else if (stack.is(TANKARD_ITEM)) {
            renderModel(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn,
                    TANKARD_MODEL, TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z,
                    SCALE_X, SCALE_Y, SCALE_Z);
            ci.cancel();
        }
        // 幽灵软糖
        else if (stack.is(GHOST_GUMMY_ITEM)) {
            renderModel(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn,
                    GHOST_GUMMY_MODEL, TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z,
                    SCALE_X, SCALE_Y, SCALE_Z);
            ci.cancel();
        }
        // 苹果软糖
        else if (stack.is(APPLE_GUMMY_ITEM)) {
            renderModel(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn,
                    APPLE_GUMMY_MODEL, TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z,
                    SCALE_X, SCALE_Y, SCALE_Z);
            ci.cancel();
        }
        // 食人妖颅骨
        else if (stack.is(TROLL_SKULL_ITEM)) {
            int facing = ((IPodiumFacing) entity).getPodiumFacing();
            renderTrollSkull(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, facing);
            ci.cancel();
        }
    }

    /**
     * 通用渲染方法：根据模型位置和平移缩放参数渲染 BakedModel
     */
    private void renderModel(PoseStack poseStack, MultiBufferSource buffer, int light, int overlay,
                             ResourceLocation modelLocation,
                             double translateX, double translateY, double translateZ,
                             float scaleX, float scaleY, float scaleZ) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.getModelManager() == null) return;

        BakedModel model = mc.getModelManager().getModel(modelLocation);
        if (model == mc.getModelManager().getMissingModel()) {
            System.out.println("Model missing: " + modelLocation);
            return;
        }

        BlockState dummyState = Blocks.AIR.defaultBlockState();
        RandomSource randomSource = mc.level.random;
        ModelData modelData = ModelData.EMPTY;
        RenderType renderType = RenderType.cutout();

        VertexConsumer consumer = buffer.getBuffer(renderType);
        List<BakedQuad> quads = model.getQuads(dummyState, null, randomSource, modelData, renderType);

        poseStack.pushPose();
        poseStack.translate(translateX, translateY, translateZ);
        poseStack.scale(scaleX, scaleY, scaleZ);

        for (BakedQuad quad : quads) {
            consumer.putBulkData(poseStack.last(), quad, 0.95F, 0.95F, 0.95F, light, overlay);
        }

        poseStack.popPose();
    }

    /**
     * 渲染食人妖颅骨实体模型（仅头部），根据朝向索引进行微调
     */
    private void renderTrollSkull(PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, int facing) {
        RenderType renderType = RenderType.entityTranslucent(TROLL_SKULL_TEXTURE);
        VertexConsumer consumer = buffer.getBuffer(renderType);

        poseStack.pushPose();

        // 基础位置
        poseStack.translate(0, 1.63, 0);
        poseStack.scale(1.0F, 1.0F, 1.0F);

        // 根据朝向进行世界坐标系微调（可选，数值可根据实际效果调整）
        switch (facing) {
            case 0 -> poseStack.translate(0.5, 0.0, 1.0);      // 南
            case 1 -> poseStack.translate(0.0, 0.0, 0.5);     // 东
            case 2 -> poseStack.translate(0.5, 0.0, 0.0);    // 北
            case 3 -> poseStack.translate(1.0, 0.0, 0.5);    // 西
            default -> {}
        }

        // 计算水平旋转角度（0=南，90=西，180=北，270=东）
        float yaw = switch (facing) {
            case 0 -> 0.0F;
            case 1 -> 270.0F;
            case 2 -> 180.0F;
            case 3 -> 90.0F;
            default -> 0.0F;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        // 原版局部偏移（模拟 IA 的 TROLL 分支）
        poseStack.translate(0.0, 1.0, -0.35);

        // 头部俯仰调整
        TROLL_MODEL.resetToDefaultPose();
        TROLL_MODEL.head.rotateAngleX = (float) Math.toRadians(-20);
        TROLL_MODEL.head.render(poseStack, consumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}