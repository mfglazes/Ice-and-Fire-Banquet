package com.github.millions_of_glazes.ice_and_fire_banquet.event;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ice_and_Fire_banquet.MOD_ID)
public class AdvancementEvents {

    /**
     * 玩家拾取物品时立即检测。
     */
    @SubscribeEvent
    public static void onItemPickup(PlayerEvent.ItemPickupEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        checkCookeryBook(player, event.getStack());
    }

    /**
     * 每秒扫描背包，覆盖命令/合成等直接获得的情况。
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;
        if (event.player.tickCount % 20 != 0) return; // 每秒一次
        for (ItemStack stack : event.player.getInventory().items) {
            checkCookeryBook(event.player, stack);
        }
    }

    private static void checkCookeryBook(Player player, ItemStack stack) {
        if (stack.getItem() != IafItemRegistry.BESTIARY.get()) return;

        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        // 获取已解锁页面的整数数组
        int[] pages = tag.getIntArray("Pages");
        for (int page : pages) {
            // COOKERY 的 ordinal 是 27（原版枚举 0~26，新增后为 27）
            if (page == 27) {
                grantAdvancement(player, "banquet/root", "has_bestiary");
                return;
            }
        }
    }

    private static void grantAdvancement(Player player, String advancementPath, String criterion) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        Advancement advancement = serverPlayer.getServer()
                .getAdvancements()
                .getAdvancement(new ResourceLocation(Ice_and_Fire_banquet.MOD_ID, advancementPath));

        if (advancement != null) {
            serverPlayer.getAdvancements().award(advancement, criterion);
        }
    }
}