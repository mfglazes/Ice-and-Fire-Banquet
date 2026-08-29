package com.github.millions_of_glazes.ice_and_fire_banquet.event.DragonSteelKnife;

import com.github.alexthe666.iceandfire.entity.EntityDragonFireCharge;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.item.DragonSteelOverrides;
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

public class FireDragonSteelKnife extends KnifeItem implements DragonSteelOverrides<FireDragonSteelKnife> {

    // 最小蓄力时间（tick），蓄力不足不发射
    private static final int MIN_CHARGE_TICKS = 5;
    // 发射后的冷却时间（tick）
    private static final int COOLDOWN_TICKS = 50;
    // 弹射物速度
    private static final double PROJECTILE_SPEED = 2.5;
    // 生成位置与玩家眼睛的前方距离
    private static final double SPAWN_DISTANCE = 1.5;

    public FireDragonSteelKnife(Tier tier, float attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
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

    /**
     * 最大使用时长，让玩家可以一直蓄力直到主动松开。
     */
    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    /**
     * 使用动画为弓的拉弦动作。
     */
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    /**
     * 玩家松开右键时触发：检查蓄力时间，生成龙息弹并进入冷却。
     */
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        int chargeTicks = this.getUseDuration(stack) - timeLeft;
        if (chargeTicks < MIN_CHARGE_TICKS) return;          // 蓄力不足
        if (player.getCooldowns().isOnCooldown(this)) return; // 冷却中

        // 消耗耐久（创造模式除外）
        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(4, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }


        Vec3 look = player.getLookAngle();

        EntityDragonFireCharge charge = null;
        if (!level.isClientSide) {
            // 创建火龙龙息弹实体
            charge = new EntityDragonFireCharge(
                    IafEntityRegistry.FIRE_DRAGON_CHARGE.get(),
                    level,
                    player.getX() + look.x * SPAWN_DISTANCE,
                    player.getEyeY() + look.y * SPAWN_DISTANCE,
                    player.getZ() + look.z * SPAWN_DISTANCE,
                    look.x * PROJECTILE_SPEED,
                    look.y * PROJECTILE_SPEED,
                    look.z * PROJECTILE_SPEED
            );
            charge.setOwner(player);  // 设置发射者为玩家，以正确计算伤害归属
            level.addFreshEntity(charge);
        }

        // 播放发射音效
        level.playSound(null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDER_DRAGON_SHOOT,  // 可根据需要换成自定义音效
                SoundSource.PLAYERS,
                1.0F,
                1.0F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        // 设置冷却
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 1. 先执行父类（KnifeItem）的逻辑，保证基础功能不丢失
        boolean result = super.hurtEnemy(stack, target, attacker);
        // 2. 再调用 DragonSteel 的特殊效果（火焰、击退等）
        DragonSteelOverrides.super.hurtEnemy(this, stack, target, attacker);
        return super.hurtEnemy(stack, target, attacker);
    }

    // Tooltip 入口
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        // 保留父类的 Tooltip（如果有）
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        // 加上 DragonSteel 的特性描述
        DragonSteelOverrides.super.appendHoverText(this.getTier(), stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> bakeDragonsteel() {
        return null;
    }
}
