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

@Config(name = "minecord/presence")
public class PresenceConfig implements ConfigData
{
    @Category("Presence Categories")
    @Comment("A collection of presence categories used to group presences together")
    public HashMap<String, CategorySchema> categories = new HashMap<>(3);

    /**
     * Presence category configuration schema.
     */
    public static class CategorySchema
    {
        public CategorySchema() {}

        public CategorySchema(int interval, boolean random, PresenceSchema... presences)
        {
            this.interval = interval;
            this.random = random;
            if (presences != null) this.presences = presences;
        }

        @Comment("The number of seconds between presence updates (at least 15s)")
        public int interval = 60;

        @Comment("True if presences should be chosen randomly, else round-robin")
        public boolean random = false;

        @Category("Presences")
        @Comment("A list of presences shown by the Discord bot while the category is active")
        public PresenceSchema[] presences = new PresenceSchema[] {};

        /**
         * Presence entry configuration schema.
         */
        public static class PresenceSchema
        {
            public PresenceSchema() {}

            public PresenceSchema(
                @Nullable Boolean idle, @Nullable OnlineStatus status, @Nullable ActivitySchema activity
            )
            {
                this.idle = idle;
                this.status = status;
                this.activity = activity;
            }

            @Comment("If defined, overrides whether the bot is idling")
            public @Nullable Boolean idle = null;

            @Comment("""
                If defined, overrides the online status
                Allowed values: null, ONLINE, IDLE, DO_NOT_DISTURB, INVISIBLE and OFFLINE""")
            public @Nullable OnlineStatus status = null;

            @Category("Activity")
            @Comment("If defined, overrides the game activity")
            public @Nullable ActivitySchema activity = null;

            /**
             * Presence activity configuration schema.
             */
            public static class ActivitySchema
            {
                public ActivitySchema() {}

                public ActivitySchema(ActivityType type, String name, @Nullable String url)
                {
                    this.type = type;
                    this.name = name;
                    this.url = url;
                }

                @Comment("""
                    The type of activity
                    Allowed values: COMPETING, LISTENING, PLAYING, STREAMING and WATCHING""")
                public ActivityType type = ActivityType.PLAYING;

                @SuppressWarnings("checkstyle:linelength")
                @Comment("""
                    The name of the activity
                    Usages: ${version}, ${ip}, ${port}, ${motd}, ${difficulty}, ${max_players}, ${player_count} and ${uptime}""")
                public String name = "Minecraft";

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
