package com.github.millions_of_glazes.ice_and_fire_banquet.event.PodiumRenderer;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.world.item.ItemStack;
import com.github.alexthe666.iceandfire.client.render.tile.RenderPodium;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;

@Cancelable
public class PodiumRenderEvent extends Event {
    private final RenderPodium<?> renderer;
    private final TileEntityPodium podium;
    private final float partialTicks;
    private final int light;
    private final int overlay;

    public PodiumRenderEvent(RenderPodium<?> renderer, TileEntityPodium podium, float partialTicks, int light, int overlay) {
        this.renderer = renderer;
        this.podium = podium;
        this.partialTicks = partialTicks;
        this.light = light;
        this.overlay = overlay;
    }

    public RenderPodium<?> getRenderer() { return renderer; }
    public TileEntityPodium getPodium() { return podium; }
    public ItemStack getItemStack() { return podium.getItem(0); }
    public float getPartialTicks() { return partialTicks; }
    public int getLight() { return light; }
    public int getOverlay() { return overlay; }
}
