package com.github.millions_of_glazes.ice_and_fire_banquet.common.utility;

import com.github.millions_of_glazes.ice_and_fire_banquet.capability.DragonEyeDataProvider;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GoldenEyeUtils {

    // 颜色映射表（与命令中的 COLORS 保持一致）
    private static final Map<String, List<String>> COLORS = Map.of(
            "fire", List.of("red", "green", "bronze", "gray"),
            "ice", List.of("blue", "white", "sapphire", "silver"),
            "lightning", List.of("electric", "amethyst", "copper", "black")
    );

    /**
     * 基础重载：接受变种索引（0-3）
     */
    public static void applyGoldenEye(ServerPlayer player,
                                      String dragonType,
                                      int variant,
                                      boolean isMale,
                                      int addedLevel,
                                      int durationSeconds,
                                      double speedMultiplier) {
        // 获取已有的黄金瞳效果
        MobEffectInstance existing = player.getEffect(ModEffects.GOLDEN_EYE.get());

        int newLevel;
        int newDurationTicks;

        if (existing != null) {
            // 等级叠加：旧等级 + 新等级，不超过 5
            int oldLevel = existing.getAmplifier() + 1; // amplifier 0 = 等级1
            newLevel = Math.min(5, oldLevel + addedLevel);

            // 时间叠加：旧剩余时间 + 新时间
            int oldDuration = existing.getDuration();
            newDurationTicks = oldDuration + durationSeconds * 20;

            // 移除旧效果，避免重复
            player.removeEffect(ModEffects.GOLDEN_EYE.get());
        } else {
            newLevel = addedLevel;
            newDurationTicks = durationSeconds * 20;
        }

        // 添加合并后的效果
        player.addEffect(new MobEffectInstance(
                ModEffects.GOLDEN_EYE.get(),
                newDurationTicks,
                newLevel - 1,              // amplifier = 等级-1
                false,                     // 不是环境效果
                true,                      // 显示粒子
                true                       // 显示图标
        ));

        // 同步更新 Capability 中的龙种信息（等级必须与效果等级一致）
        player.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).ifPresent(data -> {
            data.setDragonInfo(dragonType, variant, newLevel);
            data.setMale(isMale);
            data.setSpeedMultiplier(speedMultiplier);
        });
    }

    /**
     * 颜色名称重载：接受颜色字符串（如 "red", "blue", "electric" 等）
     */
    public static void applyGoldenEye(ServerPlayer player,
                                      String dragonType,
                                      String colorName,
                                      boolean isMale,
                                      int addedLevel,
                                      int durationSeconds,
                                      double speedMultiplier) {
        int variant = parseVariant(dragonType, colorName);
        if (variant == -1) {
            variant = 0; // 无效颜色时回退到第一个变种
        }
        applyGoldenEye(player, dragonType, variant, isMale, addedLevel, durationSeconds, speedMultiplier);
    }

    /**
     * 颜色名称 + 性别字符串重载：最常用的方法
     */
    public static void applyGoldenEye(ServerPlayer player,
                                      String dragonType,
                                      String colorName,
                                      String gender,
                                      int addedLevel,
                                      int durationSeconds,
                                      double speedMultiplier) {
        boolean isMale = parseGender(gender);
        applyGoldenEye(player, dragonType, colorName, isMale, addedLevel, durationSeconds, speedMultiplier);
    }

    /**
     * 解析颜色名称或数字索引为变种码（0-3），无效返回 -1
     */
    public static int parseVariant(String dragonType, String color) {
        List<String> valid = COLORS.get(dragonType);
        if (valid == null) return -1;

        // 尝试解析为数字索引
        try {
            int idx = Integer.parseInt(color);
            if (idx >= 0 && idx < valid.size()) {
                return idx;
            }
        } catch (NumberFormatException ignored) {}

        // 尝试匹配颜色名称（忽略大小写和下划线）
        for (int i = 0; i < valid.size(); i++) {
            if (valid.get(i).equalsIgnoreCase(color) ||
                    valid.get(i).replace("_", "").equalsIgnoreCase(color.replace("_", ""))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 解析性别字符串，支持 "male"/"m"/"female"/"f"，默认雄性
     */
    public static boolean parseGender(String gender) {
        return switch (gender.toLowerCase(Locale.ROOT)) {
            case "male", "m" -> true;
            case "female", "f" -> false;
            default -> true; // 默认雄性
        };
    }
}