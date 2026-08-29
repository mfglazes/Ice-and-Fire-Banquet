package com.github.millions_of_glazes.ice_and_fire_banquet.entity;

import com.github.alexthe666.iceandfire.entity.EntityGhostSword;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

public class EntityGhostKnife extends EntityGhostSword implements IEntityAdditionalSpawnData {

    private ItemStack renderStack = ItemStack.EMPTY;

    // 注册用的工厂构造（必须保留）
    public EntityGhostKnife(EntityType<? extends EntityGhostSword> type, Level world) {
        super(type, world);
    }

    // 客户端接收网络包时使用的构造（修正版）
    public EntityGhostKnife(PlayMessages.SpawnEntity spawnEntity, Level world) {
        super(ModEntities.GHOST_KNIFE.get(), world);   // 改为使用自己的实体类型
    }

    // 实际使用的构造
    public EntityGhostKnife(Level world, LivingEntity shooter, double damage, ItemStack renderStack) {
        super(ModEntities.GHOST_KNIFE.get(), world, shooter, damage);
        this.renderStack = renderStack.copy();
    }

    public ItemStack getRenderStack() {
        return renderStack;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("RenderItem")) {
            renderStack = ItemStack.of(tag.getCompound("RenderItem"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!renderStack.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            renderStack.save(itemTag);
            tag.put("RenderItem", itemTag);
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeItem(renderStack);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        renderStack = additionalData.readItem();
    }

    @NotNull
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
