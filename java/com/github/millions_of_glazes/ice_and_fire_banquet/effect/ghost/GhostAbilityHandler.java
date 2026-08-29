package com.github.millions_of_glazes.ice_and_fire_banquet.effect.ghost;

import com.github.alexthe666.iceandfire.entity.EntityGhost;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEffects;

@Mod.EventBusSubscriber(modid = Ice_and_Fire_banquet.MOD_ID)
public class GhostAbilityHandler {

    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        boolean hasGhost = player.hasEffect(ModEffects.GHOST.get());
        Abilities abilities = player.getAbilities();

        if (hasGhost) {
            player.noPhysics = true;

            if (!player.isCreative() && !player.isSpectator()) {
                abilities.mayfly = true;
                abilities.flying = true;
            }
        }
        else {
            player.noPhysics = false;
            if (!player.isCreative() && !player.isSpectator()) {
                abilities.mayfly = false;
                abilities.flying = false;
            }
        }
        player.onUpdateAbilities();
    }

    @SubscribeEvent
    public static void onTargetChange(LivingChangeTargetEvent event) {
        if (event.getNewTarget() instanceof Player player &&
                player.hasEffect(ModEffects.GHOST.get())) {
            if (event.getEntity() instanceof EntityGhost) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.hasEffect(ModEffects.GHOST.get())) {
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
            }
            player.noPhysics = false;
            player.onUpdateAbilities();
        }
    }
}
