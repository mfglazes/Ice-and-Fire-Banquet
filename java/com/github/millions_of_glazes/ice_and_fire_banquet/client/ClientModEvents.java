package com.github.millions_of_glazes.ice_and_fire_banquet.client;

import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModKeys;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModNetwork;
import com.github.millions_of_glazes.ice_and_fire_banquet.network.PacketDragonRoar;
import com.github.millions_of_glazes.ice_and_fire_banquet.network.PacketDragonTransform;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ice_and_Fire_banquet.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientModEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (ModKeys.DRAGON_TRANSFORM.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(new PacketDragonTransform());
        }
        if (ModKeys.DRAGON_ROAR.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(new PacketDragonRoar());
        }
    }
}
