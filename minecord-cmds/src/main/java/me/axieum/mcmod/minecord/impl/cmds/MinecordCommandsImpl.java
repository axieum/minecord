package me.axieum.mcmod.minecord.impl.cmds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import me.shedaniel.autoconfig.ConfigHolder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.addon.MinecordAddon;
import me.axieum.mcmod.minecord.api.cmds.MinecordCommands;
import me.axieum.mcmod.minecord.api.cmds.command.MinecordCommand;
import me.axieum.mcmod.minecord.api.cmds.event.JDACommandEvents;
import me.axieum.mcmod.minecord.impl.cmds.callback.DiscordCommandListener;
import me.axieum.mcmod.minecord.impl.cmds.command.discord.CustomCommand;
import me.axieum.mcmod.minecord.impl.cmds.command.discord.TPSCommand;
import me.axieum.mcmod.minecord.impl.cmds.command.discord.UptimeCommand;
import me.axieum.mcmod.minecord.impl.cmds.config.CommandConfig;

public final class MinecordCommandsImpl implements MinecordCommands, MinecordAddon
{
    public static final MinecordCommands INSTANCE = new MinecordCommandsImpl();
    public static final Logger LOGGER = LogManager.getLogger("Minecord|Commands");
    private static final ConfigHolder<CommandConfig> CONFIG = CommandConfig.init();

    // A mapping of command names to their implementation (initial capacity for all built-in commands)
    private static final HashMap<String, MinecordCommand> COMMANDS = new HashMap<>(2);
    // A mapping of cooldown keys to their end timestamp (millis since epoch)
    private static final HashMap<String, Long> COOLDOWNS = new HashMap<>();

    @Override
    public void onInitializeMinecord(JDABuilder builder)
    {
        LOGGER.info("Minecord Commands is getting ready...");

        // Register Discord callbacks
        builder.addEventListeners(new DiscordCommandListener());

        // Register all Minecord provided commands
        initCommands(getConfig());
    }

    /**
     * Initialises and registers all Minecord provided commands.
     *
     * @param config command config
     */
    public static void initCommands(CommandConfig config)
    {
        final MinecordCommands commands = MinecordCommands.getInstance();

        // Register the uptime command, if enabled
        if (config.builtin.uptime.enabled) {
            try {
                LOGGER.info("Adding built-in uptime command as '/{}'", config.builtin.uptime.name);
                commands.addCommand(config.builtin.uptime.name, new UptimeCommand(config.builtin.uptime));
            } catch (IllegalArgumentException e) {
                LOGGER.error("Encountered invalid built-in uptime command!", e);
            }
        } else {
            LOGGER.info("Skipping disabled built-in uptime command");
        }

        // Register the ticks-per-second (TPS) command, if enabled
        if (config.builtin.tps.enabled) {
            try {
                LOGGER.info("Adding built-in ticks-per-second (TPS) command as '/{}'", config.builtin.uptime.name);
                commands.addCommand(config.builtin.tps.name, new TPSCommand(config.builtin.tps));
            } catch (IllegalArgumentException e) {
                LOGGER.error("Encountered invalid built-in ticks-per-second (TPS) command!", e);
            }
        } else {
            LOGGER.info("Skipping disabled built-in ticks-per-second (TPS) command");
        }

        // Register all custom commands, if any
        for (CommandConfig.CustomCommandSchema c : config.custom) {
            if (c.enabled) {
                try {
                    LOGGER.info("Adding custom command as '/{}' to run '{}'", c.name, c.command);
                    commands.addCommand(c.name, new CustomCommand(c));
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Encountered invalid custom '/{}' command!", c.name, e);
                }
            } else {
                LOGGER.info("Skipping disabled custom command '/{}'", c.name);
            }
        }

        // Clear any existing command cooldowns
        commands.clearCooldowns();
    }

    @Override
    public void updateCommandList()
    {
        Minecord.getInstance().getJDA().ifPresentOrElse(jda -> {
            LOGGER.info("Updating the Discord command list...");

            // Prepare a bulk request to update all the commands
            CommandListUpdateAction commands = jda.updateCommands();

            // Extract and add all registered command data
            COMMANDS.values().stream().map(MinecordCommand::getSlashCommandData).forEach(commands::addCommands);

            // Fire an event to allow the action be mutated
            commands = JDACommandEvents.BEFORE_UPDATE_COMMAND_LIST.invoker().onUpdatingCommandList(commands);

            // Finally, queue the command list update action
            commands.queue(cmds -> {
                LOGGER.info("Updated {} commands in Discord - they can take up to an hour to appear!", cmds.size());
                JDACommandEvents.AFTER_UPDATE_COMMAND_LIST.invoker().onUpdateCommandList(cmds);
            }, error -> LOGGER.error("Could not update the command list in Discord!", error));
        }, () -> LOGGER.warn("Unable to update the command list in Discord as the bot is not yet connected!"));
    }

    @Override
    public @Nullable MinecordCommand addCommand(final @NotNull String name, final @NotNull MinecordCommand command)
    {
        // Add the new command, capturing the previous command with its name, if present
        final @Nullable MinecordCommand oldCommand = COMMANDS.put(name, command);
        // Update the command event listeners, if present
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            // Remove the old event listener, if present
            if (oldCommand != null) jda.removeEventListener(oldCommand);
            // Add the new event listener
            jda.addEventListener(command);
        });
        return oldCommand;
    }

    @Override
    public @Nullable MinecordCommand removeCommand(final @NotNull String name)
    {
        // Remove the command, capturing its instance, if present
        final @Nullable MinecordCommand command = COMMANDS.remove(name);
        // Remove the command as an event listener, if present
        if (command != null) Minecord.getInstance().getJDA().ifPresent(jda -> jda.removeEventListener(command));
        return command;
    }

    @Override
    public boolean hasCommand(final @Nullable String name)
    {
        return COMMANDS.containsKey(name);
    }

    @Override
    public Optional<MinecordCommand> getCommand(final @Nullable String name)
    {
        return Optional.ofNullable(COMMANDS.get(name));
    }

    @Override
    public List<MinecordCommand> getCommands()
    {
        return List.copyOf(COMMANDS.values()); // immutable
    }

    @Override
    public void applyCooldown(@NotNull String key, int seconds)
    {
        if (seconds > 0) COOLDOWNS.put(key, System.currentTimeMillis() + seconds * 1000L);
    }

    @Override
    public int getCooldown(@NotNull String key)
    {
        int remaining = 0;
        if (COOLDOWNS.containsKey(key)) {
            remaining = Math.max(0, (int) Math.round((COOLDOWNS.get(key) - System.currentTimeMillis()) / 1000D));
            if (remaining == 0) clearCooldown(key);
        }
        return remaining;
    }

    @Override
    public Map<String, Long> getCooldowns()
    {
        return Map.copyOf(COOLDOWNS); // immutable
    }

    @Override
    public @Nullable Long clearCooldown(@NotNull String key)
    {
        return COOLDOWNS.remove(key);
    }

    @Override
    public void clearCooldowns()
    {
        COOLDOWNS.clear();
    }

    @Override
    public void clearInactiveCooldowns()
    {
        final long now = System.currentTimeMillis();
        COOLDOWNS.values().removeIf(endsAt -> now >= endsAt);
    }

    /**
     * Returns the Minecord Commands config instance.
     *
     * @return config instance
     */
    public static CommandConfig getConfig()
    {
        return CONFIG.getConfig();
    }
}
