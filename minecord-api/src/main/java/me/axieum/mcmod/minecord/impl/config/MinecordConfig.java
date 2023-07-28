package me.axieum.mcmod.minecord.impl.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Minecord partitioned configuration schema.
 */
@Config(name = "minecord")
public class MinecordConfig extends PartitioningSerializer.GlobalData
{
    /** Discord bot configuration. */
    @Category("bot")
    public BotConfig bot = new BotConfig();

    /** Minecord translations configuration. */
    @Category("i18n")
    public I18nConfig i18n = new I18nConfig();

    /** Minecord miscellaneous configuration. */
    @Category("misc")
    public MiscConfig misc = new MiscConfig();

    /**
     * Registers and loads a new configuration instance.
     *
     * @see AutoConfig#register(Class, ConfigSerializer.Factory)
     */
    public static void load()
    {
        // Register (and load) the config
        AutoConfig.register(MinecordConfig.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) ->
            AutoConfig.getConfigHolder(MinecordConfig.class).load());
    }
}
