package me.axieum.mcmod.minecord.api.presence.category;

import java.util.Optional;
import java.util.function.Function;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

/**
 * A Minecord presence supplier.
 */
public interface PresenceSupplier
{
    /**
     * Returns whether the bot is idle.
     *
     * @return true if the bot is idle, or empty if not changing
     */
    default Optional<Boolean> isIdle()
    {
        return Optional.empty();
    }

    /**
     * Returns the online status for the bot.
     *
     * @return online status, or empty if not changing
     */
    default Optional<OnlineStatus> getStatus()
    {
        return Optional.empty();
    }

    /**
     * Returns the game activity for the bot.
     *
     * @return game activity, or empty if not changing
     */
    default Optional<Activity> getActivity()
    {
        return getActivity(Function.identity());
    }

    /**
     * Returns the game activity for the bot after modifying the name.
     *
     * @param nameMutator mutator for the activity name
     * @return game activity, or empty if not changing
     */
    default Optional<Activity> getActivity(Function<String, String> nameMutator)
    {
        return Optional.empty();
    }
}
