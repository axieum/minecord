package me.axieum.mcmod.minecord.api.cmds;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.axieum.mcmod.minecord.api.cmds.command.MinecordCommand;
import me.axieum.mcmod.minecord.impl.cmds.MinecordCommandsImpl;

/**
 * A gateway into the Minecord Commands addon.
 */
public interface MinecordCommands
{
    /**
     * Returns the Minecord Commands instance.
     *
     * @return Minecord Commands instance
     */
    static MinecordCommands getInstance()
    {
        return MinecordCommandsImpl.INSTANCE;
    }

    /**
     * Updates the command list in Discord.
     *
     * <p>NB: It can take up to an hour for the changes to appear!
     *
     * @see #addCommand(String, MinecordCommand)
     */
    void updateCommandList();

    /**
     * Registers a new command, replacing any command with the same name.
     *
     * @param name     command name
     * @param commands Minecord commands
     * @return the previous command with the {@code name}, if any
     */
    @Nullable MinecordCommand addCommand(@NotNull String name, @NotNull MinecordCommand commands);

    /**
     * Unregisters a command given its unique name.
     *
     * @param name command name
     * @return the removed command instance, if any
     */
    @Nullable MinecordCommand removeCommand(@NotNull String name);

    /**
     * Returns a command given its previously registered name.
     *
     * @param name registered command name
     * @return registered Minecord command if present
     */
    Optional<MinecordCommand> getCommand(@Nullable String name);

    /**
     * Determines whether a given command name has been registered.
     *
     * @param name command name
     * @return true if the command name has already been registered
     */
    boolean hasCommand(@Nullable String name);

    /**
     * Returns an immutable list of all registered commands.
     *
     * @return immutable list of Minecord commands
     */
    List<MinecordCommand> getCommands();

    /**
     * Applies a given cooldown against a key.
     *
     * @param key cooldown key
     * @param seconds number of seconds the cooldown is active for
     */
    void applyCooldown(@NotNull String key, int seconds);

    /**
     * Looks up a given cooldown key and returns its remaining seconds.
     *
     * <p>Note: If the returned cooldown is zero, its key is removed!
     *
     * @param key cooldown key
     * @return seconds before the cooldown ends, defaults to {@code 0}
     */
    int getCooldown(@NotNull String key);

    /**
     * Returns an immutable mapping of all command cooldowns.
     *
     * @return immutable mapping of cooldown keys to their end timestamps
     */
    Map<String, Long> getCooldowns();

    /**
     * Clears a cooldown by its key.
     *
     * @param key cooldown key
     * @return the cleared cooldown end timestamp if present
     */
    @Nullable Long clearCooldown(@NotNull String key);

    /**
     * Clears all cooldown keys.
     */
    void clearCooldowns();

    /**
     * Clears any inactive/stale cooldown keys.
     */
    void clearInactiveCooldowns();
}
