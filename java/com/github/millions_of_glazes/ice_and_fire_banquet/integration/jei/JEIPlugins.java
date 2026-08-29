package com.github.millions_of_glazes.ice_and_fire_banquet.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.utility.TextUtils;
import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.item.ModItems;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.block.ModBlocks;

import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class JEIPlugins implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(Ice_and_Fire_banquet.MOD_ID, "jei_plugins");

    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.DRAGON_ICE_STOVE.get()), RecipeTypes.CAMPFIRE_COOKING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.DRAGON_FIRE_STOVE.get()), RecipeTypes.CAMPFIRE_COOKING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.DRAGON_LIGHTNING_STOVE.get()), RecipeTypes.CAMPFIRE_COOKING);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(new ItemStack(ModItems.SILVER_KNIFE.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.knife"));
        registration.addIngredientInfo(new ItemStack(ModItems.COPPER_KNIFE.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.knife"));
        registration.addIngredientInfo(new ItemStack(ModItems.GHOST_KNIFE.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.knife"));
        registration.addIngredientInfo(new ItemStack(ModItems.DRAGONSTEEL_FIRE_KNIFE.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.knife"));
        registration.addIngredientInfo(new ItemStack(ModItems.DRAGONSTEEL_ICE_KNIFE.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.knife"));
        registration.addIngredientInfo(new ItemStack(ModItems.DRAGONSTEEL_LIGHTNING_KNIFE.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.knife"));
        registration.addIngredientInfo(new ItemStack(ModItems.DRAGONBONE_KNIFE.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.knife"));
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }
}
