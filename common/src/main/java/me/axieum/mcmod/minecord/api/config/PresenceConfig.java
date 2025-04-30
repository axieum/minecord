package me.axieum.mcmod.minecord.api.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject;
import eu.pb4.placeholders.api.node.TextNode;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import org.jetbrains.annotations.Nullable;

import me.axieum.mcmod.minecord.api.presence.PresenceSupplier;
import static me.axieum.mcmod.minecord.api.Minecord.LOGGER;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.parseNode;

/**
 * Minecord Presence configuration schema.
 */
@Category(value = "presence")
@ConfigInfo(
    titleTranslation = "text.rconfig.minecord.presence.title",
    descriptionTranslation = "text.rconfig.minecord.presence.description"
)
public final class PresenceConfig
{
    private PresenceConfig() {}

    /** A collection of presence categories used to group presences together. */
    @ConfigEntry(id = "categories")
    public static HashMap<String, Category> categories = new HashMap<>(3);

    /**
     * Presence category configuration schema.
     */
    @ConfigObject
    public static class Category
    {
        /** Constructs a new empty Discord presence category config. */
        public Category() {}

        /**
         * Constructs a new Discord presence category config.
         *
         * @param interval number of seconds between presence updates (at least 15s)
         * @param random true if presences should be chosen randomly, else round-robin
         * @param presences a list of presence configs shown by the Discord bot while the category is active
         */
        public Category(int interval, boolean random, Presence... presences)
        {
            this.interval = interval;
            this.random = random;
            if (presences != null) this.presences = presences;
        }

        /** The number of seconds between presence updates (at least 15s). */
        @ConfigEntry(id = "interval")
        public int interval = 60;

        /** True if presences should be chosen randomly, else round-robin. */
        @ConfigEntry(id = "random")
        public boolean random = false;

        /** A list of presences shown by the Discord bot while the category is active. */
        @ConfigEntry(id = "presences")
        public Presence[] presences = new Presence[] {};

        /**
         * Presence entry configuration schema.
         */
        @ConfigObject
        public static class Presence
        {
            /**
             * Constructs a new empty Discord presence config.
             */
            public Presence() {}

            /**
             * Constructs a new Discord presence config.
             *
             * @param idle     true if the bot is idle, false for active, or null for default
             * @param status   bot status
             * @param activity bot activity
             */
            public Presence(@Nullable Boolean idle, @Nullable OnlineStatus status, @Nullable Activity activity)
            {
                this.idle = idle;
                this.status = status;
                this.activity = activity;
            }

            /**
             * If defined, overrides whether the bot is idling.
             */
            @ConfigEntry(id = "idle")
            public @Nullable Boolean idle = null;

            /**
             * If defined, overrides the online status.
             */
            @ConfigEntry(id = "status")
            public @Nullable OnlineStatus status = null;

            /**
             * If defined, overrides the game activity.
             */
            @ConfigEntry(id = "activity")
            public @Nullable Activity activity = null;

            /**
             * Presence activity configuration schema.
             */
            @ConfigObject
            public static class Activity
            {
                /**
                 * Constructs a new empty Discord presence activity config.
                 */
                public Activity() {}

                /**
                 * Constructs a new Discord presence activity config.
                 *
                 * @param type type of activity
                 * @param name name of the activity
                 * @param url  optional link to the activity, e.g. Twitch stream
                 */
                public Activity(ActivityType type, String name, @Nullable String url)
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
                @ConfigEntry(id = "type")
                public ActivityType type = ActivityType.PLAYING;

                /**
                 * The name of the activity.
                 *
                 * <ul>
                 *   <li>{@code ${uptime [format]}} &mdash; the total process uptime (to the nearest minute)</li>
                 * </ul>
                 */
                @ConfigEntry(id = "name")
                @SuppressWarnings("checkstyle:linelength")
                public String name = "Minecraft";

                /**
                 * Pre-parsed 'name' text node.
                 */
                public TextNode nameNode;

                /**
                 * If defined, provides a link to the activity, e.g. Twitch stream.
                 */
                @ConfigEntry(id = "url")
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
                    public Optional<net.dv8tion.jda.api.entities.Activity> getActivity(
                        Function<TextNode, String> nameMutator
                    )
                    {
                        return Optional.ofNullable(activity).map(activity ->
                            net.dv8tion.jda.api.entities.Activity.of(
                                activity.type, nameMutator.apply(activity.nameNode), activity.url));
                    }
                };
            }
        }
    }

    static {
        // Add a default presence category to be used while the Minecraft server is starting
        categories.put("starting", new Category(60, false,
            // Watching Minecraft startup
            new Category.Presence(true, OnlineStatus.IDLE,
                new Category.Presence.Activity(
                    ActivityType.WATCHING, "Minecraft startup", null
                )
            )
        ));

        // Add a default presence category to be used while the Minecraft server is running
        categories.put("running", new Category(60, true,
            // Playing Minecraft 1.17
            new Category.Presence(false, OnlineStatus.ONLINE,
                new Category.Presence.Activity(
                    ActivityType.PLAYING, "Minecraft ${server:version}", null
                )
            ),
            // Watching 2 player(s)
            new Category.Presence(false, OnlineStatus.ONLINE,
                new Category.Presence.Activity(
                    ActivityType.WATCHING, "${server:online} player(s)", null
                )
            ),
            // Playing for 3 hours 24 minutes 10 seconds
            new Category.Presence(false, OnlineStatus.ONLINE,
                new Category.Presence.Activity(
                    ActivityType.PLAYING, "for ${uptime}", null
                )
            )
        ));

        // Add a default presence category to be used while the Minecraft server is stopping
        categories.put("stopping", new Category(60, false,
            // Watching Minecraft shutdown
            new Category.Presence(false, OnlineStatus.DO_NOT_DISTURB,
                new Category.Presence.Activity(
                    ActivityType.WATCHING, "Minecraft shutdown", null
                )
            )
        ));
    }

    /**
     * Validates the configuration.
     */
    public static void validate() throws IllegalArgumentException
    {
        // Validate each configured category
        for (Map.Entry<String, Category> entry : categories.entrySet()) {
            final String name = entry.getKey();
            final Category category = entry.getValue();

            // Check that the category name is non-empty
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("The presence category name must be non-empty!");
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

            // Parse presence templates
            Arrays.stream(category.presences)
                .filter(presence -> presence.activity != null)
                .forEach(presence -> presence.activity.nameNode = parseNode(presence.activity.name));
        }
    }
}
