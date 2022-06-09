package me.axieum.mcmod.minecord.impl.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.RequiresRestart;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.dv8tion.jda.api.OnlineStatus;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

@Config(name = "minecord/bot")
public class MinecordConfig implements ConfigData
{
    @Comment("Token used to authenticate against your Discord bot")
    @RequiresRestart
    public String token = "";

    @Category("Bot Status")
    @Comment("Bot statuses relayed during the lifecycle of the server")
    public StatusSchema status = new StatusSchema();

    /**
     * Bot status configuration schema.
     */
    public static class StatusSchema
    {
        @Comment("Status while the server is starting")
        public OnlineStatus starting = OnlineStatus.IDLE;

        @Comment("Status after the server has started")
        public OnlineStatus started = OnlineStatus.ONLINE;

        @Comment("Status while the server is stopping")
        public OnlineStatus stopping = OnlineStatus.DO_NOT_DISTURB;

        @Comment("Status after the server has stopped")
        public OnlineStatus stopped = OnlineStatus.OFFLINE;
    }

    @Comment("True if all guild members should be cached, in turn allowing @mentions\n"
        + "NB: This requires the Privileged Gateway Intent 'Server Members' to be enabled on your Discord bot!")
    @RequiresRestart
    public boolean cacheMembers = false;

    /**
     * Registers and prepares a new configuration instance.
     *
     * @return registered config holder
     * @see me.shedaniel.autoconfig.AutoConfig#register(Class, ConfigSerializer.Factory)
     */
    public static ConfigHolder<MinecordConfig> init()
    {
        // Register the config
        ConfigHolder<MinecordConfig> holder = AutoConfig.register(MinecordConfig.class, JanksonConfigSerializer::new);

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) ->
            AutoConfig.getConfigHolder(MinecordConfig.class).load());

        return holder;
    }
}
