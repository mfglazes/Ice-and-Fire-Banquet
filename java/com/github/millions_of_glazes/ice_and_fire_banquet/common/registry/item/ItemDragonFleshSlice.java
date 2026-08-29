package com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.item;

import com.github.alexthe666.iceandfire.item.ItemGenericFood;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemDragonFleshSlice extends ItemGenericFood {
    int dragonType;

    public ItemDragonFleshSlice(int dragonType) {
        super(4, 0.4F, true, false, false);
        this.dragonType = dragonType;
    }

    static String getNameForType(int dragonType) {
        String var10000;
        switch (dragonType) {
            case 0 -> var10000 = "fire_dragon_flesh_slice";
            case 1 -> var10000 = "ice_dragon_flesh_slice";
            case 2 -> var10000 = "lightning_dragon_flesh_slice";
            default -> var10000 = "fire_dragon_flesh_slice";
        }

        return var10000;
    }

    public void onFoodEaten(ItemStack stack, Level worldIn, LivingEntity livingEntity) {
        if (!worldIn.isClientSide) {
            if (this.dragonType == 0) {
                livingEntity.setSecondsOnFire(3);
            } else if (this.dragonType == 1) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 1));
            } else if (!livingEntity.level().isClientSide) {
                LightningBolt lightningboltentity = (LightningBolt)EntityType.LIGHTNING_BOLT.create(livingEntity.level());
                lightningboltentity.moveTo(livingEntity.position());
                if (!livingEntity.level().isClientSide) {
                    livingEntity.level().addFreshEntity(lightningboltentity);
                }
            }
        }

    }
}
