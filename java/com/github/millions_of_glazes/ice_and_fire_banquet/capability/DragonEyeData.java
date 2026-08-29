package com.github.millions_of_glazes.ice_and_fire_banquet.capability;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.EntityLightningDragon;
import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModNetwork;
import com.github.millions_of_glazes.ice_and_fire_banquet.network.PacketSyncMorph;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class DragonEyeData {
    private String dragonType = "";
    private int variant = 0;
    private int level = 1;
    private double speedMultiplier = 1.0;
    private boolean isMale = true;
    private boolean morphActive = false;
    private int dragonId = -1;

    public void setDragonInfo(String type, int var, int lvl) {
        this.dragonType = type;
        this.variant = var;
        this.level = lvl;
    }

    public void setSpeedMultiplier(double mult) { this.speedMultiplier = mult; }
    public void setMale(boolean male) { this.isMale = male; }

    public String getDragonType() {
        return dragonType;
    }
    public int getVariant() {
        return variant;
    }
    public int getLevel() {
        return level;
    }
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
    public boolean isMale() {
        return isMale;
    }
    public boolean isMorphActive() {
        return morphActive;
    }
    public void setMorphActive(boolean active) {
        this.morphActive = active;
    }

    public boolean hasGoldenEyeEffect(Player player) {
        return player.hasEffect(net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getValue(
                 new net.minecraft.resources.ResourceLocation(Ice_and_Fire_banquet.MOD_ID, "golden_eye"))) && !dragonType.isEmpty();
    }

    public void startMorph(ServerPlayer player) {
        if (morphActive || player.level().isClientSide) return;
        try {
            EntityDragonBase dragon = createDragon(player);
            if (dragon == null) {
                player.sendSystemMessage(Component.literal("§c[黄金瞳] 龙种数据错误，化龙失败！"));
                return;
            }
            player.level().addFreshEntity(dragon);
            dragon.getPersistentData().putUUID("MorphOwner", player.getUUID());
            this.dragonId = dragon.getId();

            player.setInvisible(true);      // 玩家隐身，粒子自动隐藏
            player.noPhysics = true;
            player.startRiding(dragon, true);
            morphActive = true;

            // 只复制效果给龙（龙拥有粒子）
            transferEffectsToDragon(player, dragon);

            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncMorph(true));
        }
        catch (Exception e) {
            Ice_and_Fire_banquet.LOGGER.error("化龙时发生异常", e);
            player.sendSystemMessage(Component.literal("§c[黄金瞳] 化龙出现错误，已自动还原！"));
            if (dragonId != -1) {
                var e2 = player.level().getEntity(dragonId);
                if (e2 != null) e2.discard();
            }
            endMorph(player);
        }
    }

    public void endMorph(ServerPlayer player) {
        if (!morphActive) return;
        if (dragonId != -1) {
            var entity = player.level().getEntity(dragonId);
            if (entity instanceof EntityDragonBase dragon) {
                if (player.getVehicle() == dragon) player.stopRiding();
                dragon.discard();
            }
        }
        player.setInvisible(false);         // 恢复可见，粒子自动恢复
        player.noPhysics = false;
        dragonId = -1;
        morphActive = false;

        // 不再调用 showPlayerParticles
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncMorph(false));
    }

    public void resetMorph() {
        dragonId = -1;
        morphActive = false;
    }

    public EntityDragonBase getMorphDragon(Player player) {
        if (dragonId == -1) return null;
        var entity = player.level().getEntity(dragonId);
        return entity instanceof EntityDragonBase d ? d : null;
    }

    public EntityDragonBase createDragon(Player player) {
        EntityDragonBase dragon = switch (dragonType) {
            case "fire" -> new EntityFireDragon(player.level());
            case "ice" -> new EntityIceDragon(player.level());
            case "lightning" -> new EntityLightningDragon(player.level());
            default -> null;
        };
        if (dragon != null) {
            dragon.setPos(player.getX(), player.getY(), player.getZ());
            dragon.setVariant(variant);
            int ageDays = switch (level) {
                case 1 -> 1; case 2 -> 25; case 3 -> 50; case 4 -> 75; case 5 -> 100; default -> 100;
            };
            dragon.setAgeInDays(ageDays);
            dragon.setGender(isMale);
            dragon.setTame(true);
            dragon.setOwnerUUID(player.getUUID());
            dragon.setCustomName(player.getDisplayName().copy().append(" (龙)"));

            // 补全初始状态（修复攻击NPE）
            dragon.setHunger(100);
            dragon.setCommand(0);
            dragon.setInSittingPose(false);      // 替代 setSleeping，确保龙不处于睡觉状态
            dragon.setTackling(false);
            dragon.usingGroundAttack = true;
            dragon.setNoAi(false);
            dragon.heal(dragon.getMaxHealth());

            double baseSpeed = dragon.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
            dragon.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(baseSpeed * speedMultiplier);
        }
        return dragon;
    }

    public void readNBT(CompoundTag tag) {
        dragonType = tag.getString("Type");
        variant = tag.getInt("Variant");
        level = tag.getInt("Level");
        speedMultiplier = tag.contains("SpeedMult") ? tag.getDouble("SpeedMult") : 1.0;
        isMale = tag.contains("IsMale") ? tag.getBoolean("IsMale") : true;
        morphActive = tag.getBoolean("MorphActive");
        if (tag.contains("DragonId")) dragonId = tag.getInt("DragonId");
        else dragonId = -1;
    }

    public CompoundTag writeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Type", dragonType);
        tag.putInt("Variant", variant);
        tag.putInt("Level", level);
        tag.putDouble("SpeedMult", speedMultiplier);
        tag.putBoolean("IsMale", isMale);
        tag.putBoolean("MorphActive", morphActive);
        if (dragonId != -1) tag.putInt("DragonId", dragonId);
        return tag;
    }

    // 只保留 transferEffectsToDragon
    public void transferEffectsToDragon(ServerPlayer player, EntityDragonBase dragon) {
        for (MobEffectInstance effectInstance : player.getActiveEffects()) {
            MobEffect effect = effectInstance.getEffect();
            int duration = effectInstance.getDuration();
            int amplifier = effectInstance.getAmplifier();
            boolean ambient = effectInstance.isAmbient();
            boolean showIcon = effectInstance.isVisible();
            dragon.addEffect(new MobEffectInstance(effect, duration, amplifier, ambient, true, showIcon));
        }
    }
}
