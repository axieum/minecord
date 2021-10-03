package me.axieum.mcmod.minecord.api.cmds;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

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
     * @see #addCommands(MinecordCommand...)
     */
    void updateCommandList();

    /**
     * Registers new commands.
     *
     * @param commands Minecord commands
     */
    void addCommands(@NotNull MinecordCommand... commands);

    /**
     * Unregisters a command given its unique name.
     *
     * @param name command name
     * @return the removed command instance, if any
     */
    MinecordCommand removeCommand(@NotNull String name);

    /**
     * Unregisters a command given its implementation.
     *
     * @param command Minecord command
     * @return the removed command instance, if any
     */
    MinecordCommand removeCommand(@NotNull MinecordCommand command);

    /**
     * Determines whether a given command name has been registered.
     *
     * @param name command name
     * @return true if the command name has already been registered
     */
    boolean hasCommand(@NotNull String name);

    /**
     * Returns a command given its previously registered name.
     *
     * @param name registered command name
     * @return registered Minecord command if present
     */
    Optional<MinecordCommand> getCommand(@NotNull String name);

    /**
     * Returns an immutable list of all registered commands.
     *
     * @return immutable list of Minecord commands
     */
    List<MinecordCommand> getCommands();
}
