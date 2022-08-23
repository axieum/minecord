package me.axieum.mcmod.minecord.impl.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

@Config(name = "minecord")
public class MinecordConfig extends PartitioningSerializer.GlobalData
{
    @Category("bot")
    public BotConfig bot = new BotConfig();

    @Category("i18n")
    public I18nConfig i18n = new I18nConfig();

    @Category("misc")
    public MiscConfig misc = new MiscConfig();

    /**
     * Registers and prepares a new configuration instance.
     *
     * @return registered config holder
     * @see AutoConfig#register(Class, ConfigSerializer.Factory)
     */
    public static ConfigHolder<MinecordConfig> init()
    {
        // Register the config
        ConfigHolder<MinecordConfig> holder = AutoConfig.register(
            MinecordConfig.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new)
        );

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) ->
            AutoConfig.getConfigHolder(MinecordConfig.class).load());

        return holder;
    }
}
