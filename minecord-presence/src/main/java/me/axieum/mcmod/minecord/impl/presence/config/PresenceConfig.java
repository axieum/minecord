package me.axieum.mcmod.minecord.impl.presence.config;

import java.util.Optional;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
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
    @ConfigEntry.Category("Presence Categories")
    @Comment("A list of presence categories used to group presences together")
    public Category[] categories;

    /**
     * Presence category configuration schema.
     */
    public static class Category
    {
        public Category() {}

        public Category(String name, int interval, boolean random, PresenceEntry... presences)
        {
            this.name = name;
            this.interval = interval;
            this.random = random;
            if (presences != null) this.presences = presences;
        }

        @Comment("The name of the category")
        public String name;

        @Comment("The number of seconds between presence updates (at least 15s)")
        public int interval = 60;

        @Comment("True if presences should be chosen randomly, else round-robin")
        public boolean random = false;

        @ConfigEntry.Category("Presences")
        @Comment("A list of presences shown by the Discord bot while the category is active")
        public PresenceEntry[] presences = new PresenceEntry[] {};

        /**
         * Presence entry configuration schema.
         */
        public static class PresenceEntry
        {
            public PresenceEntry() {}

            public PresenceEntry(
                @Nullable Boolean idle, @Nullable OnlineStatus status, @Nullable ActivityEntry activity
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

            @ConfigEntry.Category("Activity")
            @Comment("If defined, overrides the game activity")
            public @Nullable ActivityEntry activity = null;

            /**
             * Presence activity configuration schema.
             */
            public static class ActivityEntry
            {
                public ActivityEntry() {}

                public ActivityEntry(ActivityType type, String name, @Nullable String url)
                {
                    this.type = type;
                    this.name = name;
                    this.url = url;
                }

                @Comment("""
                    The type of activity
                    Allowed values: COMPETING, DEFAULT, LISTENING, STREAMING and WATCHING""")
                public ActivityType type = ActivityType.DEFAULT;

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
                        return activity != null ? Optional.of(Activity.of(activity.type, activity.name, activity.url))
                                                : Optional.empty();
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
        // Add default presence categories
        categories = new Category[] {
            // Presences shown while the Minecraft server is starting
            new Category("starting", 60, false,
                // Watching Minecraft startup
                new Category.PresenceEntry(true, OnlineStatus.IDLE, new Category.PresenceEntry.ActivityEntry(
                    ActivityType.WATCHING, "Minecraft startup", null
                ))
            ),
            // Presences shown while the Minecraft server is running
            new Category("running", 60, true,
                // Playing Minecraft 1.17
                new Category.PresenceEntry(false, OnlineStatus.ONLINE, new Category.PresenceEntry.ActivityEntry(
                    ActivityType.DEFAULT, "Minecraft ${version}", null
                )),
                // Watching 2 player(s)
                new Category.PresenceEntry(false, OnlineStatus.ONLINE, new Category.PresenceEntry.ActivityEntry(
                    ActivityType.WATCHING, "${player_count} player(s)", null
                )),
                // Playing for 3 hours 24 minutes 10 seconds
                new Category.PresenceEntry(false, OnlineStatus.ONLINE, new Category.PresenceEntry.ActivityEntry(
                    ActivityType.DEFAULT, "for ${uptime}", null
                )),
                // Playing on hard mode
                new Category.PresenceEntry(false, OnlineStatus.ONLINE, new Category.PresenceEntry.ActivityEntry(
                    ActivityType.DEFAULT, "on ${difficulty} mode", null
                ))
            ),
            // Presences shown while the Minecraft server is stopping
            new Category("stopping", 60, false,
                // Watching Minecraft shutdown
                new Category.PresenceEntry(false, OnlineStatus.DO_NOT_DISTURB, new Category.PresenceEntry.ActivityEntry(
                    ActivityType.WATCHING, "Minecraft shutdown", null
                ))
            ),
        };
    }

    @Override
    public void validatePostLoad() throws ValidationException
    {
        // Validate each configured category
        for (Category category : categories) {
            // Check that the category name is non-empty
            if (category.name == null || category.name.isEmpty()) {
                throw new ValidationException("The presence category name must be non-empty!");
            }

            // Check that the update intervals are within reasonable bounds
            if (category.interval < 15) {
                LOGGER.warn(
                    "A Discord presence update interval shorter than 15 seconds will lead to rate-limits! "
                        + "Reverting category '{}' to 15s.",
                    category.name
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
