package me.axieum.mcmod.minecord.impl.cmds;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import me.shedaniel.autoconfig.ConfigHolder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public void onInitializeMinecord(JDABuilder builder)
    {
        LOGGER.info("Minecord Commands is getting ready...");
        final CommandConfig config = getConfig();

        // Register Discord callbacks
        builder.addEventListeners(new DiscordCommandListener());

        // Register the uptime command, if enabled
        if (config.builtin.uptime.enabled) {
            try {
                LOGGER.info("Adding built-in uptime command as '/{}'", config.builtin.uptime.name);
                addCommands(new UptimeCommand(config.builtin.uptime));
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
                addCommands(new TPSCommand(config.builtin.tps));
            } catch (IllegalArgumentException e) {
                LOGGER.error("Encountered invalid built-in ticks-per-second (TPS) command!", e);
            }
        } else {
            LOGGER.info("Skipping disabled built-in ticks-per-second (TPS) command");
        }

        // Register all custom commands, if any
        for (CommandConfig.CustomCommand c : config.custom) {
            if (c.enabled) {
                try {
                    LOGGER.info("Adding custom command as '/{}' to run '{}'", c.name, c.command);
                    addCommands(new CustomCommand(c));
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Encountered invalid custom '/{}' command!", c.name, e);
                }
            } else {
                LOGGER.info("Skipping disabled custom command '/{}'", c.name);
            }
        }
    }

    @Override
    public void updateCommandList()
    {
        Minecord.getInstance().getJDA().ifPresentOrElse(jda -> {
            LOGGER.info("Updating the Discord command list...");

            // Prepare a bulk request to update all the commands
            CommandListUpdateAction commands = jda.updateCommands();

            // Extract and add all registered command data
            COMMANDS.values().stream().map(MinecordCommand::getCommandData).forEach(commands::addCommands);

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
    public void addCommands(final @NotNull MinecordCommand... commands)
    {
        for (MinecordCommand command : commands)
            COMMANDS.put(command.getCommandData().getName(), command);
    }

    @Override
    public MinecordCommand removeCommand(final @NotNull String name)
    {
        return COMMANDS.remove(name);
    }

    @Override
    public MinecordCommand removeCommand(final @NotNull MinecordCommand command)
    {
        return COMMANDS.remove(command.getCommandData().getName());
    }

    @Override
    public boolean hasCommand(final @NotNull String name)
    {
        return COMMANDS.containsKey(name);
    }

    @Override
    public Optional<MinecordCommand> getCommand(final @NotNull String name)
    {
        return Optional.ofNullable(COMMANDS.get(name));
    }

    @Override
    public List<MinecordCommand> getCommands()
    {
        return List.copyOf(COMMANDS.values()); // immutable
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
