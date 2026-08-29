package com.github.millions_of_glazes.ice_and_fire_banquet.config;


import com.github.millions_of_glazes.ice_and_fire_banquet.client.EyeRenderHandler;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class GoldenEyeClientConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // 默认眼睛列表（必须在 EYES_LIST 之前定义）
    private static final List<String> DEFAULT_EYES = List.of(
            "-0.0935,-0.22,-0.25,0.0625,255,215,0,255",
            "0.0935,-0.22,-0.25,0.0625,255,215,0,255"
    );

    public static final ForgeConfigSpec.IntValue EYE_COUNT = BUILDER
            .comment("Number of golden eyes to render (max 32768)")
            .defineInRange("eyeCount", 2, 0, 32768);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EYES_LIST = BUILDER
            .comment("List of eyes to render. Each entry format: x,y,z,size,r,g,b,a",
                    "x, y, z are floats (position relative to head model origin at bottom center)",
                    "size is a float (side length of the eye quad)",
                    "r, g, b, a are integers 0-255 (color and alpha)",
                    "Default left eye: -0.0935,-0.225,-2,0.0625,255,215,0,255",
                    "Default right eye: 0.0935,-0.225,-2,0.0625,255,215,0,255")
            .defineList("eyes", DEFAULT_EYES, GoldenEyeClientConfig::validateEyeString);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static boolean validateEyeString(Object obj) {
        if (!(obj instanceof String s)) return false;
        String[] parts = s.split(",");
        if (parts.length != 8) return false;
        try {
            Float.parseFloat(parts[0].trim());
            Float.parseFloat(parts[1].trim());
            Float.parseFloat(parts[2].trim());
            Float.parseFloat(parts[3].trim());
            for (int i = 4; i < 8; i++) {
                int val = Integer.parseInt(parts[i].trim());
                if (val < 0 || val > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 从配置文件读取并应用眼睛渲染参数。
     * 必须在配置加载完成后调用（例如在 EntityRenderersEvent.AddLayers 事件中）。
     */
    public static void applyConfig() {
        int count = EYE_COUNT.get();
        List<? extends String> eyeStrings = EYES_LIST.get();
        List<EyeRenderHandler.EyeRenderData> parsed = parseEyes(eyeStrings);
        if (parsed.size() > count) {
            parsed = parsed.subList(0, count);
        }
        EyeRenderHandler.setEyes(parsed);
    }

    private static List<EyeRenderHandler.EyeRenderData> parseEyes(List<? extends String> eyeStrings) {
        List<EyeRenderHandler.EyeRenderData> result = new ArrayList<>();
        for (String s : eyeStrings) {
            try {
                String[] parts = s.split(",");
                float x = Float.parseFloat(parts[0].trim());
                float y = Float.parseFloat(parts[1].trim());
                float z = Float.parseFloat(parts[2].trim());
                float size = Float.parseFloat(parts[3].trim());
                int r = Integer.parseInt(parts[4].trim());
                int g = Integer.parseInt(parts[5].trim());
                int b = Integer.parseInt(parts[6].trim());
                int a = Integer.parseInt(parts[7].trim());
                result.add(new EyeRenderHandler.EyeRenderData(x, y, z, size, r, g, b, a));
            } catch (Exception e) {
                // 忽略无效条目
            }
        }
        return result;
    }
}
