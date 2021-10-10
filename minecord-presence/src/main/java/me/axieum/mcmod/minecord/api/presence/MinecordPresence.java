package me.axieum.mcmod.minecord.api.presence;

import java.util.List;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * Starts (or restarts) the Discord bot presence update task.
     *
     * @param jda      JDA client
     * @param interval number of seconds between presence updates (at least 15s)
     * @param random   true if presences should be chosen randomly, else round-robin
     */
    void start(@NotNull JDA jda, long interval, boolean random);

    /**
     * Stops the Discord bot presence update task, if present.
     */
    void stop();

    /**
     * Returns the current stage (or category) that presences are chosen from.
     *
     * @return stage name
     */
    @NotNull String getStage();

    /**
     * Returns a list of all registered stage (or category) names.
     *
     * @return immutable list of stage names
     */
    List<String> getStages();

    /**
     * Sets the stage (or category) to choose presences from, and restarts the
     * Discord bot presence update task.
     *
     * <p>NB: If the given stage (or category) is already in use, no action is taken.
     *
     * @param name     stage name
     * @param interval number of seconds between presence updates (at least 15s)
     * @param random   true if presences should be chosen randomly, else round-robin
     * @return {@code this} for chaining
     * @see #start(JDA, long, boolean)
     */
    MinecordPresence setStage(@NotNull String name, long interval, boolean random);

    /**
     * Temporarily sets the stage (or category) to choose presences from,
     * restarts the Discord bot presence update task, and executes a given
     * runnable. After the given runnable finishes executing, reverts the
     * stage (or category) back to how it was found.
     *
     * @param name     stage name
     * @param interval number of seconds between presence updates (at least 15s)
     * @param random   true if presences should be chosen randomly, else round-robin
     * @param runnable function to be run while the stage is active
     * @return {@code this} for chaining
     * @see #setStage(String, long, boolean)
     */
    MinecordPresence setStageTemporarily(@NotNull String name, long interval, boolean random, Runnable runnable);

    /**
     * Clears all presence entries from the given stage (or category).
     *
     * <p>NB: If the given stage (or category) is currently in use, the Discord
     * bot presence update task is stopped.
     *
     * @param name stage name
     * @return list of removed presences, if any
     * @see #stop()
     */
    @Nullable List<PresenceSupplier> clearStage(@NotNull String name);

    /**
     * Returns the current stage (or category) presence update interval.
     *
     * @return current presence update interval
     */
    long getInterval();

    /**
     * Returns true if the current stage (or category) chooses random presences, else round-robin.
     *
     * @return true if presences are chosen randomly, else round-robin
     */
    boolean isRandom();

    /**
     * Returns a list of all presence entries from the current stage (or category).
     *
     * @return list of current presences, if any
     */
    @Nullable List<PresenceSupplier> getPresences();

    /**
     * Returns a list of all presence entries from the given stage (or category).
     *
     * @param name stage name
     * @return list of presences, if any
     */
    @Nullable List<PresenceSupplier> getPresences(@NotNull String name);

    /**
     * Adds presences to the given stage (or category).
     *
     * @param name      stage name
     * @param presences list of presences
     * @return {@code this} for chaining
     */
    MinecordPresence addPresences(@NotNull String name, @NotNull PresenceSupplier... presences);
}
