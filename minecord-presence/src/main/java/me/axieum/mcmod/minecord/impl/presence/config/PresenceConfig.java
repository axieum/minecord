package me.axieum.mcmod.minecord.impl.presence.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import me.axieum.mcmod.minecord.api.presence.category.PresenceSupplier;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.presence.MinecordPresenceImpl;
import static me.axieum.mcmod.minecord.impl.presence.MinecordPresenceImpl.LOGGER;

/**
 * Minecord Presence configuration schema.
 */
@Config(name = "minecord/presence")
public class PresenceConfig implements ConfigData
{
    /** A collection of presence categories used to group presences together. */
    @Category("Presence Categories")
    @Comment("A collection of presence categories used to group presences together")
    public HashMap<String, CategorySchema> categories = new HashMap<>(3);

    /**
     * Presence category configuration schema.
     */
    public static class CategorySchema
    {
        /** Constructs a new empty Discord presence category config. */
        public CategorySchema() {}

        /**
         * Constructs a new Discord presence category config.
         *
         * @param interval number of seconds between presence updates (at least 15s)
         * @param random true if presences should be chosen randomly, else round-robin
         * @param presences a list of presence configs shown by the Discord bot while the category is active
         */
        public CategorySchema(int interval, boolean random, PresenceSchema... presences)
        {
            this.interval = interval;
            this.random = random;
            if (presences != null) this.presences = presences;
        }

        /** The number of seconds between presence updates (at least 15s). */
        @Comment("The number of seconds between presence updates (at least 15s)")
        public int interval = 60;

        /** True if presences should be chosen randomly, else round-robin. */
        @Comment("True if presences should be chosen randomly, else round-robin")
        public boolean random = false;

        /** A list of presences shown by the Discord bot while the category is active. */
        @Category("Presences")
        @Comment("A list of presences shown by the Discord bot while the category is active")
        public PresenceSchema[] presences = new PresenceSchema[] {};

        /**
         * Presence entry configuration schema.
         */
        public static class PresenceSchema
        {
            /** Constructs a new empty Discord presence config. */
            public PresenceSchema() {}

            /**
             * Constructs a new Discord presence config.
             *
             * @param idle true if the bot is idle, false for active, or null for default
             * @param status bot status
             * @param activity bot activity
             */
            public PresenceSchema(
                @Nullable Boolean idle, @Nullable OnlineStatus status, @Nullable ActivitySchema activity
            )
            {
                this.idle = idle;
                this.status = status;
                this.activity = activity;
            }

            /** If defined, overrides whether the bot is idling. */
            @Comment("If defined, overrides whether the bot is idling")
            public @Nullable Boolean idle = null;

            /**
             * If defined, overrides the online status.
             *
             * <p>Allowed values: {@code null}, {@code ONLINE}, {@code IDLE},
             * {@code DO_NOT_DISTURB}, {@code INVISIBLE} and {@code OFFLINE}.
             */
            @Comment("""
                If defined, overrides the online status
                Allowed values: null, ONLINE, IDLE, DO_NOT_DISTURB, INVISIBLE and OFFLINE""")
            public @Nullable OnlineStatus status = null;

            /** If defined, overrides the game activity. */
            @Category("Activity")
            @Comment("If defined, overrides the game activity")
            public @Nullable ActivitySchema activity = null;

            /** Presence activity configuration schema. */
            public static class ActivitySchema
            {
                /** Constructs a new empty Discord presence activity config. */
                public ActivitySchema() {}

                /**
                 * Constructs a new Discord presence activity config.
                 *
                 * @param type type of activity
                 * @param name name of the activity
                 * @param url optional link to the activity, e.g. Twitch stream
                 */
                public ActivitySchema(ActivityType type, String name, @Nullable String url)
                {
                    this.type = type;
                    this.name = name;
                    this.url = url;
                }

                /**
                 * The type of activity.
                 *
                 * <p>Allowed values: {@code COMPETING}, {@code LISTENING}, {@code PLAYING},
                 * {@code STREAMING} and {@code WATCHING}.
                 */
                @Comment("""
                    The type of activity
                    Allowed values: COMPETING, LISTENING, PLAYING, STREAMING and WATCHING""")
                public ActivityType type = ActivityType.PLAYING;

