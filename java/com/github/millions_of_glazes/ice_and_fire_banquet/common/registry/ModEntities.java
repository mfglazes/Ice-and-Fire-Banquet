package com.github.millions_of_glazes.ice_and_fire_banquet.common.registry;

import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.block.ModBlocks;
import com.github.millions_of_glazes.ice_and_fire_banquet.entity.DragonFireStoveBlockEntity;
import com.github.millions_of_glazes.ice_and_fire_banquet.entity.DragonIceStoveBlockEntity;
import com.github.millions_of_glazes.ice_and_fire_banquet.entity.DragonLightningStoveBlockEntity;
import com.github.millions_of_glazes.ice_and_fire_banquet.entity.EntityGhostKnife;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Ice_and_Fire_banquet.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Ice_and_Fire_banquet.MOD_ID);

    public static final RegistryObject<EntityType<EntityGhostKnife>> GHOST_KNIFE =
            ENTITIES.register("ghost_knife", () -> EntityType.Builder.<EntityGhostKnife>of(EntityGhostKnife::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .setCustomClientFactory(EntityGhostKnife::new)
                    .build("ghost_knife"));

    public static final RegistryObject<BlockEntityType<DragonFireStoveBlockEntity>> DRAGON_FIRE_STOVE=
            BLOCK_ENTITIES.register("dragon_fire_stove",
                    () -> BlockEntityType.Builder.of(
                            DragonFireStoveBlockEntity::new,
                            ModBlocks.DRAGON_FIRE_STOVE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<DragonIceStoveBlockEntity>> DRAGON_ICE_STOVE =
            BLOCK_ENTITIES.register("dragon_ice_stove",
                    () -> BlockEntityType.Builder.of(
                            DragonIceStoveBlockEntity::new,
                            ModBlocks.DRAGON_ICE_STOVE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<DragonLightningStoveBlockEntity>> DRAGON_LIGHTNING_STOVE =
            BLOCK_ENTITIES.register("dragon_lightning_stove",
                    () -> BlockEntityType.Builder.of(
                            DragonLightningStoveBlockEntity::new,
                            ModBlocks.DRAGON_LIGHTNING_STOVE.get()
                    ).build(null));
}
