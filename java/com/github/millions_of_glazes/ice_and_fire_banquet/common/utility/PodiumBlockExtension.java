package com.github.millions_of_glazes.ice_and_fire_banquet.common.utility;

import com.github.alexthe666.iceandfire.entity.tile.IafTileEntityRegistry;
import com.github.millions_of_glazes.ice_and_fire_banquet.api.BlockEntityTypeExtension;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PodiumBlockExtension {
    public static void addPodiumBlock(Block block) {
        BlockEntityType<?> podiumType = IafTileEntityRegistry.PODIUM.get();
        ((BlockEntityTypeExtension) podiumType).addValidBlock(block);
    }
}
