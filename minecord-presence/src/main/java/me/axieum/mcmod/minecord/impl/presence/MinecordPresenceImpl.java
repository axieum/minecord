package me.axieum.mcmod.minecord.impl.presence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;

import me.shedaniel.autoconfig.ConfigHolder;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
            removeCategory(c.name);
            // Instantiate a new presence category
            final PresenceCategory category = new PresenceCategory(c.interval, c.random);
            // Add all configured presence suppliers
            Arrays.stream(c.presences)
                  .map(PresenceConfig.Category.PresenceEntry::getPresenceSupplier)
                  .forEach(category::addPresenceSuppliers);
            // Register the newly created presence category
            addCategory(c.name, category);
        });
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
