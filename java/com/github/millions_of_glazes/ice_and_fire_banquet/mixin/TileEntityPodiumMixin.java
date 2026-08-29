package com.github.millions_of_glazes.ice_and_fire_banquet.mixin;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import com.github.millions_of_glazes.ice_and_fire_banquet.api.IPodiumFacing;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityPodium.class)
public abstract class TileEntityPodiumMixin implements IPodiumFacing {

    @Unique
    private int podiumFacing = 0; // 默认南方

    @Override
    public int getPodiumFacing() {
        return this.podiumFacing;
    }

    @Override
    public void setPodiumFacing(int facing) {
        this.podiumFacing = facing;
        // 标记数据更改，以便保存和同步
        if ((Object) this instanceof TileEntityPodium self) {
            self.setChanged();
        }
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void onLoad(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("PodiumFacing")) {
            this.podiumFacing = tag.getInt("PodiumFacing");
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void onSaveAdditional(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("PodiumFacing", this.podiumFacing);
    }
}
