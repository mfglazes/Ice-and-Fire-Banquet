package com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.item;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEffects;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModFoods;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModSounds;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.utility.GoldenEyeUtils;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.utility.TooltipHelper;
import com.github.millions_of_glazes.ice_and_fire_banquet.event.GhostKnife;
import net.brdle.collectorsreap.common.item.food.GummyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import com.github.millions_of_glazes.ice_and_fire_banquet.event.ItemKnife;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.item.ConsumableItem;
import vectorwing.farmersdelight.common.item.KnifeItem;

import java.lang.reflect.Constructor;
import java.util.List;

import static com.github.alexthe666.iceandfire.item.IafItemRegistry.*;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Ice_and_Fire_banquet.MOD_ID);

    public static final RegistryObject<Item> FIRE_DRAGON_MEAT_SLICE =
            ITEMS.register("fire_dragon_meat_slice", () -> new ItemDragonFleshSlice(0));
    public static final RegistryObject<Item> ICE_DRAGON_MEAT_SLICE =
            ITEMS.register("ice_dragon_meat_slice", () -> new ItemDragonFleshSlice(1));
    public static final RegistryObject<Item> LIGHTNING_DRAGON_MEAT_SLICE =
            ITEMS.register("lightning_dragon_meat_slice", () -> new ItemDragonFleshSlice(2));
    public static final RegistryObject<Item> GHOST_POPSICLE =
            ITEMS.register("ghost_popsicle", () -> new ConsumableItem(new Item.Properties().food(ModFoods.GHOST_POPSICLE), true));

    public static final RegistryObject<Item> SILVER_KNIFE =
            ITEMS.register("silver_knife", () -> new ItemKnife(SILVER_TOOL_MATERIAL,0.5F,-2.0F, new Item.Properties()));
    public static final RegistryObject<Item> COPPER_KNIFE =
            ITEMS.register("copper_knife", () -> new KnifeItem(COPPER_TOOL_MATERIAL,0.5F,-2.0F,new Item.Properties()));
    public static final RegistryObject<Item> DRAGONBONE_KNIFE =
            ITEMS.register("dragonbone_knife", () -> new KnifeItem(DRAGONBONE_TOOL_MATERIAL,0.5F,-2.0F,new Item.Properties()));
    public static final RegistryObject<Item> DRAGONSTEEL_FIRE_KNIFE =
            ITEMS.register("dragonsteel_fire_knife", () -> createDragonSteelKnife("fire"));
    public static final RegistryObject<Item> DRAGONSTEEL_ICE_KNIFE =
            ITEMS.register("dragonsteel_ice_knife", () -> createDragonSteelKnife("ice"));
    public static final RegistryObject<Item> DRAGONSTEEL_LIGHTNING_KNIFE =
            ITEMS.register("dragonsteel_lightning_knife", () -> createDragonSteelKnife("lightning"));
    public static final RegistryObject<Item> GHOST_KNIFE =
            ITEMS.register("ghost_knife", () -> new GhostKnife(IafItemRegistry.GHOST_SWORD_TOOL_MATERIAL, 1.5F, -2.0F, new Item.Properties()));

    public static final RegistryObject<Item> FIRE_DRAGON_RED_FEMALE_HEAD_BANQUET =
            ITEMS.register("fire_dragon_red_female_head_banquet", () -> new ConsumableItem(new Item.Properties().food(ModFoods.DRAGON_HEAD_BANQUET), true) {
                        @Override
                        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
                            if (!level.isClientSide && entity instanceof ServerPlayer player) {
                                GoldenEyeUtils.applyGoldenEye(player, "fire", "red", "female", 3, 200, 2.5);
                            }
                            return super.finishUsingItem(stack, level, entity);
                        }

                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            tooltip.add(Component.translatable("dragon.red").withStyle(ChatFormatting.DARK_RED));
                            // 动态添加黄金瞳提示（参数与 finishUsingItem 保持一致）
                            TooltipHelper.addEffectTooltip(
                                    tooltip,
                                    ModEffects.GOLDEN_EYE.get(),
                                    3,      // 等级
                                    200      // 持续时间（秒）
                            );
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });

    public static final RegistryObject<Item> FIRE_DRAGON_BLOOD_CURRY =
            ITEMS.register("fire_dragon_blood_curry", () -> new ConsumableItem(new Item.Properties().food(ModFoods.DRAGON_BLOOD_CURRY), true));

    public static final RegistryObject<Item> GHOST_GUMMY =
            ITEMS.register("ghost_gummy", () -> new GummyItem(new Item.Properties().food(ModFoods.GHOST_GUMMY)) {@Override public boolean enabled() {return true;}});

    public static final RegistryObject<Item> MAIN_TITLE_MUSIC_DISC =
            ITEMS.register("main_title_music_disc", () -> new RecordItem(15, ModSounds.MAIN_TITLE, new Item.Properties().stacksTo(1), 2000));

    public static final RegistryObject<Item> LOGO_ITEM =
            ITEMS.register("logo_item", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GOLDEN_EYE_ICON =
            ITEMS.register("golden_eye_icon", () -> new Item(new Item.Properties()));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }








    




    /**
     * 通过反射创建龙钢刀，避免在 ModItems 类加载时触发冰火传说类的初始化。
     */
    private static Item createDragonSteelKnife(String type) {
        try {
            // 1. 获取对应的 DragonSteelTier 常量（反射）
            Class<?> tierClass = Class.forName("com.github.alexthe666.iceandfire.item.DragonSteelTier");
            String tierFieldName = switch (type) {
                case "fire" -> "DRAGONSTEEL_TIER_FIRE";
                case "ice" -> "DRAGONSTEEL_TIER_ICE";
                case "lightning" -> "DRAGONSTEEL_TIER_LIGHTNING";
                default -> throw new IllegalArgumentException("Unknown type: " + type);
            };
            Tier tier = (Tier) tierClass.getField(tierFieldName).get(null);

            // 2. 实例化对应的 Knife 类
            String className = "com.github.millions_of_glazes.ice_and_fire_banquet.event.DragonSteelKnife." +
                    switch (type) {
                        case "fire" -> "FireDragonSteelKnife";
                        case "ice" -> "IceDragonSteelKnife";
                        default -> "LightningDragonSteelKnife";
                    };
            Class<?> knifeClass = Class.forName(className);
            Constructor<?> ctor = knifeClass.getConstructor(Tier.class, float.class, float.class, Item.Properties.class);
            return (Item) ctor.newInstance(tier, -4.5F, -2.0F, new Item.Properties());

        }
        catch (Exception e) {
            Ice_and_Fire_banquet.LOGGER.error("Failed to create dragon steel knife of type {}", type, e);
            // 返回一个占位物品，避免游戏崩溃（可替换为 Items.AIR）
            return new Item(new Item.Properties());
        }
    }


}
