package com.github.millions_of_glazes.ice_and_fire_banquet.client;

import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModKeys;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ice_and_Fire_banquet.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyRegistry {
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        ModKeys.register(event);
    }
}
