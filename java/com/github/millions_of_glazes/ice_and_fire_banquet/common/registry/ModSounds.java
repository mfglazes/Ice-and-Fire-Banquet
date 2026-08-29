package com.github.millions_of_glazes.ice_and_fire_banquet.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.github.millions_of_glazes.ice_and_fire_banquet.Ice_and_Fire_banquet;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Ice_and_Fire_banquet.MOD_ID);

    public static final RegistryObject<SoundEvent> MAIN_TITLE =
            registerSoundEvent("main_title");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(Ice_and_Fire_banquet.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus)  {
        SOUND_EVENTS.register(eventBus);
    }
}
