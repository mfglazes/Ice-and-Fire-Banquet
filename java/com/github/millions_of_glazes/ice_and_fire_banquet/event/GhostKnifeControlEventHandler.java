package com.github.millions_of_glazes.ice_and_fire_banquet.event;

import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ice_and_Fire_banquet.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GhostKnifeControlEventHandler {

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        GhostKnife.onLeftClickEmpty(event);
    }
}
