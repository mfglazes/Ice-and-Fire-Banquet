package com.github.millions_of_glazes.ice_and_fire_banquet.event;

import com.github.alexthe666.iceandfire.item.DragonSteelOverrides;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.common.item.KnifeItem;

import javax.annotation.Nullable;
import java.util.List;

public class ItemKnife extends KnifeItem implements DragonSteelOverrides<ItemKnife> {

    public ItemKnife(Tier tier, float attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 1. 先执行父类（KnifeItem）的逻辑，保证基础功能不丢失
        boolean result = super.hurtEnemy(stack, target, attacker);
        // 2. 再调用 DragonSteel 的特殊效果（火焰、击退等）
        DragonSteelOverrides.super.hurtEnemy(this, stack, target, attacker);
        return result;
    }

    // Tooltip 入口
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        // 保留父类的 Tooltip（如果有）
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        // 加上 DragonSteel 的特性描述
        DragonSteelOverrides.super.appendHoverText(this.getTier(), stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> bakeDragonsteel() {
        return null;
    }
}