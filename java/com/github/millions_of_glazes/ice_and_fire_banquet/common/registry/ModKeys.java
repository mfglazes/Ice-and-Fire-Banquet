package com.github.millions_of_glazes.ice_and_fire_banquet.common.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class ModKeys {
    public static final KeyMapping DRAGON_TRANSFORM = new KeyMapping("key.ice_and_fire_banquet.transform", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.category.ice_and_fire_banquet");
    public static final KeyMapping DRAGON_ROAR = new KeyMapping("key.ice_and_fire_banquet.roar", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.category.ice_and_fire_banquet");

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(DRAGON_TRANSFORM);
        event.register(DRAGON_ROAR);
    }
}
