package me.axieum.mcmod.minecord.impl.avatars.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

@Config(name = "minecord/avatars")
public class AvatarConfig implements ConfigData
{
    /**
     * Registers and prepares a new configuration instance.
     *
     * @return registered config holder
     * @see AutoConfig#register(Class, ConfigSerializer.Factory)
     */
    public static ConfigHolder<AvatarConfig> init()
    {
        // Register the config
        ConfigHolder<AvatarConfig> holder = AutoConfig.register(AvatarConfig.class, JanksonConfigSerializer::new);

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) ->
            AutoConfig.getConfigHolder(AvatarConfig.class).load());

        return holder;
    }
}