                /**
                 * The name of the activity.
                 *
                 * <p>Usages: {@code ${version}}, {@code ${ip}}, {@code ${port}}, {@code ${motd}},
                 * {@code ${difficulty}}, {@code ${max_players}}, {@code ${player_count}} and {@code ${uptime}}.
                 */
                @SuppressWarnings("checkstyle:linelength")
                @Comment("""
                    The name of the activity
                    Usages: ${version}, ${ip}, ${port}, ${motd}, ${difficulty}, ${max_players}, ${player_count} and ${uptime}""")
                public String name = "Minecraft";

                /** If defined, provides a link to the activity, e.g. Twitch stream. */
                @Comment("If defined, provides a link to the activity, e.g. Twitch stream")
                public @Nullable String url = null;
            }

            /**
             * Builds and returns the presence supplier.
             *
             * @return presence supplier
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
                    public Optional<Activity> getActivity(StringTemplate template)
                    {
                        return activity != null ? Optional.of(
                            Activity.of(activity.type, template.format(activity.name), activity.url)
                        ) : Optional.empty();
                    }
                };
            }
        }
    }

    /**
     * Constructs a new presence configuration with appropriate defaults.
     */
    public PresenceConfig()
    {
        // Add a default presence category to be used while the Minecraft server is starting
        categories.put("starting", new CategorySchema(60, false,
            // Watching Minecraft startup
            new CategorySchema.PresenceSchema(true, OnlineStatus.IDLE,
                new CategorySchema.PresenceSchema.ActivitySchema(
                    ActivityType.WATCHING, "Minecraft startup", null
                )
            )
        ));

        // Add a default presence category to be used while the Minecraft server is running
        categories.put("running", new CategorySchema(60, true,
            // Playing Minecraft 1.17
            new CategorySchema.PresenceSchema(false, OnlineStatus.ONLINE,
                new CategorySchema.PresenceSchema.ActivitySchema(
                    ActivityType.PLAYING, "Minecraft ${version}", null
                )
            ),
            // Watching 2 player(s)
            new CategorySchema.PresenceSchema(false, OnlineStatus.ONLINE,
                new CategorySchema.PresenceSchema.ActivitySchema(
                    ActivityType.WATCHING, "${player_count} player(s)", null
                )
            ),
            // Playing for 3 hours 24 minutes 10 seconds
            new CategorySchema.PresenceSchema(false, OnlineStatus.ONLINE,
                new CategorySchema.PresenceSchema.ActivitySchema(
                    ActivityType.PLAYING, "for ${uptime}", null
                )
            ),
            // Playing on hard mode
            new CategorySchema.PresenceSchema(false, OnlineStatus.ONLINE,
                new CategorySchema.PresenceSchema.ActivitySchema(
                    ActivityType.PLAYING, "on ${difficulty} mode", null
                )
            )
        ));

        // Add a default presence category to be used while the Minecraft server is stopping
        categories.put("stopping", new CategorySchema(60, false,
            // Watching Minecraft shutdown
            new CategorySchema.PresenceSchema(false, OnlineStatus.DO_NOT_DISTURB,
                new CategorySchema.PresenceSchema.ActivitySchema(
                    ActivityType.WATCHING, "Minecraft shutdown", null
                )
            )
        ));
    }

    @Override
    public void validatePostLoad() throws ValidationException
    {
        // Validate each configured category
        for (Map.Entry<String, CategorySchema> entry : categories.entrySet()) {
            final String name = entry.getKey();
            final CategorySchema category = entry.getValue();

            // Check that the category name is non-empty
            if (name == null || name.isEmpty()) {
                throw new ValidationException("The presence category name must be non-empty!");
            }

            // Check that the update intervals are within reasonable bounds
            if (category.interval < 15) {
                LOGGER.warn(
                    "A Discord presence update interval shorter than 15 seconds will lead to rate-limits! "
                        + "Reverting category '{}' to 15s.",
                    name
                );
                category.interval = 15;
            }
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

        // Listen for when the config gets loaded
        holder.registerLoadListener((hld, cfg) -> {
            // Re-register all Minecord provided presences
            MinecordPresenceImpl.initPresenceCategories(cfg);
            return ActionResult.PASS;
        });

        return holder;
    }
}
