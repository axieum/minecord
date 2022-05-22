package me.axieum.mcmod.minecord.api.cmds.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

/**
 * A Minecord slash command.
 */
public abstract class MinecordCommand extends ListenerAdapter
{
    // The underlying JDA slash command data instance
    protected @NotNull SlashCommandData data;
    // True if the command requires Minecraft
    protected boolean requiresMinecraft = true;
    // True if the command feedback is only visible to the executor
    protected boolean isEphemeral = false;
    // The number of seconds a user must wait before using the command again
    protected int cooldown = 0;
    // To whom the cooldown applies
    protected CooldownScope cooldownScope = CooldownScope.USER;

    /**
     * Constructs a new Minecord command instance.
     *
     * @param name        command name
     * @param description a brief command description
     * @throws IllegalArgumentException if the name or description is invalid
     * @see net.dv8tion.jda.api.interactions.commands.build.Commands#slash(String, String)
     */
    public MinecordCommand(
        final @NotNull String name,
        final @NotNull String description
    ) throws IllegalArgumentException
    {
        this(Commands.slash(name, description));
    }

    /**
     * Constructs a new Minecord command instance given command data.
     *
     * @param data prepared JDA slash command data
     */
    public MinecordCommand(final @NotNull SlashCommandData data)
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
    public @NotNull CommandData getSlashCommandData()
    {
        return data;
    }

    /**
     * Sets the associated slash command data.
     *
     * @param data JDA slash command data
     */
    public void setSlashCommandData(final @NotNull SlashCommandData data)
    {
        this.data = data;
    }

    /**
     * Returns whether this command's feedback is only visible to the executor.
     *
     * @return true if the command feedback is ephemeral
     * @see net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction#setEphemeral(boolean)
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
     * Returns the number of seconds a user must wait before using the
     * command again.
     *
     * @return cooldown in seconds
     */
    public int getCooldown()
    {
        return cooldown;
    }

    /**
     * Sets the number of seconds a user must wait before using the command
     * again.
     *
     * @param cooldown cooldown in seconds
     * @return {@code this} for chaining
     */
    public MinecordCommand setCooldown(int cooldown)
    {
        this.cooldown = cooldown;
        return this;
    }

    /**
     * Returns whom the cooldown applies.
     *
     * @return cooldown scope
     */
    public CooldownScope getCooldownScope()
    {
        return cooldownScope;
    }

    /**
     * Sets to whom the cooldown applies.
     *
     * @param cooldownScope cooldown scope
     * @return {@code this} for chaining
     */
    public MinecordCommand setCooldownScope(CooldownScope cooldownScope)
    {
        this.cooldownScope = cooldownScope;
        return this;
    }

    /**
     * Executes the slash command.
     *
     * @param event  JDA slash command event
     * @param server Minecraft server, if present
     * @throws Exception if the command could not be executed
     * @see net.dv8tion.jda.api.hooks.ListenerAdapter#onSlashCommandInteraction(SlashCommandInteractionEvent)
     */
    public abstract void execute(
        @NotNull SlashCommandInteractionEvent event,
        @Nullable MinecraftServer server
    ) throws Exception;
}
