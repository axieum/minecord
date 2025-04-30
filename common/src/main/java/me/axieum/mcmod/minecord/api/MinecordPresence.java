package me.axieum.mcmod.minecord.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Timer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.architectury.event.events.common.LifecycleEvent;

import me.axieum.mcmod.minecord.api.config.PresenceConfig;
import me.axieum.mcmod.minecord.api.presence.PresenceCategory;
import me.axieum.mcmod.minecord.api.presence.PresenceUpdateTask;
import static me.axieum.mcmod.minecord.api.Minecord.LOGGER;

/**
 * A gateway into the Minecord Presence addon.
 */
public final class MinecordPresence
{
    private MinecordPresence() {}

    /**
     * A mapping of presence category names to their implementation (initial capacity for all built-in categories).
     */
    private static final HashMap<String, PresenceCategory> CATEGORIES = new HashMap<>(3);

    /**
     * The name of the current presence category in use, if any.
     */
    private static @Nullable String curCategory = null;

    /**
     * The current timer that is responsible for scheduling presence updates.
     */
    private static @Nullable Timer timer = null;

    /**
     * True if the presence update task was previously started (but not necessarily running due to no category).
     */
    private static boolean started = false;

    /**
     * Initialises and registers all Minecord provided presence categories.
     */
    public static void init()
    {
        // Register each of the configured presence categories
        PresenceConfig.categories.forEach((name, entry) -> {
            // Instantiate a new presence category
            final PresenceCategory category = new PresenceCategory(entry.interval, entry.random);
            // Add all configured presence suppliers
            Arrays.stream(entry.presences)
                .map(PresenceConfig.Category.Presence::getPresenceSupplier)
                .forEach(category::addPresenceSuppliers);
            // Register (or overwrite) the newly created presence category
            LOGGER.info("Adding presence category '{}' with {} presences", name, category.size());
            addCategory(name, category);
        });

        // Register server lifecycle callbacks
        LifecycleEvent.SERVER_STARTING.register(server -> useCategory("starting"));
        LifecycleEvent.SERVER_STARTED.register(server -> useCategory("running"));
        LifecycleEvent.SERVER_STOPPING.register(server -> useCategory("stopping"));

        // Start bot presence updates
        start();
    }

    /**
     * Starts the presence update task for the currently active category, if any.
     */
    public static void start()
    {
        // Stop an existing presence update task, if applicable
        stop();

        // Fetch the instance of JDA, if present
        Minecord.getJDA().ifPresent(jda -> {
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

    /**
     * Restarts the presence update task, if present.
     *
     * @see #stop()
     * @see #start()
     */
    public static void restart()
    {
        start(); // implicitly calls #stop()
    }

    /**
     * Stops the presence update task, if present.
     */
    public static void stop()
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

    /**
     * Returns whether the presence update task was last started or stopped.
     *
     * @return true if the presence update task was last started
     */
    public static boolean isStarted()
    {
        return started;
    }

    /**
     * Returns the currently active presence category, if present.
     *
     * @return active category, if present
     */
    public static Optional<PresenceCategory> getCategory()
    {
        return curCategory != null ? getCategory(curCategory) : Optional.empty();
    }

    /**
     * Returns the presence category with the given name, if present.
     *
     * @param name registered category name
     * @return active category, if present
     */
    public static Optional<PresenceCategory> getCategory(@NotNull String name)
    {
        return Optional.ofNullable(CATEGORIES.get(name));
    }

    /**
     * Returns an immutable list of all registered presence categories.
     *
     * @return immutable list of categories
     */
    public static List<PresenceCategory> getCategories()
    {
        return List.copyOf(CATEGORIES.values());
    }

    /**
     * Returns true if a presence category with the given name exists.
     *
     * @param name registered category name
     * @return true if the presence category exists
     */
    public static boolean hasCategory(@NotNull String name)
    {
        return CATEGORIES.containsKey(name);
    }

    /**
     * Registers (or overwrites) a new presence category under the given name.
     *
     * <p>NB: If the category name is currently in use, the presence update
     * task is restarted.
     *
     * @param name     category name
     * @param category presence category
     * @see #restart()
     */
    public static void addCategory(@NotNull String name, @NotNull PresenceCategory category)
    {
        // Add the new category
        CATEGORIES.put(name, category);
        // Restart the presence update task if applicable
        if (started && curCategory != null && curCategory.equals(name)) restart();
    }

    /**
     * Removes a presence category by its name, if present.
     *
     * <p>NB: If the removed category is currently in use, the presence update
     * task is stopped.
     *
     * @param name category name
     * @return the removed category, if present
     * @see #stop()
     */
    public static @Nullable PresenceCategory removeCategory(@NotNull String name)
    {
        // Remove the category by its name
        final @Nullable PresenceCategory category = CATEGORIES.remove(name);
        // Stop the presence update task if applicable
        if (started && curCategory != null && curCategory.equals(name)) stop();
        return category;
    }

    /**
     * Sets the currently active presence category by its name, and restarts
     * the presence update task.
     *
     * <p>NB: If the category does not exist, the presence update task is
     * stopped.
     *
     * @param name registered category name
     * @see #restart()
     */
    public static void useCategory(@NotNull String name)
    {
        // Check that the category is actually changing
        if (curCategory == null || !curCategory.equals(name)) {
            // Update the currently active category name
            curCategory = name;
            // Restart the presence update task if it's running
            if (started) restart();
        }
    }
}
