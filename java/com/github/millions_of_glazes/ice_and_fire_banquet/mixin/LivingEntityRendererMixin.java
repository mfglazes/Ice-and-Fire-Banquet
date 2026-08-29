package com.github.millions_of_glazes.ice_and_fire_banquet.mixin;

import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEffects;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void iceAndFireBanquet$forceTranslucent(T entity,
                                                    boolean bodyVisible,
                                                    boolean translucent,
                                                    boolean glowing,
                                                    CallbackInfoReturnable<RenderType> cir) {
        if (entity instanceof AbstractClientPlayer player && player.hasEffect(ModEffects.GHOST.get())) {
            cir.setReturnValue(RenderType.entityTranslucent(player.getSkinTextureLocation()));
        }
    }
}
