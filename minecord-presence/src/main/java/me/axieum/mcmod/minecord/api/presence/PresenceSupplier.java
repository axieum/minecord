package me.axieum.mcmod.minecord.api.presence;

import java.util.Optional;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;

import me.axieum.mcmod.minecord.api.util.StringTemplate;

/**
 * A Minecord presence supplier.
 */
@FunctionalInterface
public interface PresenceSupplier
{
    /**
     * Returns whether the bot is idle.
     *
     * @return true if the bot is idle, or unset if not changing
     */
    default Optional<Boolean> isIdle()
    {
        return Optional.empty();
    }

    /**
     * Returns the online status for the bot.
     *
     * @return online status if changing
     */
    default Optional<OnlineStatus> getStatus()
    {
        return Optional.empty();
    }

    /**
     * Returns the game activity for the bot.
     *
     * @param template string template with placeholders
     * @return game activity
     */
    @NotNull Activity getActivity(StringTemplate template);
}
