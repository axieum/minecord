package me.axieum.mcmod.minecord.impl.cmds.command.discord;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import me.axieum.mcmod.minecord.api.cmds.command.MinecordCommand;
import me.axieum.mcmod.minecord.impl.cmds.config.CommandConfig;

/**
 * Custom Minecraft proxy Minecord command.
 */
public class CustomCommand extends MinecordCommand
{
    // The custom command config instance
    private final CommandConfig.CustomCommand config;

    /**
     * Initialises a new custom command.
     *
     * @param config custom command config
     */
    public CustomCommand(CommandConfig.CustomCommand config)
    {
        super(config.name, config.description);
        data.setDefaultEnabled(config.allowByDefault);
        this.config = config;
    }

    @Override
    public void execute(SlashCommandEvent event)
    {
        event.reply(String.format("You hit the %s command!", getName())).queue();
    }
}
