package com.github.millions_of_glazes.ice_and_fire_banquet.mixin;

import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(net.minecraft.client.renderer.ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void iceAndFireBanquet$preRenderArm(AbstractClientPlayer pPlayer, float pPartialTicks, float pPitch, InteractionHand pHand, float pSwingProgress, ItemStack pStack, float pEquippedProgress, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, CallbackInfo ci) {
        if (!pPlayer.hasEffect(ModEffects.GHOST.get())) return;

        ItemStack heldItem = pPlayer.getItemInHand(pHand);
        if (heldItem.isEmpty()) {
            applyGhostRenderState();
        } else {
            restoreRenderState();
        }
    }

    @Inject(method = "renderArmWithItem", at = @At("RETURN"))
    private void iceAndFireBanquet$postRenderArm(AbstractClientPlayer pPlayer, float pPartialTicks, float pPitch, InteractionHand pHand, float pSwingProgress, ItemStack pStack, float pEquippedProgress, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, CallbackInfo ci) {
        if (pPlayer.hasEffect(ModEffects.GHOST.get())) {
            restoreRenderState();
        }
    }

    private void applyGhostRenderState() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(0.6F, 1.0F, 0.6F, 0.4F);
        RenderSystem.depthMask(false);
    }

    private void restoreRenderState() {
        RenderSystem.depthMask(true);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}