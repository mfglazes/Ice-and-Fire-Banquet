package com.github.millions_of_glazes.ice_and_fire_banquet.mixin;

import com.github.millions_of_glazes.ice_and_fire_banquet.api.BlockEntityTypeExtension;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashSet;
import java.util.Set;

@Mixin(BlockEntityType.class)
public abstract class BlockEntityTypeMixin implements BlockEntityTypeExtension {

    @Shadow
    @Final
    @Mutable
    private Set<Block> validBlocks;

    @Override
    public void addValidBlock(Block block) {
        Set<Block> newSet = new HashSet<>(validBlocks);
        newSet.add(block);
        validBlocks = newSet;
    }
}