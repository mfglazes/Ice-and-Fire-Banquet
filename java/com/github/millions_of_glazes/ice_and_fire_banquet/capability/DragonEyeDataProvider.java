package com.github.millions_of_glazes.ice_and_fire_banquet.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DragonEyeDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<DragonEyeData> DRAGON_EYE_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    private final DragonEyeData data = new DragonEyeData();
    private final LazyOptional<DragonEyeData> optional = LazyOptional.of(() -> data);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == DRAGON_EYE_DATA ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() { return data.writeNBT(); }

    @Override
    public void deserializeNBT(CompoundTag nbt) { data.readNBT(nbt); }
}
