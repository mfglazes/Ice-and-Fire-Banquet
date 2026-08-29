package com.github.millions_of_glazes.ice_and_fire_banquet.event;

import com.github.millions_of_glazes.ice_and_fire_banquet.entity.EntityGhostKnife;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModNetwork;
import com.github.millions_of_glazes.ice_and_fire_banquet.network.PacketGhostKnifeShoot;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.item.KnifeItem;

import javax.annotation.Nullable;
import java.util.List;

public class GhostKnife extends KnifeItem {

    public GhostKnife(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    /**
     * 客户端左键空击 → 发送网络包给服务端
     */
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getItem() instanceof GhostKnife knife) {
            if (!player.getCooldowns().isOnCooldown(knife)) {
                // 客户端发送请求
                ModNetwork.CHANNEL.sendToServer(new PacketGhostKnifeShoot());
            }
        }
    }

    /**
     * 服务端实际执行发射（静态方法，由网络包调用）
     */
    public static void shoot(ServerPlayer player) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(stack.getItem() instanceof GhostKnife knife)) return;
        if (player.getCooldowns().isOnCooldown(knife)) return;

        // 播放音效（所有人可闻）
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ZOMBIE_INFECT, SoundSource.PLAYERS, 1.0F, 1.0F);

        double totalDmg = 0;
        for (AttributeModifier mod : stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)) {
            totalDmg += mod.getAmount();
        }

        EntityGhostKnife shot = new EntityGhostKnife(
                player.level(),
                player,
                totalDmg * 0.5,
                stack.copy()   // ← 关键：复制手中的刀
        );
        shot.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.0F, 0.5F);
        player.level().addFreshEntity(shot);

        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        player.getCooldowns().addCooldown(knife, 10);
    }

    // 保留原版攻击行为
    @Override
    public boolean hurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target, net.minecraft.world.entity.LivingEntity attacker) {
        return super.hurtEnemy(stack, target, attacker);
    }

    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.add(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.iceandfire.ghost_sword.desc_0").withStyle(ChatFormatting.GRAY));
    }
}
