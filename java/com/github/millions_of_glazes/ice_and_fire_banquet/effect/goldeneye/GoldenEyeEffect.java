package com.github.millions_of_glazes.ice_and_fire_banquet.effect.goldeneye;

import com.github.millions_of_glazes.ice_and_fire_banquet.capability.DragonEyeDataProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class GoldenEyeEffect extends MobEffect {
    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("a6f0e5d4-1b2c-3d4e-5f6a-7b8c9d0e1f2a");
    private static final UUID DAMAGE_MODIFIER_UUID = UUID.fromString("b7f1e6d5-2c3d-4e5f-6a7b-8c9d0e1f2a3b");

    public GoldenEyeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFD700);
        addAttributeModifier(Attributes.MAX_HEALTH, HEALTH_MODIFIER_UUID.toString(), 0, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, DAMAGE_MODIFIER_UUID.toString(), 0, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        int level = amplifier + 1;
        if (modifier.getId().equals(HEALTH_MODIFIER_UUID)) return level * 40.0;
        if (modifier.getId().equals(DAMAGE_MODIFIER_UUID)) return level * 4.0;
        return super.getAttributeModifierValue(amplifier, modifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap map, int amplifier) {
        super.removeAttributeModifiers(entity, map, amplifier);
        if (entity instanceof Player player && !player.level().isClientSide && !player.hasEffect(this)) {
            player.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).ifPresent(data -> {
                if (data.isMorphActive()) data.endMorph((ServerPlayer) player);
            });
        }
    }
}
