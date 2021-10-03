package me.axieum.mcmod.minecord.impl.cmds.command.discord;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import me.axieum.mcmod.minecord.api.cmds.command.MinecordCommand;
import me.axieum.mcmod.minecord.impl.cmds.config.CommandConfig;

/**
 * Built-in uptime Minecord command.
 */
public class UptimeCommand extends MinecordCommand
{
    /**
     * Initialises a new uptime command.
     *
     * @param config uptime command config
     */
    public UptimeCommand(CommandConfig.BaseCommand config)
    {
        super(config.name, config.description);
        data.setDefaultEnabled(config.allowByDefault);
    }

    @Override
    public void execute(SlashCommandEvent event)
    {
        event.reply("You hit the uptime command!").queue();
    }
}
