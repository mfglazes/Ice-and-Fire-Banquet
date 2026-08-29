package com.github.millions_of_glazes.ice_and_fire_banquet.network;

import com.github.millions_of_glazes.ice_and_fire_banquet.capability.DragonEyeDataProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketDragonTransform {
    public PacketDragonTransform() {}
    public static void encode(PacketDragonTransform msg, FriendlyByteBuf buf) {}
    public static PacketDragonTransform decode(FriendlyByteBuf buf) { return new PacketDragonTransform(); }

    public static void handle(PacketDragonTransform msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).ifPresent(data -> {
                    if (data.hasGoldenEyeEffect(player)) {
                        if (data.isMorphActive()) data.endMorph(player);
                        else data.startMorph(player);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
