package com.github.millions_of_glazes.ice_and_fire_banquet.common.registry;

import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import com.github.millions_of_glazes.ice_and_fire_banquet.network.PacketDragonRoar;
import com.github.millions_of_glazes.ice_and_fire_banquet.network.PacketDragonTransform;
import com.github.millions_of_glazes.ice_and_fire_banquet.network.PacketGhostKnifeShoot;
import com.github.millions_of_glazes.ice_and_fire_banquet.network.PacketSyncMorph;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Ice_and_Fire_banquet.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void register() {
        int index = 0;
        CHANNEL.registerMessage(index++, PacketDragonTransform.class, PacketDragonTransform::encode, PacketDragonTransform::decode, PacketDragonTransform::handle);
        CHANNEL.registerMessage(index++, PacketDragonRoar.class, PacketDragonRoar::encode, PacketDragonRoar::decode, PacketDragonRoar::handle);
        CHANNEL.registerMessage(index++, PacketSyncMorph.class, PacketSyncMorph::encode, PacketSyncMorph::decode, PacketSyncMorph::handle);
        CHANNEL.registerMessage(index++, PacketGhostKnifeShoot.class, PacketGhostKnifeShoot::encode, PacketGhostKnifeShoot::decode, PacketGhostKnifeShoot::handle);
    }
}
