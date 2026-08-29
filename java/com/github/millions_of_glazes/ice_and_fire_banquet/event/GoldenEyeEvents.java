package com.github.millions_of_glazes.ice_and_fire_banquet.event;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import com.github.millions_of_glazes.ice_and_fire_banquet.capability.DragonEyeDataProvider;
import com.github.millions_of_glazes.ice_and_fire_banquet.command.CommandGoldenEye;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Ice_and_Fire_banquet.MOD_ID)
public class GoldenEyeEvents {

    // 龙中立（允许还手）
    @SubscribeEvent
    public static void onDragonTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof EntityDragonBase dragon && event.getNewTarget() instanceof Player player) {
            player.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).ifPresent(data -> {
                if (data.hasGoldenEyeEffect(player) && data.getLevel() >= dragon.getDragonStage()) {
                    // 如果龙正在被该玩家攻击（防守反击），允许设定目标
                    if (dragon.getLastHurtByMob() == player) {
                        return; // 不取消，让龙还手
                    }
                    event.setCanceled(true);
                }
            });
        }
    }

    // 效果到期解除化身
    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance() != null &&
                event.getEffectInstance().getEffect() == ModEffects.GOLDEN_EYE.get() &&
                event.getEntity() instanceof ServerPlayer player) {
            if (!player.hasEffect(ModEffects.GOLDEN_EYE.get())) {
                player.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).ifPresent(data -> {
                    if (data.isMorphActive()) data.endMorph(player);
                });
            }
        }
    }

    // 玩家主动下龙自动解除
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;
        player.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).ifPresent(data -> {
            if (!data.isMorphActive()) return;
            var dragon = data.getMorphDragon(player);
            if (dragon == null || player.getVehicle() != dragon) {
                data.endMorph((ServerPlayer) player);
            }
        });
    }

    // 龙死亡 -> 玩家同死
    @SubscribeEvent
    public static void onDragonDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityDragonBase dragon)) return;
        if (dragon.level().isClientSide) return;
        try {
            CompoundTag data = dragon.getPersistentData();
            if (!data.contains("MorphOwner")) return;
            UUID owner = data.getUUID("MorphOwner");
            Player player = dragon.level().getPlayerByUUID(owner);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).ifPresent(cap -> {
                    if (cap.isMorphActive() && cap.getMorphDragon(player) == dragon) {
                        serverPlayer.setInvulnerable(false);
                        serverPlayer.kill();
                        dragon.discard();
                        cap.resetMorph();
                    }
                });
            }
        } catch (Exception e) {
            Ice_and_Fire_banquet.LOGGER.error("处理龙死亡事件时异常", e);
        }
    }

    // ★ 化身龙受击 → 伤害转移给玩家，龙不受伤害
    @SubscribeEvent
    public static void onDragonHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof EntityDragonBase dragon)) return;
        if (dragon.level().isClientSide) return;

        CompoundTag persistentData = dragon.getPersistentData();
        if (!persistentData.contains("MorphOwner")) return;
        UUID owner = persistentData.getUUID("MorphOwner");
        Player player = dragon.level().getPlayerByUUID(owner);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).ifPresent(data -> {
                if (data.isMorphActive() && data.getMorphDragon(player) == dragon) {
                    if (!serverPlayer.isAlive()) {
                        // 玩家已死，龙也去死
                        dragon.kill();
                        data.resetMorph();
                        return;
                    }
                    event.setCanceled(true); // 龙不受伤害
                    serverPlayer.hurt(event.getSource(), event.getAmount()); // 伤害转移
                }
            });
        }
    }

    // ★ 玩家死亡 → 龙也死亡，并清理状态
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        player.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).ifPresent(data -> {
            if (data.isMorphActive()) {
                EntityDragonBase dragon = data.getMorphDragon(player);
                if (dragon != null) {
                    dragon.discard();
                }
                data.resetMorph();
                // 无需调用 endMorph 恢复玩家无敌/显形，因为玩家即将死亡
            }
        });
    }

    // 防止眼罩失明（黄金瞳期间）
    @SubscribeEvent
    public static void onBlindnessApply(MobEffectEvent.Applicable event) {
        if (event.getEffectInstance().getEffect() == MobEffects.BLINDNESS &&
                event.getEntity() instanceof Player player &&
                player.hasEffect(ModEffects.GOLDEN_EYE.get()) &&
                player.getItemBySlot(EquipmentSlot.HEAD).getItem() == IafItemRegistry.BLINDFOLD.get()) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandGoldenEye.register(event.getDispatcher());
    }
}
