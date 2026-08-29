package com.github.millions_of_glazes.ice_and_fire_banquet.event.DragonStoveBlock;

import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.github.millions_of_glazes.ice_and_fire_banquet.entity.DragonFireStoveBlockEntity;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModParticleTypes;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.tag.ForgeTags;
import vectorwing.farmersdelight.common.utility.ItemUtils;
import vectorwing.farmersdelight.common.utility.MathUtils;

import javax.annotation.Nullable;
import java.util.Optional;

public class DragonFireStoveBlock extends StoveBlock {
    public DragonFireStoveBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // 改为自己的方块实体
        return ModEntities.DRAGON_FIRE_STOVE.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // 绑定正确的 ticker
        return createTickerHelper(type, ModEntities.DRAGON_FIRE_STOVE.get(),
                level.isClientSide ? DragonFireStoveBlockEntity::animationTick : DragonFireStoveBlockEntity::cookingTick);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof DragonFireStoveBlockEntity stoveEntity) {
                // 掉落所有槽位物品
                ItemUtils.dropItems(level, pos, stoveEntity.getInventory());
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getItemInHand(hand);
        Item heldItem = heldStack.getItem();

        if (state.getValue(LIT)) {
            // 熄灭操作
            if (heldStack.canPerformAction(net.minecraftforge.common.ToolActions.SHOVEL_DIG)) {
                extinguish(state, level, pos);
                heldStack.hurtAndBreak(1, player, (action) -> action.broadcastBreakEvent(hand));
                return InteractionResult.SUCCESS;
            }
            if (heldStack.is(ForgeTags.BUCKETS_WATER)) {
                if (!level.isClientSide()) {
                    level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                extinguish(state, level, pos);
                if (!player.isCreative()) {
                    player.setItemInHand(hand, heldStack.getCraftingRemainingItem());
                }
                return InteractionResult.SUCCESS;
            }
        } else {
            // 点燃操作
            if (heldItem instanceof FlintAndSteelItem) {
                level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, MathUtils.RAND.nextFloat() * 0.4F + 0.8F);
                level.setBlock(pos, state.setValue(LIT, Boolean.TRUE), 11);
                heldStack.hurtAndBreak(1, player, (action) -> action.broadcastBreakEvent(hand));
                return InteractionResult.SUCCESS;
            }
            if (heldItem instanceof FireChargeItem) {
                level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (MathUtils.RAND.nextFloat() - MathUtils.RAND.nextFloat()) * 0.2F + 1.0F);
                level.setBlock(pos, state.setValue(LIT, Boolean.TRUE), 11);
                if (!player.isCreative()) {
                    heldStack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }

        // 放入食材 - 此处改为 DragonFireStoveBlockEntity
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof DragonFireStoveBlockEntity stoveEntity) {
            int stoveSlot = stoveEntity.getNextEmptySlot();
            if (stoveSlot < 0 || stoveEntity.isStoveBlockedAbove()) {
                return InteractionResult.PASS;
            }

            Optional<CampfireCookingRecipe> recipe = stoveEntity.getMatchingRecipe(new SimpleContainer(new ItemStack[]{heldStack}), stoveSlot);
            if (recipe.isPresent()) {
                if (!level.isClientSide && stoveEntity.addItem(player.getAbilities().instabuild ? heldStack.copy() : heldStack, recipe.get(), stoveSlot)) {
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        if (state.getValue(CampfireBlock.LIT)) {
            double x = (double) pos.getX() + 0.5;
            double y = (double) pos.getY();
            double z = (double) pos.getZ() + 0.5;

            // 音效完全不变
            if (rand.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, ModSounds.BLOCK_STOVE_CRACKLE.get(),
                        SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double horizontalOffset = rand.nextDouble() * 0.6 - 0.3;
            double xOffset = axis == Direction.Axis.X ? direction.getStepX() * 0.52 : horizontalOffset;
            double yOffset = rand.nextDouble() * 6.0 / 16.0;
            double zOffset = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52 : horizontalOffset;

            // 烟雾粒子不变
            level.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0, 0, 0);
            // ★ 唯一修改：火焰粒子替换为灵魂火焰（冰蓝色火焰）
            level.addParticle(ModParticleTypes.DRAGON_FIRE_FLAME.get(), x + xOffset, y + yOffset, z + zOffset, 0, 0, 0);
            System.out.println("Particle spawned at " + pos);
        }
    }
}
