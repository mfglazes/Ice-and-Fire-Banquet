package com.github.millions_of_glazes.ice_and_fire_banquet.entity;

import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.mixin.accessor.RecipeManagerAccessor;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import java.util.Optional;

public class DragonFireStoveBlockEntity extends SyncedBlockEntity {
    private static final VoxelShape GRILLING_AREA = Block.box(3.0F, 0.0F, 3.0F, 13.0F, 1.0F, 13.0F);
    private static final int INVENTORY_SLOT_COUNT = 6;
    private final ItemStackHandler inventory = this.createHandler();
    private final int[] cookingTimes = new int[6];
    private final int[] cookingTimesTotal = new int[6];
    private ResourceLocation[] lastRecipeIDs = new ResourceLocation[6];

    public DragonFireStoveBlockEntity(BlockPos pos, BlockState state) {
        super(ModEntities.DRAGON_FIRE_STOVE.get(), pos, state);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("Inventory")) {
            this.inventory.deserializeNBT(compound.getCompound("Inventory"));
        } else {
            this.inventory.deserializeNBT(compound);
        }

        if (compound.contains("CookingTimes", 11)) {
            int[] arrayCookingTimes = compound.getIntArray("CookingTimes");
            System.arraycopy(arrayCookingTimes, 0, this.cookingTimes, 0, Math.min(this.cookingTimesTotal.length, arrayCookingTimes.length));
        }

        if (compound.contains("CookingTotalTimes", 11)) {
            int[] arrayCookingTimesTotal = compound.getIntArray("CookingTotalTimes");
            System.arraycopy(arrayCookingTimesTotal, 0, this.cookingTimesTotal, 0, Math.min(this.cookingTimesTotal.length, arrayCookingTimesTotal.length));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        this.writeItems(compound);
        compound.putIntArray("CookingTimes", this.cookingTimes);
        compound.putIntArray("CookingTotalTimes", this.cookingTimesTotal);
    }

    private CompoundTag writeItems(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Inventory", this.inventory.serializeNBT());
        return compound;
    }

    // 核心烹饪逻辑（服务端）
    public static void cookingTick(Level level, BlockPos pos, BlockState state, DragonFireStoveBlockEntity stove) {
        boolean isStoveLit = state.getValue(StoveBlock.LIT);
        if (stove.isStoveBlockedAbove()) {
            if (!ItemUtils.isInventoryEmpty(stove.inventory)) {
                ItemUtils.dropItems(level, pos, stove.inventory);
                stove.inventoryChanged();
            }
        } else if (isStoveLit) {
            stove.cookAndOutputItems();
        } else {
            for (int i = 0; i < stove.inventory.getSlots(); ++i) {
                if (stove.cookingTimes[i] > 0) {
                    stove.cookingTimes[i] = Mth.clamp(stove.cookingTimes[i] - 2, 0, stove.cookingTimesTotal[i]);
                }
            }
        }
    }

