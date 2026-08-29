package com.github.millions_of_glazes.ice_and_fire_banquet.event;

import com.github.alexthe666.iceandfire.item.IafTabRegistry;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.block.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class CreativeTabInsertion {

    public static void insertCannoliAfterTrollBoots() {
        Item cannoli = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("iceandfire", "cannoli")
        );
        Item trollBoots = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("iceandfire", "mountain_troll_leather_boots")
        );

        if (cannoli == null || trollBoots == null) {
            // 物品不存在，直接返回
            return;
        }

        List<Supplier<? extends Item>> list = IafTabRegistry.TAB_ITEMS_LIST;

        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get() == trollBoots) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            // 在目标物品后插入
            list.add(index + 1, () -> cannoli);
        } else {
            // 没找到目标物品，加在末尾作为后备
            list.add(() -> cannoli);
        }
    }

    public static void insertPodiumLucumaAfterAcacia() {
        Block podiumLucuma = ForgeRegistries.BLOCKS.getValue(
                new ResourceLocation("ice_and_fire_banquet", "podium_lucuma")
        );
        Block podiumAcacia = ForgeRegistries.BLOCKS.getValue(
                new ResourceLocation("iceandfire", "podium_acacia")
        );

        // 如果任一物品不存在则直接返回
        if (podiumLucuma == null || podiumAcacia == null) {
            return;
        }

        List<Supplier<? extends Block>> list = IafTabRegistry.TAB_BLOCKS_LIST;

        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get() == podiumAcacia) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            list.add(index + 1, () -> podiumLucuma);
        } else {
            list.add(() -> podiumLucuma);
        }
    }
}
