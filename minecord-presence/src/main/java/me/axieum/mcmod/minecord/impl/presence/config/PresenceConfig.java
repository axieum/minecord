package me.axieum.mcmod.minecord.impl.presence.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import static me.axieum.mcmod.minecord.impl.presence.MinecordPresenceImpl.LOGGER;

@Config(name = "minecord/presence")
public class PresenceConfig implements ConfigData
{
    @Comment("The number of seconds between presence updates (at least 15s)")
    public int interval = 60;

    @Comment("True if the presences should be chosen randomly, else round-robin")
    public boolean random = false;

    @Category("Starting Presences")
    @Comment("A list of presences shown by the Discord bot while the server is starting")
    @SuppressWarnings("checkstyle:leftcurly")
    public PresenceEntry[] starting = {
        new PresenceEntry() {{ type = ActivityType.WATCHING; value = "Minecraft loading"; }},
    };

    @Category("Running Presences")
    @Comment("A list of presences shown by the Discord bot while the server is running")
    @SuppressWarnings("checkstyle:leftcurly")
    public PresenceEntry[] running = {
        new PresenceEntry() {{ type = ActivityType.DEFAULT; value = "Minecraft ${version}"; }},
        new PresenceEntry() {{ type = ActivityType.WATCHING; value = "${player_count} player(s)"; }},
        new PresenceEntry() {{ type = ActivityType.DEFAULT; value = "for ${uptime}"; }},
        new PresenceEntry() {{ type = ActivityType.DEFAULT; value = "on ${difficulty}"; }},
    };

    @Category("Stopping Presences")
    @Comment("A list of presences shown by the Discord bot while the server is stopping")
    @SuppressWarnings("checkstyle:leftcurly")
    public PresenceEntry[] stopping = {
        new PresenceEntry() {{ type = ActivityType.WATCHING; value = "Minecraft stopping"; }},
    };

    public static class PresenceEntry
    {
        @Comment("""
            The type of presence
            Allowed values: COMPETING, DEFAULT, LISTENING, STREAMING and WATCHING""")
        public ActivityType type = ActivityType.DEFAULT;

        @SuppressWarnings("checkstyle:linelength")
        @Comment("""
            The text value that is put on display
            Usages: ${version}, ${ip}, ${port}, ${motd}, ${difficulty}, ${max_players}, ${player_count and ${uptime}""")
        public String value = "Minecraft ${version}";

        @Comment("If defined, sets the URL of the underlying media, e.g. Twitch stream")
        public @Nullable String url = null;
    }

    @Override
    public void validatePostLoad()
    {
        // Check that the interval is within reasonable bounds
        if (interval < 15) {
            LOGGER.warn(
                "A Discord presence update interval shorter than 15 seconds will lead to rate-limits! Reverting to 15s."
            );
            interval = 15;
        }
    }

    /**
     * Registers and prepares a new configuration instance.
     *
     * @return registered config holder
     * @see AutoConfig#register(Class, ConfigSerializer.Factory)
     */
    public static ConfigHolder<PresenceConfig> init()
    {
        // Register the config
        ConfigHolder<PresenceConfig> holder = AutoConfig.register(PresenceConfig.class, JanksonConfigSerializer::new);

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) ->
            AutoConfig.getConfigHolder(PresenceConfig.class).load());

        return holder;
    }
}
