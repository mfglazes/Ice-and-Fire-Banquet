package com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.block;

import com.github.alexthe666.iceandfire.block.BlockPodium;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import com.github.millions_of_glazes.ice_and_fire_banquet.event.DragonStoveBlock.DragonFireStoveBlock;
import com.github.millions_of_glazes.ice_and_fire_banquet.event.DragonStoveBlock.DragonIceStoveBlock;
import com.github.millions_of_glazes.ice_and_fire_banquet.event.DragonStoveBlock.DragonLightningStoveBlock;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.item.ModItems.ITEMS;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Ice_and_Fire_banquet.MOD_ID);

    public static final RegistryObject<Block> FIRE_LILY_CRATE =
            registerBlock("fire_lily_crate", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2.0F,3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> FROST_LILY_CRATE =
            registerBlock("frost_lily_crate", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2.0F,3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIGHTNING_LILY_CRATE =
            registerBlock("lightning_lily_crate", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2.0F,3.0F).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> DRAGON_ICE_STOVE =
            registerBlock("dragon_ice_stove", () -> new DragonIceStoveBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS).lightLevel(litBlockEmission(13))));

    public static final RegistryObject<Block> DRAGON_FIRE_STOVE =
            registerBlock("dragon_fire_stove", () -> new DragonFireStoveBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS).lightLevel(litBlockEmission(13))));

    public static final RegistryObject<Block> DRAGON_LIGHTNING_STOVE =
            registerBlock("dragon_lightning_stove", () -> new DragonLightningStoveBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS).lightLevel(litBlockEmission(13))));

    public static final RegistryObject<Block> PODIUM_LUCUMA =
            registerBlock("podium_lucuma", BlockPodium::new);

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return (state) -> (Boolean)state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    private static <T extends Block> void registerBlockItems(String name, Supplier<T> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> blockRegistry = BLOCKS.register(name, block);
        registerBlockItems(name, blockRegistry);
        return blockRegistry;
    }

    public static void register(IEventBus eventbus) {
        BLOCKS.register(eventbus);
    }
}
