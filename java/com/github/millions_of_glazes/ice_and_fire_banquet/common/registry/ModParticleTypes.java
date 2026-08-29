package com.github.millions_of_glazes.ice_and_fire_banquet.common.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;

public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Ice_and_Fire_banquet.MOD_ID);

    public static final RegistryObject<SimpleParticleType> DRAGON_FIRE_FLAME =
            PARTICLES.register("dragon_fire_flame", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> DRAGON_ICE_FLAME =
            PARTICLES.register("dragon_ice_flame", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> DRAGON_LIGHTNING_FLAME =
            PARTICLES.register("dragon_lightning_flame", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> LIGHTNING =
            PARTICLES.register("lightning", () -> new SimpleParticleType(false));
}