package com.github.millions_of_glazes.ice_and_fire_banquet.event.DragonSteelKnife;

import com.github.alexthe666.iceandfire.entity.EntityDragonLightningCharge;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.item.DragonSteelOverrides;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import vectorwing.farmersdelight.common.item.KnifeItem;

import javax.annotation.Nullable;
import java.util.List;

public class LightningDragonSteelKnife extends KnifeItem implements DragonSteelOverrides<LightningDragonSteelKnife> {

    private static final int MIN_CHARGE_TICKS = 5;
    private static final int COOLDOWN_TICKS = 50;
    private static final double PROJECTILE_SPEED = 2.5;
    private static final double SPAWN_DISTANCE = 1.5;

    public LightningDragonSteelKnife(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    /**
     * 右键开始蓄力（拉弓），冷却中或指向砧板(可交互方块)时无法使用。
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        // 1. 检测玩家视线是否正对着砧板（或其他可交互方块）
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockState state = level.getBlockState(hit.getBlockPos());
            if (state.getBlock() instanceof vectorwing.farmersdelight.common.block.CuttingBoardBlock) {
                return InteractionResultHolder.pass(stack); // 不蓄力，交给方块交互
            }
        }

        // 2. 否则正常开始蓄力
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;
        int chargeTicks = this.getUseDuration(stack) - timeLeft;
        if (chargeTicks < MIN_CHARGE_TICKS || player.getCooldowns().isOnCooldown(this)) return;

        // 消耗耐久（创造模式除外）
        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(4, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }

        Vec3 look = player.getLookAngle();
        if (!level.isClientSide) {
            EntityDragonLightningCharge charge = new EntityDragonLightningCharge(
                    IafEntityRegistry.LIGHTNING_DRAGON_CHARGE.get(),
                    level,
                    player.getX() + look.x * SPAWN_DISTANCE,
                    player.getEyeY() + look.y * SPAWN_DISTANCE,
                    player.getZ() + look.z * SPAWN_DISTANCE,
                    look.x * PROJECTILE_SPEED,
                    look.y * PROJECTILE_SPEED,
                    look.z * PROJECTILE_SPEED
            );
            charge.setOwner(player);
            level.addFreshEntity(charge);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
    }

    // ========== 近战特效（委托给 DragonSteelOverrides） ==========
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        // 调用接口默认的龙钢特效（闪电、击退等）
        DragonSteelOverrides.super.hurtEnemy(this, stack, target, attacker);
        return result;
    }

    // ========== 工具提示（委托给 DragonSteelOverrides） ==========
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn,
                                List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        DragonSteelOverrides.super.appendHoverText(this.getTier(), stack, worldIn, tooltip, flagIn);
    }

    // ========== 实现接口中的 Deprecated 方法 ==========
    @Override
    @Deprecated
    public Multimap<Attribute, AttributeModifier> bakeDragonsteel() {
        // 不额外增加属性，返回空映射
        return ImmutableMultimap.of();
    }
}