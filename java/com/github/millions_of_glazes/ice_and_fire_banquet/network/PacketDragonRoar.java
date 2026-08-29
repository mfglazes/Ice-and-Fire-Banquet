package com.github.millions_of_glazes.ice_and_fire_banquet.network;

import com.github.millions_of_glazes.ice_and_fire_banquet.capability.DragonEyeDataProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketDragonRoar {

    public PacketDragonRoar() {}

    public static void encode(PacketDragonRoar msg, FriendlyByteBuf buf) {}

    public static PacketDragonRoar decode(FriendlyByteBuf buf) {
        return new PacketDragonRoar();
    }

    public static void handle(PacketDragonRoar msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).ifPresent(data -> {
                    if (data.isMorphActive()) {
                        var dragon = data.getMorphDragon(player);
                        if (dragon != null) {
                            // 龙咆哮（保留冰火传说原生效果）
                            dragon.roar();

                            // 给予化龙玩家力量 II，持续 10 秒
                            player.addEffect(new MobEffectInstance(
                                    MobEffects.DAMAGE_BOOST, 200, 1,
                                    false, true, true));

                            // 对附近所有生物施加虚弱
                            int weaknessAmplifier = data.getLevel() >= 5 ? 1 : 0; // 5级→虚弱II，其余→虚弱I
                            int durationTicks = 3000;

                            AABB area = dragon.getBoundingBox().inflate(10.0D); // 半径10格
                            List<LivingEntity> nearbyEntities = dragon.level().getEntitiesOfClass(
                                    LivingEntity.class, area,
                                    entity -> entity != player && entity != dragon && entity.isAlive());

                            for (LivingEntity target : nearbyEntities) {
                                target.addEffect(new MobEffectInstance(
                                        MobEffects.WEAKNESS, durationTicks, weaknessAmplifier,
                                        false, true, true));
                            }
                        }
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
