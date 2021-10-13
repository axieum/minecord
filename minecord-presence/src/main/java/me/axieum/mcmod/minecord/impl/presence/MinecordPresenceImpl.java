package me.axieum.mcmod.minecord.impl.presence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Timer;

import me.shedaniel.autoconfig.ConfigHolder;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.axieum.mcmod.minecord.api.addon.MinecordAddon;
import me.axieum.mcmod.minecord.api.presence.MinecordPresence;
import me.axieum.mcmod.minecord.api.presence.category.PresenceCategory;
import me.axieum.mcmod.minecord.impl.presence.config.PresenceConfig;

public final class MinecordPresenceImpl implements MinecordPresence, MinecordAddon
{
    public static final MinecordPresence INSTANCE = new MinecordPresenceImpl();
    public static final Logger LOGGER = LogManager.getLogger("Minecord|Presence");
    private static final ConfigHolder<PresenceConfig> CONFIG = PresenceConfig.init();

    // A mapping of presence category names to their implementation (initial capacity for all built-in categories)
    private static final HashMap<String, PresenceCategory> CATEGORIES = new HashMap<>(3);
    // The current presence category in use, if any
    private static @Nullable String category = null;
    // The current timer that is responsible for scheduling presence updates
    private static @Nullable Timer timer = null;

    @Override
    public void onInitializeMinecord(JDABuilder builder)
    {
        LOGGER.info("Minecord Presence is getting ready...");

        // todo: Update the current presence category throughout the lifecycle of the Minecraft server

        // todo: Start and stop the presence update task with the Discord client

        // Register all Minecord provided presence categories
        initPresenceCategories(getConfig());
    }

    /**
     * Initialises and registers all Minecord provided presence categories.
     *
     * @param config presence config
     */
    public static void initPresenceCategories(PresenceConfig config)
    {
        // Register each of the configured presence categories
        Arrays.stream(config.categories).forEach(c -> {
            // Unregister the category if it already exists
            MinecordPresence.getInstance().removeCategory(c.name);
            // Instantiate a new presence category
            final PresenceCategory category = new PresenceCategory(c.interval, c.random);
            // Add all configured presence suppliers
            Arrays.stream(c.presences)
                  .map(PresenceConfig.Category.PresenceEntry::getPresenceSupplier)
                  .forEach(category::addPresenceSuppliers);
            // Register the newly created presence category
            MinecordPresence.getInstance().addCategory(c.name, category);
        });
    }

    @Override
    public void start()
    {
        // todo
    }

    @Override
    public void pause()
    {
        // todo
    }

    @Override
    public void resume()
    {
        // todo
    }

    @Override
    public void restart()
    {
        // todo
    }

    @Override
    public void stop()
    {
        // todo
    }

    @Override
    public Optional<PresenceCategory> getCategory()
    {
        return category != null ? getCategory(category) : Optional.empty();
    }

    @Override
    public Optional<PresenceCategory> getCategory(@NotNull String name)
    {
        return Optional.ofNullable(CATEGORIES.get(name));
    }

    @Override
    public List<PresenceCategory> getCategories()
    {
        return List.copyOf(CATEGORIES.values());
    }

    @Override
    public boolean hasCategory(@NotNull String name)
    {
        return CATEGORIES.containsKey(name);
    }

    @Override
    public MinecordPresence addCategory(@NotNull String name, @NotNull PresenceCategory cat)
    {
        // Add the new category
        CATEGORIES.put(name, cat);
        // Restart the presence update task if applicable
        if (category != null && category.equals(name)) restart();
        return this;
    }

    @Override
    public @Nullable PresenceCategory removeCategory(@NotNull String name)
    {
        // Remove the category by its name
        final @Nullable PresenceCategory cat = CATEGORIES.remove(name);
        // Stop the presence update task if applicable
        if (category != null && category.equals(name)) stop();
        return cat;
    }

    @Override
    public MinecordPresence useCategory(@NotNull String name) throws IllegalArgumentException
    {
        // Check that the category is actually changing
        if (category == null || !category.equals(name)) {
            // Update the currently active category name
            category = name;
            // Restart the presence update task
            restart();
        }
        return this;
    }

    /**
     * Returns the Minecord Presence config instance.
     *
     * @return config instance
     */
    public static PresenceConfig getConfig()
    {
        return CONFIG.getConfig();
    }
}
