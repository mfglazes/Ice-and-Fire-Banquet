package com.github.millions_of_glazes.ice_and_fire_banquet.capability;

import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ice_and_Fire_banquet.MOD_ID)
public class DragonEyeStorage {
    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(Ice_and_Fire_banquet.MOD_ID, "dragon_eye"), new DragonEyeDataProvider());
        }
    }
}
