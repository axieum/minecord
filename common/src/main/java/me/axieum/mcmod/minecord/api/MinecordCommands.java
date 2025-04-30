package me.axieum.mcmod.minecord.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.axieum.mcmod.minecord.api.cmds.DiscordCommandListener;
import me.axieum.mcmod.minecord.api.cmds.MinecordCommand;
import me.axieum.mcmod.minecord.api.cmds.builtin.CustomCommand;
import me.axieum.mcmod.minecord.api.cmds.builtin.TPSCommand;
import me.axieum.mcmod.minecord.api.cmds.builtin.UptimeCommand;
import me.axieum.mcmod.minecord.api.config.CommandConfig;
import me.axieum.mcmod.minecord.api.event.JDAEvents;
import static me.axieum.mcmod.minecord.api.Minecord.LOGGER;

/**
 * A gateway into the Minecord Commands addon.
 */
public final class MinecordCommands
{
    private MinecordCommands() {}

    /**
     * A mapping of command names to their implementation (initial capacity for all built-in commands).
     */
    private static final HashMap<String, MinecordCommand> COMMANDS = new HashMap<>(2);

    /**
     * A mapping of cooldown keys to their end timestamp (millis since epoch).
     */
    private static final HashMap<String, Long> COOLDOWNS = new HashMap<>();

    /**
     * Initialises and registers all Minecord provided commands.
     *
     * @param jda JDA client
     */
    public static void init(JDA jda)
    {
        // Register the uptime command, if enabled
        CommandConfig.BuiltinCommands.UptimeCommand uptime = CommandConfig.BuiltinCommands.uptime;
        if (uptime.enabled) {
            try {
                LOGGER.info("Adding built-in uptime command as '/{}'", uptime.name);
                addCommand(uptime.name, new UptimeCommand(uptime));
            } catch (IllegalArgumentException e) {
                LOGGER.error("Encountered invalid built-in uptime command!", e);
            }
        } else {
            LOGGER.info("Skipping disabled built-in uptime command");
        }

        // Register the ticks-per-second (TPS) command, if enabled
        CommandConfig.BuiltinCommands.TPSCommand tps = CommandConfig.BuiltinCommands.tps;
        if (tps.enabled) {
            try {
                LOGGER.info("Adding built-in ticks-per-second (TPS) command as '/{}'", tps.name);
                addCommand(tps.name, new TPSCommand(tps));
            } catch (IllegalArgumentException e) {
                LOGGER.error("Encountered invalid built-in ticks-per-second (TPS) command!", e);
            }
        } else {
            LOGGER.info("Skipping disabled built-in ticks-per-second (TPS) command");
        }

        // Register all custom commands, if any
        for (CommandConfig.CustomCommand c : CommandConfig.custom) {
            if (c.enabled) {
                try {
                    LOGGER.info("Adding custom command as '/{}' to run '{}'", c.name, c.command);
                    addCommand(c.name, new CustomCommand(c));
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Encountered invalid custom '/{}' command!", c.name, e);
                }
            } else {
                LOGGER.info("Skipping disabled custom command '/{}'", c.name);
            }
        }

        // Clear any existing command cooldowns
        clearCooldowns();

        // Update the command list in Discord
        updateCommandList(jda);

        // Register the command event listeners
        jda.addEventListener(new DiscordCommandListener());
        getCommands().forEach(jda::addEventListener);
    }

    /**
     * Updates the command list in Discord.
     *
     * <p>NB: It can take up to an hour for the changes to appear!
     *
     * @param jda JDA client
     * @see #addCommand(String, MinecordCommand)
     */
    public static void updateCommandList(JDA jda)
    {
        LOGGER.info("Updating the Discord command list...");

        // Prepare a bulk request to update all the commands
        CommandListUpdateAction commands = jda.updateCommands();

        // Extract and add all registered command data
        COMMANDS.values().stream().map(MinecordCommand::getSlashCommandData).forEach(commands::addCommands);

        // Fire an event to allow the action to be mutated
        commands = JDAEvents.BEFORE_UPDATE_COMMAND_LIST.invoker().onUpdatingCommandList(commands);

        // Finally, queue the command list update action
        commands.queue(cmds -> {
            LOGGER.info("Updated {} commands in Discord - they can take up to an hour to appear!", cmds.size());
            JDAEvents.AFTER_UPDATE_COMMAND_LIST.invoker().onUpdateCommandList(cmds);
        }, error -> LOGGER.error("Could not update the command list in Discord!", error));
    }

