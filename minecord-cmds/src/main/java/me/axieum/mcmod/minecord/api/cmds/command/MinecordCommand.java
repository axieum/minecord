package me.axieum.mcmod.minecord.api.cmds.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

/**
 * A Minecord slash command.
 */
public abstract class MinecordCommand
{
    // The underlying JDA slash command data instance
    protected @NotNull CommandData data;

    /**
     * Constructs a new Minecord command instance.
     *
     * @param name        command name
     * @param description a brief command description
     * @throws IllegalArgumentException if the name or description is invalid
     * @see net.dv8tion.jda.api.interactions.commands.build.CommandData#CommandData(String, String)
     */
    public MinecordCommand(
        final @NotNull String name,
        final @NotNull String description
    ) throws IllegalArgumentException
    {
        this(new CommandData(name, description));
    }

    /**
     * Constructs a new Minecord command instance given command data.
     *
     * @param data prepared JDA command data
     */
    public MinecordCommand(final @NotNull CommandData data)
    {
        this.data = data;
    }

    /**
     * Returns the command name.
     *
     * @return command name
     */
    public String getName()
    {
        return data.getName();
    }

    /**
     * Returns the associated slash command data.
     *
     * @return JDA slash command data
     */
    public CommandData getCommandData()
    {
        return data;
    }

    /**
     * Sets the associated slash command data.
     *
     * @param data JDA slash command data
     */
    public void setCommandData(final @NotNull CommandData data)
    {
        this.data = data;
    }

    /**
     * Executes the slash command.
     *
     * @param event JDA slash command event
     */
    public abstract void execute(SlashCommandEvent event);
}
