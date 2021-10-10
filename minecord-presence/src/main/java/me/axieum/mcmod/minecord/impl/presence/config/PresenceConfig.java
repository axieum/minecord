package me.axieum.mcmod.minecord.impl.presence.config;

import java.util.Optional;
import java.util.stream.Stream;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import me.axieum.mcmod.minecord.api.presence.PresenceSupplier;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.presence.MinecordPresenceImpl;
import static me.axieum.mcmod.minecord.impl.presence.MinecordPresenceImpl.LOGGER;

@Config(name = "minecord/presence")
public class PresenceConfig implements ConfigData
{
    @Category("Starting Presences")
    @Comment("A stage (or category) of presences shown while the Minecraft server is starting")
    public Stage starting = new Stage()
    {
        {
            random = false;
            presences = new PresenceEntry[] {
                // Watching Minecraft startup
                new PresenceEntry()
                {
                    {
                        idle = true;
                        status = OnlineStatus.IDLE;
                        activity.type = ActivityType.WATCHING;
                        activity.name = "Minecraft startup";
                    }
                },
            };
        }
    };

    @Category("Running Presences")
    @Comment("A stage (or category) of presences shown while the Minecraft server is running")
    public Stage running = new Stage()
    {
        {
            random = true;
            presences = new PresenceEntry[] {
                // Playing Minecraft 1.17
                new PresenceEntry()
                {
                    {
                        idle = false;
                        status = OnlineStatus.ONLINE;
                        activity.type = ActivityType.DEFAULT;
                        activity.name = "Minecraft ${version}";
                    }
                },
                // Watching 2 player(s)
                new PresenceEntry()
                {
                    {
                        idle = false;
                        status = OnlineStatus.ONLINE;
                        activity.type = ActivityType.WATCHING;
                        activity.name = "${player_count} player(s)";
                    }
                },
                // Playing for 3 hours 24 minutes 10 seconds
                new PresenceEntry()
                {
                    {
                        idle = false;
                        status = OnlineStatus.ONLINE;
                        activity.type = ActivityType.DEFAULT;
                        activity.name = "for ${uptime}";
                    }
                },
                // Playing on hard mode
                new PresenceEntry()
                {
                    {
                        idle = false;
                        status = OnlineStatus.ONLINE;
                        activity.type = ActivityType.DEFAULT;
                        activity.name = "on ${difficulty} mode";
                    }
                },
            };
        }
    };

    @Category("Stopping Presences")
    @Comment("A stage (or category) of presences shown while the Minecraft server is stopping")
    public Stage stopping = new Stage()
    {
        {
            random = false;
            presences = new PresenceEntry[] {
                // Watching Minecraft shutdown
                new PresenceEntry()
                {
                    {
                        idle = false;
                        status = OnlineStatus.DO_NOT_DISTURB;
                        activity.type = ActivityType.WATCHING;
                        activity.name = "Minecraft shutdown";
                    }
                },
            };
        }
    };

    /**
     * Presence stage configuration schema.
     */
    public static class Stage
    {
        @Comment("The number of seconds between presence updates (at least 15s)")
        public int interval = 60;

        @Comment("True if presences should be chosen randomly, else round-robin")
        public boolean random = false;

        @Category("Presences")
        @Comment("A list of presences shown by the Discord bot while the stage is active")
        public PresenceEntry[] presences = new PresenceEntry[] {};

        /**
         * Presence entry configuration schema.
         */
        public static class PresenceEntry
        {
            @Comment("If defined, overrides whether the bot is idling")
            public @Nullable Boolean idle = null;

            @Comment("""
                If defined, overrides the online status
                Allowed values: null, ONLINE, IDLE, DO_NOT_DISTURB, INVISIBLE and OFFLINE""")
            public @Nullable OnlineStatus status = null;

            @Category("Activity")
            @Comment("The activity displayed")
            public ActivityEntry activity = new ActivityEntry();

            /**
             * Presence activity configuration schema.
             */
            public static class ActivityEntry
            {
                @Comment("""
                    The type of activity
                    Allowed values: COMPETING, DEFAULT, LISTENING, STREAMING and WATCHING""")
                public ActivityType type = ActivityType.DEFAULT;

                @SuppressWarnings("checkstyle:linelength")
                @Comment("""
                    The name of the activity
                    Usages: ${version}, ${ip}, ${port}, ${motd}, ${difficulty}, ${max_players}, ${player_count} and ${uptime}""")
                public String name = "Minecraft ${version}";

                @Comment("If defined, provides a link to the activity, e.g. Twitch stream")
                public @Nullable String url = null;
            }

            /**
             * Builds and returns the presence.
             *
             * @return Minecord presence instance
             */
            public PresenceSupplier getPresenceSupplier()
            {
                return new PresenceSupplier()
                {
                    @Override
                    public Optional<OnlineStatus> getStatus()
                    {
                        return Optional.ofNullable(status);
                    }

                    @Override
                    public Optional<Boolean> isIdle()
                    {
                        return Optional.ofNullable(idle);
                    }

                    @Override
                    public @NotNull Activity getActivity(StringTemplate template)
                    {
                        return Activity.of(activity.type, activity.name, activity.url);
                    }
                };
            }
        }
    }

    @Override
    public void validatePostLoad()
    {
        // Check that the update intervals are within reasonable bounds
        Stream.of(starting, running, stopping).filter(stage -> stage.interval < 15).forEach(stage -> {
            LOGGER.warn(
                "A Discord presence update interval shorter than 15 seconds will lead to rate-limits! Reverting to 15s."
            );
            stage.interval = 15;
        });
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

        // Listen for when the config gets loaded
        holder.registerLoadListener((hld, cfg) -> {
            // Re-register all Minecord provided presences
            MinecordPresenceImpl.initPresences(cfg);
            return ActionResult.PASS;
        });

        return holder;
    }
}