    /**
     * Registers a new command, replacing any command with the same name.
     *
     * @param name     command name
     * @param command  Minecord command
     * @return the previous command with the {@code name}, if any
     */
    public static @Nullable MinecordCommand addCommand(@NotNull String name, @NotNull MinecordCommand command)
    {
        // Add the new command, capturing the previous command with its name, if present
        final @Nullable MinecordCommand oldCommand = COMMANDS.put(name, command);
        // Update the command event listeners, if present
        Minecord.getJDA().ifPresent(jda -> {
            // Remove the old event listener, if present
            if (oldCommand != null) jda.removeEventListener(oldCommand);
            // Add the new event listener
            jda.addEventListener(command);
        });
        return oldCommand;
    }

    /**
     * Unregisters a command given its unique name.
     *
     * @param name command name
     * @return the removed command instance, if any
     */
    public static @Nullable MinecordCommand removeCommand(@NotNull String name)
    {
        // Remove the command, capturing its instance, if present
        final @Nullable MinecordCommand command = COMMANDS.remove(name);
        // Remove the command as an event listener, if present
        if (command != null) Minecord.getJDA().ifPresent(jda -> jda.removeEventListener(command));
        return command;
    }

    /**
     * Returns a command given its previously registered name.
     *
     * @param name registered command name
     * @return registered Minecord command if present
     */
    public static Optional<MinecordCommand> getCommand(@Nullable String name)
    {
        return Optional.ofNullable(COMMANDS.get(name));
    }

    /**
     * Determines whether a given command name has been registered.
     *
     * @param name command name
     * @return true if the command name has already been registered
     */
    public static boolean hasCommand(@Nullable String name)
    {
        return COMMANDS.containsKey(name);
    }

    /**
     * Returns an immutable list of all registered commands.
     *
     * @return immutable list of Minecord commands
     */
    public static List<MinecordCommand> getCommands()
    {
        return List.copyOf(COMMANDS.values()); // immutable
    }

    /**
     * Applies a given cooldown against a key.
     *
     * @param key     cooldown key
     * @param seconds number of seconds the cooldown is active for
     */
    public static void applyCooldown(@NotNull String key, int seconds)
    {
        if (seconds > 0) COOLDOWNS.put(key, System.currentTimeMillis() + seconds * 1000L);
    }

    /**
     * Looks up a given cooldown key and returns its remaining seconds.
     *
     * <p>Note: If the returned cooldown is zero, its key is removed!
     *
     * @param key cooldown key
     * @return seconds before the cooldown ends, defaults to {@code 0}
     */
    public static int getCooldown(@NotNull String key)
    {
        int remaining = 0;
        if (COOLDOWNS.containsKey(key)) {
            remaining = Math.max(0, (int) Math.round((COOLDOWNS.get(key) - System.currentTimeMillis()) / 1000D));
            if (remaining == 0) clearCooldown(key);
        }
        return remaining;
    }

    /**
     * Returns an immutable mapping of all command cooldowns.
     *
     * @return immutable mapping of cooldown keys to their end timestamps
     */
    public static Map<String, Long> getCooldowns()
    {
        return Map.copyOf(COOLDOWNS); // immutable
    }

    /**
     * Clears a cooldown by its key.
     *
     * @param key cooldown key
     */
    public static void clearCooldown(@NotNull String key)
    {
        COOLDOWNS.remove(key);
    }

    /**
     * Clears all cooldown keys.
     */
    public static void clearCooldowns()
    {
        COOLDOWNS.clear();
    }

    /**
     * Clears any inactive/stale cooldown keys.
     */
    public static void clearInactiveCooldowns()
    {
        final long now = System.currentTimeMillis();
        COOLDOWNS.values().removeIf(endsAt -> now >= endsAt);
    }
}