    // 烟雾动画逻辑（客户端） - 完全不变，只产生烟雾
    public static void animationTick(Level level, BlockPos pos, BlockState state, DragonFireStoveBlockEntity stove) {
        for (int i = 0; i < stove.inventory.getSlots(); ++i) {
            if (!stove.inventory.getStackInSlot(i).isEmpty() && level.random.nextFloat() < 0.2F) {
                Vec2 stoveItemVector = stove.getStoveItemOffset(i);
                Direction direction = state.getValue(StoveBlock.FACING);
                int directionIndex = direction.get2DDataValue();
                Vec2 offset = directionIndex % 2 == 0 ? stoveItemVector : new Vec2(stoveItemVector.y, stoveItemVector.x);

                double x = pos.getX() + 0.5 - (float) direction.getStepX() * offset.x + (float) direction.getClockWise().getStepX() * offset.x;
                double y = pos.getY() + 1.0;
                double z = pos.getZ() + 0.5 - (float) direction.getStepZ() * offset.y + (float) direction.getClockWise().getStepZ() * offset.y;

                for (int k = 0; k < 3; ++k) {
                    level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 5.0E-4, 0.0);
                }
            }
        }
    }

    private void cookAndOutputItems() {
        if (this.level != null) {
            boolean didInventoryChange = false;

            for (int i = 0; i < this.inventory.getSlots(); ++i) {
                ItemStack stoveStack = this.inventory.getStackInSlot(i);
                if (!stoveStack.isEmpty()) {
                    this.cookingTimes[i]++;
                    if (this.cookingTimes[i] >= this.cookingTimesTotal[i]) {
                        Container inventoryWrapper = new SimpleContainer(new ItemStack[]{stoveStack});
                        Optional<CampfireCookingRecipe> recipe = this.getMatchingRecipe(inventoryWrapper, i);
                        if (recipe.isPresent()) {
                            ItemStack resultStack = recipe.get().getResultItem(this.level.registryAccess());
                            if (!resultStack.isEmpty()) {
                                ItemUtils.spawnItemEntity(this.level, resultStack.copy(),
                                        this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.0, this.worldPosition.getZ() + 0.5,
                                        this.level.random.nextGaussian() * 0.01, 0.1, this.level.random.nextGaussian() * 0.01);
                            }
                        }
                        this.inventory.setStackInSlot(i, ItemStack.EMPTY);
                        didInventoryChange = true;
                    }
                }
            }

            if (didInventoryChange) {
                this.inventoryChanged();
            }
        }
    }

    public int getNextEmptySlot() {
        for (int i = 0; i < this.inventory.getSlots(); ++i) {
            if (this.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public boolean addItem(ItemStack itemStackIn, CampfireCookingRecipe recipe, int slot) {
        if (0 <= slot && slot < this.inventory.getSlots()) {
            ItemStack slotStack = this.inventory.getStackInSlot(slot);
            if (slotStack.isEmpty()) {
                this.cookingTimesTotal[slot] = recipe.getCookingTime();
                this.cookingTimes[slot] = 0;
                this.inventory.setStackInSlot(slot, itemStackIn.split(1));
                this.lastRecipeIDs[slot] = recipe.getId();
                this.inventoryChanged();
                return true;
            }
        }
        return false;
    }

    public Optional<CampfireCookingRecipe> getMatchingRecipe(Container recipeWrapper, int slot) {
        if (this.level == null) {
            return Optional.empty();
        }
        if (this.lastRecipeIDs[slot] != null) {
            Recipe<Container> recipe = ((RecipeManagerAccessor) this.level.getRecipeManager()).getRecipeMap(RecipeType.CAMPFIRE_COOKING).get(this.lastRecipeIDs[slot]);
            if (recipe instanceof CampfireCookingRecipe && recipe.matches(recipeWrapper, this.level)) {
                return Optional.of((CampfireCookingRecipe) recipe);
            }
        }
        return this.level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, recipeWrapper, this.level);
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public boolean isStoveBlockedAbove() {
        if (this.level != null) {
            BlockState above = this.level.getBlockState(this.worldPosition.above());
            return Shapes.joinIsNotEmpty(GRILLING_AREA, above.getShape(this.level, this.worldPosition.above()), BooleanOp.AND);
        }
        return false;
    }

    public Vec2 getStoveItemOffset(int index) {
        final float X_OFFSET = 0.3F;
        final float Y_OFFSET = 0.2F;
        Vec2[] OFFSETS = new Vec2[]{
                new Vec2(0.3F, 0.2F),
                new Vec2(0.0F, 0.2F),
                new Vec2(-0.3F, 0.2F),
                new Vec2(0.3F, -0.2F),
                new Vec2(0.0F, -0.2F),
                new Vec2(-0.3F, -0.2F)
        };
        return OFFSETS[index];
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.writeItems(new CompoundTag());
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }
}
