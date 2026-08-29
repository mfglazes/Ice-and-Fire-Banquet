package com.github.millions_of_glazes.ice_and_fire_banquet;

import com.github.millions_of_glazes.ice_and_fire_banquet.client.EyeRenderHandler;
import com.github.millions_of_glazes.ice_and_fire_banquet.client.particle.LightningParticle;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.utility.PodiumBlockExtension;
import com.github.millions_of_glazes.ice_and_fire_banquet.config.GoldenEyeClientConfig;
import com.github.millions_of_glazes.ice_and_fire_banquet.effect.ghost.GhostTranslucentLayer;
import com.mojang.logging.LogUtils;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.block.ModBlocks;
import com.github.millions_of_glazes.ice_and_fire_banquet.client.renderer.DragonFireStoveRenderer;
import com.github.millions_of_glazes.ice_and_fire_banquet.client.renderer.DragonIceStoveRenderer;
import com.github.millions_of_glazes.ice_and_fire_banquet.client.renderer.DragonLightningStoveRenderer;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEffects;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModFluids;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModCreativeModeTabs;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.item.ModItems;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModEntities;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModParticleTypes;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModSounds;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.ModNetwork;
import com.github.millions_of_glazes.ice_and_fire_banquet.capability.DragonEyeData;
import com.github.millions_of_glazes.ice_and_fire_banquet.client.renderer.RenderGhostKnife;
import com.github.millions_of_glazes.ice_and_fire_banquet.config.Config;
import com.github.millions_of_glazes.ice_and_fire_banquet.event.CreativeTabInsertion;

@Mod(Ice_and_Fire_banquet.MOD_ID)
public class Ice_and_Fire_banquet {

    public static final String MOD_ID = "ice_and_fire_banquet";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Ice_and_Fire_banquet() {
        @SuppressWarnings("deprecation")
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        init(context);
    }

    public Ice_and_Fire_banquet(FMLJavaModLoadingContext context) {
        init(context);
    }

    private void init(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModSounds.register(modEventBus);
        ModParticleTypes.PARTICLES.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModEntities.BLOCK_ENTITIES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::registerCapabilities);

        ModNetwork.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // 注册客户端配置（黄金瞳渲染）
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GoldenEyeClientConfig.SPEC, "ice_and_fire_banquet-goldeneye-client.toml");
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonSetup {
        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(CreativeTabInsertion::insertCannoliAfterTrollBoots);
            event.enqueueWork(CreativeTabInsertion::insertPodiumLucumaAfterAcacia);
            event.enqueueWork(() -> {PodiumBlockExtension.addPodiumBlock(ModBlocks.PODIUM_LUCUMA.get());});
        }
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(DragonEyeData.class);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientSetup {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(
                    ModEntities.DRAGON_ICE_STOVE.get(),
                    DragonIceStoveRenderer::new
            );
            event.registerBlockEntityRenderer(
                    ModEntities.DRAGON_LIGHTNING_STOVE.get(),
                    DragonLightningStoveRenderer::new
            );
            event.registerBlockEntityRenderer(
                    ModEntities.DRAGON_FIRE_STOVE.get(),
                    DragonFireStoveRenderer::new
            );
            event.registerEntityRenderer(
                    ModEntities.GHOST_KNIFE.get(),
                    RenderGhostKnife::new
            );
        }

        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(
                    ModParticleTypes.DRAGON_ICE_FLAME.get(),
                    FlameParticle.Provider::new
            );
            event.registerSpriteSet(
                    ModParticleTypes.DRAGON_FIRE_FLAME.get(),
                    FlameParticle.Provider::new
            );
            event.registerSpriteSet(
                    ModParticleTypes.DRAGON_LIGHTNING_FLAME.get(),
                    FlameParticle.Provider::new
            );

            event.registerSpriteSet(
                    ModParticleTypes.LIGHTNING.get(),
                    LightningParticle.Provider::new
            );
        }

        @SubscribeEvent
        public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
            // 配置已加载，应用黄金瞳设置
            GoldenEyeClientConfig.applyConfig();

            // 为默认皮肤和纤细皮肤渲染器添加黄金瞳层和幽灵层
            addLayerToSkin(event, "default");
            addLayerToSkin(event, "slim");
        }

        private static void addLayerToSkin(EntityRenderersEvent.AddLayers event, String skinType) {
            var renderer = event.getSkin(skinType);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new EyeRenderHandler(playerRenderer));
                playerRenderer.addLayer(new GhostTranslucentLayer(playerRenderer));
            }
        }
    }
}
