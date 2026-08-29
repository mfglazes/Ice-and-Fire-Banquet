package com.github.millions_of_glazes.ice_and_fire_banquet.common.utility;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;

import java.util.List;

public class TooltipHelper {

    /**
     * 添加一个标准药水效果提示（如：黄金瞳 III (1:00)）
     */
    public static void addEffectTooltip(List<Component> tooltip, MobEffect effect, int level, int durationSeconds) {
        MutableComponent effectName = Component.translatable(effect.getDescriptionId());
        String romanLevel = getRomanNumeral(level);
        String durationString = formatDuration(durationSeconds);

        // 格式：名称 等级 (时间)
        tooltip.add(effectName.append(" " + romanLevel + " (" + durationString + ")")
                .withStyle(effect.getCategory().getTooltipFormatting()));
    }

    /**
     * 将等级转换为罗马数字（1-5对应 I-V）
     */
    private static String getRomanNumeral(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> Integer.toString(level);
        };
    }

    /**
     * 将秒数格式化为 mm:ss
     */
    private static String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
}
