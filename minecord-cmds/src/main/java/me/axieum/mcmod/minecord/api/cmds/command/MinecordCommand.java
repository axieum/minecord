package me.axieum.mcmod.minecord.api.cmds.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

/**
 * A Minecord slash command.
 */
public abstract class MinecordCommand extends ListenerAdapter
{
    // The underlying JDA slash command data instance
    protected @NotNull CommandData data;
    // True if the command requires Minecraft
    protected boolean requiresMinecraft = true;
    // True if the command feedback is only visible to the executor
    protected boolean isEphemeral = false;

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
    public @NotNull String getName()
    {
        return data.getName();
    }

    /**
     * Returns the associated slash command data.
     *
     * @return JDA slash command data
     */
    public @NotNull CommandData getCommandData()
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
     * Returns whether this command's feedback is only visible to the executor.
     *
     * @return true if the command feedback is ephemeral
     * @see net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction#setEphemeral(boolean)
     */
    public boolean isEphemeral()
    {
        return isEphemeral;
    }

    /**
     * Sets whether this command's feedback is only visible to the executor.
     *
     * @param isEphemeral true if the command feedback is ephemeral
     * @return {@code this} for chaining
     */
    public MinecordCommand setEphemeral(boolean isEphemeral)
    {
        this.isEphemeral = isEphemeral;
        return this;
    }

    /**
     * Returns whether this command requires the Minecraft server to
     * have *started* in order to execute.
     *
     * @return true if the command requires Minecraft
     */
    public boolean requiresMinecraft()
    {
        return requiresMinecraft;
    }

    /**
     * Sets whether this command requires the Minecraft server to have
     * *started* in order to execute.
     *
     * @param requiresMinecraft true if the command requires Minecraft
     * @return {@code this} for chaining
     */
    public MinecordCommand setRequiresMinecraft(boolean requiresMinecraft)
    {
        this.requiresMinecraft = requiresMinecraft;
        return this;
    }

    /**
     * Executes the slash command.
     *
     * @param event  JDA slash command event
     * @param server Minecraft server, if present
     * @throws Exception if the command could not be executed
     * @see net.dv8tion.jda.api.hooks.ListenerAdapter#onSlashCommand(SlashCommandEvent)
     */
    public abstract void execute(@NotNull SlashCommandEvent event, @Nullable MinecraftServer server) throws Exception;
}
