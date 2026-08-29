package com.github.millions_of_glazes.ice_and_fire_banquet.event.PodiumRenderer;

import com.github.alexthe666.iceandfire.block.BlockPodium;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import com.github.millions_of_glazes.ice_and_fire_banquet.api.IPodiumFacing;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "ice_and_fire_banquet", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PodiumPlacementHandler {

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player &&
                event.getPlacedBlock().getBlock() instanceof BlockPodium) {

            BlockEntity be = player.level().getBlockEntity(event.getPos());
            if (be instanceof TileEntityPodium podium) {
                // 获取玩家水平朝向（忽略上下）
                Direction facing = player.getDirection();
                int facingIndex = switch (facing) {
                    case SOUTH -> 0;
                    case WEST  -> 1;
                    case NORTH -> 2;
                    case EAST  -> 3;
                    default    -> 0; // 其他方向（上下）默认南方
                };
                ((IPodiumFacing) podium).setPodiumFacing(facingIndex);
            }
        }
    }
}
