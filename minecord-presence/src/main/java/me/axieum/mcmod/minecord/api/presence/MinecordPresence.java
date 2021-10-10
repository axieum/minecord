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
     * Returns the Minecord Commands instance.
     *
     * @return Minecord Commands instance
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
     * Pauses the presence update task, if present.
     */
    void pause();

    /**
     * Resumes the presence update task, if present.
     */
    void resume();

    /**
     * Restarts the presence update task, if present.
     */
    void restart();

    /**
     * Stops the presence update task, if present.
     */
    void stop();

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
     * @param name     category name
     * @param category presence category
     * @return {@code this} for chaining
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
     * Sets the currently active presence category by its name.
     *
     * @param name registered category name
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if the category does not exist
     */
    MinecordPresence useCategory(@NotNull String name) throws IllegalArgumentException;
}
