package com.github.millions_of_glazes.ice_and_fire_banquet.network;

import com.github.millions_of_glazes.ice_and_fire_banquet.capability.DragonEyeDataProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncMorph {
    private final boolean active;
    public PacketSyncMorph(boolean active) { this.active = active; }
    public static void encode(PacketSyncMorph msg, FriendlyByteBuf buf) { buf.writeBoolean(msg.active); }
    public static PacketSyncMorph decode(FriendlyByteBuf buf) { return new PacketSyncMorph(buf.readBoolean()); }

    public static void handle(PacketSyncMorph msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.getCapability(DragonEyeDataProvider.DRAGON_EYE_DATA).ifPresent(data -> data.setMorphActive(msg.active));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
