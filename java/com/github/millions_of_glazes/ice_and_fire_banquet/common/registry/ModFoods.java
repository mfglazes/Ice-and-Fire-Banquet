package com.github.millions_of_glazes.ice_and_fire_banquet.common.registry;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import org.jetbrains.annotations.NotNull;

public class ModFoods {

    public static final FoodProperties GHOST_POPSICLE =
            (new FoodProperties.Builder()).nutrition(3).saturationMod(0.2F)
                    .effect(new MobEffectInstance(ModEffects.GHOST.get(), 3600, 0), 1.0F)
                    .effect(new MobEffectInstance(MobEffects.NIGHT_VISION, 3600, 0), 1.0F)
                    .effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 9), 1.0F)
                    .fast().alwaysEat().build();

    public static final FoodProperties GHOST_GUMMY =
            GUMMY(
                    new MobEffectInstance(ModEffects.GHOST.get(), 900, 0),
                    new MobEffectInstance(MobEffects.NIGHT_VISION, 900, 0),
                    new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 900, 9)
            );

    public static final FoodProperties DRAGON_HEAD_BANQUET =
            (new FoodProperties.Builder()).nutrition(12).saturationMod(0.6F)
                    .effect(new MobEffectInstance(MobEffects.HEAL, 1, 8), 1.0F)
                    .effect(new MobEffectInstance(MobEffects.NIGHT_VISION, 6000, 0), 1.0F)
                    .fast().alwaysEat().build();

    public static final FoodProperties DRAGON_BLOOD_CURRY =
            (new FoodProperties.Builder()).nutrition(6).saturationMod(0.5F)
                    .effect(new MobEffectInstance(vectorwing.farmersdelight.common.registry.ModEffects.COMFORT.get(), 1200, 0), 1.0F)
                    .effect(new MobEffectInstance(MobEffects.LUCK, 1800, 2), 1.0F)
                    .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 1), 1.0F)
                    .fast().alwaysEat().build();


    private static FoodProperties.Builder GUMMY() {
        return new FoodProperties.Builder().alwaysEat().nutrition(2).saturationMod(0.0F);
    }

    private static FoodProperties GUMMY(@NotNull MobEffectInstance... effects) {
        FoodProperties.Builder builder = GUMMY();
        for (MobEffectInstance effect : effects) {
            builder.effect(effect, 1.0F);
        }
        return builder.build();
    }

    private static FoodProperties GUMMY(@NotNull MobEffectInstance effect) {
        return GUMMY(new MobEffectInstance[]{effect});
    }
}
