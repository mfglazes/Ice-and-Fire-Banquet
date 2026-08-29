package com.github.millions_of_glazes.ice_and_fire_banquet.network;

import com.github.millions_of_glazes.ice_and_fire_banquet.event.GhostKnife;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGhostKnifeShoot {

    public PacketGhostKnifeShoot() {}

    public static void encode(PacketGhostKnifeShoot msg, FriendlyByteBuf buf) {}

    public static PacketGhostKnifeShoot decode(FriendlyByteBuf buf) {
        return new PacketGhostKnifeShoot();
    }

    public static void handle(PacketGhostKnifeShoot msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                GhostKnife.shoot(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
