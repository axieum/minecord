package me.axieum.mcmod.minecord.api.presence;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.axieum.mcmod.minecord.api.presence.category.PresenceCategory;
import me.axieum.mcmod.minecord.impl.presence.MinecordPresenceImpl;

/**
 * A gateway into the Minecord Presence addon.
 */
public interface MinecordPresence
{
    /**
     * Returns the Minecord Presence instance.
     *
     * @return Minecord Presence instance
     */
    static MinecordPresence getInstance()
    {
        return MinecordPresenceImpl.INSTANCE;
    }

    /**
     * Starts the presence update task for the currently active category, if any.
     */
    void start();

    /**
     * Restarts the presence update task, if present.
     *
     * @see #stop()
     * @see #start()
     */
    void restart();

    /**
     * Stops the presence update task, if present.
     */
    void stop();

    /**
     * Returns whether the presence update task was last started or stopped.
     *
     * @return true if the presence update task was last started
     */
    boolean isStarted();

    /**
     * Returns the currently active presence category, if present.
     *
     * @return active category, if present
     */
    Optional<PresenceCategory> getCategory();

    /**
     * Returns the presence category with the given name, if present.
     *
     * @param name registered category name
     * @return active category, if present
     */
    Optional<PresenceCategory> getCategory(@NotNull String name);

    /**
     * Returns an immutable list of all registered presence categories.
     *
     * @return immutable list of categories
     */
    List<PresenceCategory> getCategories();

    /**
     * Returns true if a presence category with the given name exists.
     *
     * @param name registered category name
     * @return true if the presence category exist
     */
    boolean hasCategory(@NotNull String name);

    /**
     * Registers (or overwrites) a new presence category under the given name.
     *
     * <p>NB: If the category name is currently in use, the presence update
     * task is restarted.
     *
     * @param name     category name
     * @param category presence category
     * @return {@code this} for chaining
     * @see #restart()
     */
    MinecordPresence addCategory(@NotNull String name, @NotNull PresenceCategory category);

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
    @Nullable PresenceCategory removeCategory(@NotNull String name);

    /**
     * Sets the currently active presence category by its name, and restarts
     * the presence update task.
     *
     * <p>NB: If the category does not exist, the presence update task is
     * stopped.
     *
     * @param name registered category name
     * @return {@code this} for chaining
     * @see #restart()
     */
    MinecordPresence useCategory(@NotNull String name);
}
