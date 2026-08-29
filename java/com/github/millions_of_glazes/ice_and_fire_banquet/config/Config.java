package com.github.millions_of_glazes.ice_and_fire_banquet.config;

import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Ice_and_Fire_banquet.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    // 总开关：控制 Podium 上所有自定义模型
    private static final ForgeConfigSpec.BooleanValue ENABLE_PODIUM_DRAGON_BLOOD_MODELS = BUILDER
            .comment("Master switch for all custom models on Podium")
            .define("enablePodiumModels", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean logDirtBlock;
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;
    public static boolean enablePodiumModels;

    private static boolean validateItemName(final Object obj) {
        if (obj instanceof String itemName) {
            try {
                ResourceLocation rl = new ResourceLocation(itemName);
                return ForgeRegistries.ITEMS.containsKey(rl);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) return;
        logDirtBlock = LOG_DIRT_BLOCK.get();
        magicNumber = MAGIC_NUMBER.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
        items = ITEM_STRINGS.get().stream()
                .map(itemName -> {
                    ResourceLocation rl = new ResourceLocation(itemName);
                    return ForgeRegistries.ITEMS.getValue(rl);
                })
                .collect(Collectors.toSet());
        enablePodiumModels = ENABLE_PODIUM_DRAGON_BLOOD_MODELS.get();
    }
}