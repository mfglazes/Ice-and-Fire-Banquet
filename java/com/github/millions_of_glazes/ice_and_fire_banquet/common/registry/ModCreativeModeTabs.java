package com.github.millions_of_glazes.ice_and_fire_banquet.common.registry;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.block.ModBlocks;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ice_and_Fire_banquet.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ICE_AND_FIRE_BANQUET_TAB =
            CREATIVE_MODE_TABS.register("ice_and_fire_banquet_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.LOGO_ITEM.get()))
                    .title(Component.translatable("itemGroup.ice_and_fire_banquet_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.DRAGON_FIRE_STOVE.get());
                        pOutput.accept(ModBlocks.DRAGON_ICE_STOVE.get());
                        pOutput.accept(ModBlocks.DRAGON_LIGHTNING_STOVE.get());
                        pOutput.accept(ModBlocks.FIRE_LILY_CRATE.get());
                        pOutput.accept(ModBlocks.FROST_LILY_CRATE.get());
                        pOutput.accept(ModBlocks.LIGHTNING_LILY_CRATE.get());
                        pOutput.accept(ModItems.MAIN_TITLE_MUSIC_DISC.get());
                        pOutput.accept(ModItems.SILVER_KNIFE.get());
                        pOutput.accept(ModItems.COPPER_KNIFE.get());
                        pOutput.accept(ModItems.DRAGONBONE_KNIFE.get());
                        pOutput.accept(ModItems.DRAGONSTEEL_FIRE_KNIFE.get());
                        pOutput.accept(ModItems.DRAGONSTEEL_ICE_KNIFE.get());
                        pOutput.accept(ModItems.DRAGONSTEEL_LIGHTNING_KNIFE.get());
                        pOutput.accept(ModItems.GHOST_KNIFE.get());
                        pOutput.accept(IafItemRegistry.STYMPHALIAN_DAGGER.get());
                        pOutput.accept(ModItems.FIRE_DRAGON_MEAT_SLICE.get());
                        pOutput.accept(ModItems.ICE_DRAGON_MEAT_SLICE.get());
                        pOutput.accept(ModItems.LIGHTNING_DRAGON_MEAT_SLICE.get());
                        pOutput.accept(ModItems.GHOST_POPSICLE.get());
                        pOutput.accept(ModItems.FIRE_DRAGON_RED_FEMALE_HEAD_BANQUET.get());
                        pOutput.accept(ModItems.FIRE_DRAGON_BLOOD_CURRY.get());
                    }).build());

    public static final RegistryObject<CreativeModeTab> ICE_AND_FIRE_BANQUET_COMBINATION =
                CREATIVE_MODE_TABS.register("ice_and_fire_banquet_combination", () -> CreativeModeTab.builder()
                        .icon(() -> new ItemStack(ModItems.GHOST_GUMMY.get()))
                        .title(Component.translatable("itemGroup.ice_and_fire_banquet_combination"))
                        .displayItems((pParameters, pOutput) -> {
                            pOutput.accept(ModItems.GHOST_GUMMY.get());
                        }).withTabsBefore(ICE_AND_FIRE_BANQUET_TAB.getKey())
                        .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
