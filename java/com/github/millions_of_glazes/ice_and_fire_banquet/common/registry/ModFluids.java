package com.github.millions_of_glazes.ice_and_fire_banquet.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.common.fluid.AlcoholFluidType;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, "ice_and_fire_banquet");
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, "ice_and_fire_banquet");

    public static final RegistryObject<FluidType> FIRE_DRAGON_BLOOD_TYPE;
    public static final RegistryObject<Fluid> FIRE_DRAGON_BLOOD;
    public static final RegistryObject<Fluid> FLOWING_FIRE_DRAGON_BLOOD;
    public static ForgeFlowingFluid.Properties FIRE_DRAGON_BLOOD_PROPERTIES;

    public static final RegistryObject<FluidType> ICE_DRAGON_BLOOD_TYPE;
    public static final RegistryObject<Fluid> ICE_DRAGON_BLOOD;
    public static final RegistryObject<Fluid> FLOWING_ICE_DRAGON_BLOOD;
    public static ForgeFlowingFluid.Properties ICE_DRAGON_BLOOD_PROPERTIES;

    public static final RegistryObject<FluidType> LIGHTNING_DRAGON_BLOOD_TYPE;
    public static final RegistryObject<Fluid> LIGHTNING_DRAGON_BLOOD;
    public static final RegistryObject<Fluid> FLOWING_LIGHTNING_DRAGON_BLOOD;
    public static ForgeFlowingFluid.Properties LIGHTNING_DRAGON_BLOOD_PROPERTIES;

    public ModFluids() {
    }

    static {
        FIRE_DRAGON_BLOOD_TYPE = FLUID_TYPES.register("fire_dragon_blood_type", () -> new AlcoholFluidType(0xFFFB0001));
        FIRE_DRAGON_BLOOD = FLUIDS.register("fire_dragon_blood", () -> new ForgeFlowingFluid.Source(FIRE_DRAGON_BLOOD_PROPERTIES));
        FLOWING_FIRE_DRAGON_BLOOD = FLUIDS.register("flowing_fire_dragon_blood", () -> new ForgeFlowingFluid.Flowing(FIRE_DRAGON_BLOOD_PROPERTIES));
        FIRE_DRAGON_BLOOD_PROPERTIES = new ForgeFlowingFluid.Properties(FIRE_DRAGON_BLOOD_TYPE, FIRE_DRAGON_BLOOD, FLOWING_FIRE_DRAGON_BLOOD).bucket(() -> ForgeRegistries.ITEMS.getValue(new ResourceLocation("iceandfire", "fire_dragon_blood"))).block(null);

        ICE_DRAGON_BLOOD_TYPE = FLUID_TYPES.register("ice_dragon_blood_type", () -> new AlcoholFluidType(0xFF07A4F8));
        ICE_DRAGON_BLOOD = FLUIDS.register("ice_dragon_blood", () -> new ForgeFlowingFluid.Source(ICE_DRAGON_BLOOD_PROPERTIES));
        FLOWING_ICE_DRAGON_BLOOD = FLUIDS.register("flowing_ice_dragon_blood", () -> new ForgeFlowingFluid.Flowing(ICE_DRAGON_BLOOD_PROPERTIES));
        ICE_DRAGON_BLOOD_PROPERTIES = new ForgeFlowingFluid.Properties(ICE_DRAGON_BLOOD_TYPE, ICE_DRAGON_BLOOD, FLOWING_ICE_DRAGON_BLOOD).bucket(() -> ForgeRegistries.ITEMS.getValue(new ResourceLocation("iceandfire", "ice_dragon_blood"))).block(null);

        LIGHTNING_DRAGON_BLOOD_TYPE = FLUID_TYPES.register("lightning_dragon_blood_type", () -> new AlcoholFluidType(0xFF7100FC));
        LIGHTNING_DRAGON_BLOOD = FLUIDS.register("lightning_dragon_blood", () -> new ForgeFlowingFluid.Source(LIGHTNING_DRAGON_BLOOD_PROPERTIES));
        FLOWING_LIGHTNING_DRAGON_BLOOD = FLUIDS.register("flowing_lightning_dragon_blood", () -> new ForgeFlowingFluid.Flowing(LIGHTNING_DRAGON_BLOOD_PROPERTIES));
        LIGHTNING_DRAGON_BLOOD_PROPERTIES = new ForgeFlowingFluid.Properties(LIGHTNING_DRAGON_BLOOD_TYPE, LIGHTNING_DRAGON_BLOOD, FLOWING_LIGHTNING_DRAGON_BLOOD).bucket(() -> ForgeRegistries.ITEMS.getValue(new ResourceLocation("iceandfire", "lightning_dragon_blood"))).block(null);
    }
}
