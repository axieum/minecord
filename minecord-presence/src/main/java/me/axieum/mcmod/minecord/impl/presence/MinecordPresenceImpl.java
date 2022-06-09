package me.axieum.mcmod.minecord.impl.presence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Timer;

import me.shedaniel.autoconfig.ConfigHolder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import me.axieum.mcmod.minecord.api.Minecord;
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
    // The name of the current presence category in use, if any
    private static @Nullable String curCategory = null;
    // The current timer that is responsible for scheduling presence updates
    private static @Nullable Timer timer = null;
    // True if the presence update task was previously started (but not necessarily running due to no category, etc.)
    private static boolean started = false;

    @Override
    public void onInitializeMinecord(JDABuilder builder)
    {
        LOGGER.info("Minecord Presence is getting ready...");

        // Update the current presence category throughout the lifecycle of the Minecraft server
        ServerLifecycleEvents.SERVER_STARTING.register(s -> useCategory("starting"));
        ServerLifecycleEvents.SERVER_STARTED.register(s -> useCategory("running"));
        ServerLifecycleEvents.SERVER_STOPPING.register(s -> useCategory("stopping"));

        // Start and stop the presence update task with the Discord client
        builder.addEventListeners(new ListenerAdapter()
        {
            @Override
            public void onReady(ReadyEvent event)
            {
                start();
            }

            @Override
            public void onShutdown(ShutdownEvent event)
            {
                stop();
            }
        });

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
        config.categories.forEach((name, entry) -> {
            // Instantiate a new presence category
            final PresenceCategory category = new PresenceCategory(entry.interval, entry.random);
            // Add all configured presence suppliers
            Arrays.stream(entry.presences)
                  .map(PresenceConfig.CategorySchema.PresenceSchema::getPresenceSupplier)
                  .forEach(category::addPresenceSuppliers);
            // Register (or overwrite) the newly created presence category
            LOGGER.info("Adding presence category '{}' with {} presences", name, category.size());
            MinecordPresence.getInstance().addCategory(name, category);
        });
    }

    @Override
    public void start()
    {
        // Stop an existing presence update task, if applicable
        stop();

        // Fetch the instance of JDA, if present
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            // Using the current presence category, if present
            getCategory().ifPresent(category -> {
                // Prepare a new timer and schedule the presence update task on it
                LOGGER.info(
                    "Scheduling Discord bot presence updates with category '{}' for every {} second(s)",
                    curCategory, category.getInterval()
                );
                timer = new Timer("Minecord-Presence-Timer", true);
                timer.schedule(new PresenceUpdateTask(jda, category), 0, category.getInterval() * 1000L);
            });
        });

        // Remember that the presence update task was started
        started = true;
    }

    @Override
    public void restart()
    {
        start(); // implicitly calls #stop()
    }

    @Override
    public void stop()
    {
        // Cancel the existing timer, if present
        if (timer != null) {
            LOGGER.debug("Unscheduling Discord bot presence updates");
            timer.cancel();
            timer = null;
        }

        // Remember that the presence update task was stopped
        started = false;
    }

    @Override
    public boolean isStarted()
    {
        return started;
    }

    @Override
    public Optional<PresenceCategory> getCategory()
    {
        return curCategory != null ? getCategory(curCategory) : Optional.empty();
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
    public MinecordPresence addCategory(@NotNull String name, @NotNull PresenceCategory category)
    {
        // Add the new category
        CATEGORIES.put(name, category);
        // Restart the presence update task if applicable
        if (started && curCategory != null && curCategory.equals(name)) restart();
        return this;
    }

    @Override
    public @Nullable PresenceCategory removeCategory(@NotNull String name)
    {
        // Remove the category by its name
        final @Nullable PresenceCategory category = CATEGORIES.remove(name);
        // Stop the presence update task if applicable
        if (started && curCategory != null && curCategory.equals(name)) stop();
        return category;
    }

    @Override
    public MinecordPresence useCategory(@NotNull String name)
    {
        // Check that the category is actually changing
        if (curCategory == null || !curCategory.equals(name)) {
            // Update the currently active category name
            curCategory = name;
            // Restart the presence update task if it's running
            if (started) restart();
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
