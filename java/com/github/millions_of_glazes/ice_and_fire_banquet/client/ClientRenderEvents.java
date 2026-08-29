package com.github.millions_of_glazes.ice_and_fire_banquet.client;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import com.github.millions_of_glazes.ice_and_fire_banquet.capability.DragonEyeDataProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ice_and_Fire_banquet.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientRenderEvents {

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (isMorphed(event.getEntity())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (isMorphed(Minecraft.getInstance().player)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderArm(RenderArmEvent event) {
        if (isMorphed(Minecraft.getInstance().player)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        player.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).ifPresent(data -> {
            if (data.isMorphActive()) {
                EntityDragonBase dragon = data.getMorphDragon(player);
                if (dragon != null && Minecraft.getInstance().getCameraEntity() != dragon) {
                    Minecraft.getInstance().setCameraEntity(dragon);
                }
            } else {
                if (Minecraft.getInstance().getCameraEntity() != player) {
                    Minecraft.getInstance().setCameraEntity(player);
                }
            }
        });
    }

    private static boolean isMorphed(Player player) {
        if (player == null) return false;
        return player.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).map(d -> d.isMorphActive()).orElse(false);
    }
}
