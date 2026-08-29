package com.github.millions_of_glazes.ice_and_fire_banquet.common.registry;

import com.github.millions_of_glazes.ice_and_fire_banquet.effect.goldeneye.GoldenEyeEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import com.github.millions_of_glazes.ice_and_fire_banquet.effect.ghost.GhostEffect;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Ice_and_Fire_banquet.MOD_ID);

    public static final RegistryObject<MobEffect> GHOST = EFFECTS.register("ghost", GhostEffect::new);
    public static final RegistryObject<MobEffect> GOLDEN_EYE = EFFECTS.register("golden_eye", GoldenEyeEffect::new);
}
