package me.axieum.mcmod.minecord.impl.cmds.command.discord;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import me.axieum.mcmod.minecord.api.cmds.command.MinecordCommand;
import me.axieum.mcmod.minecord.impl.cmds.config.CommandConfig;

/**
 * Built-in ticks-per-second (TPS) Minecord command.
 */
public class TPSCommand extends MinecordCommand
{
    /**
     * Initialises a new ticks-per-second (TPS) command.
     *
     * @param config ticks-per-second (TPS) command config
     */
    public TPSCommand(CommandConfig.BaseCommand config)
    {
        super(config.name, config.description);
        data.setDefaultEnabled(config.allowByDefault);
    }

    @Override
    public void execute(SlashCommandEvent event)
    {
        event.reply("You hit the ticks-per-second (TPS) command!").queue();
    }
}
