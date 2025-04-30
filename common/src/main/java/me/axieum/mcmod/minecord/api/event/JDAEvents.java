package me.axieum.mcmod.minecord.api.event;

import java.util.List;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;

import me.axieum.mcmod.minecord.api.cmds.MinecordCommand;

/**
 * A collection of callbacks for the managed JDA client.
 */
public interface JDAEvents
{
    /**
     * Called before building the JDA client.
     */
    Event<BuildClient> BUILD_CLIENT = EventFactory.createLoop();

    /**
     * Called before updating the Discord command list.
     */
    Event<BeforeUpdateCommandList> BEFORE_UPDATE_COMMAND_LIST = EventFactory.createLoop();

    /**
     * Called after updating the Discord command list.
     */
    Event<AfterUpdateCommandList> AFTER_UPDATE_COMMAND_LIST = EventFactory.createLoop();

    /**
     * Called before allowing Discord command execution.
     *
     * <p>NB: If you cancel the execution, you are responsible for replying to the interaction.
     */
    Event<AllowCommand> ALLOW_COMMAND = EventFactory.createEventResult();

    /**
     * A callback for before building the JDA client.
     */
    @FunctionalInterface
    interface BuildClient
    {
        /**
         * Called before building the JDA client.
         *
         * @param builder JDA client builder
         */
        void onBuildClient(JDABuilder builder);
    }

    /**
     * A callback for before updating the Discord command list.
     */
    @FunctionalInterface
    interface BeforeUpdateCommandList
    {
        /**
         * Called before updating the Discord command list.
         *
         * @param action command list update action
         * @return command list update action to be queued
         */
        @NotNull CommandListUpdateAction onUpdatingCommandList(@NotNull CommandListUpdateAction action);
    }

    /**
     * A callback for after updating the Discord command list.
     */
    @FunctionalInterface
    interface AfterUpdateCommandList
    {
        /**
         * Called after updating the Discord command list.
         *
         * @param commands list of resulting commands
         */
        void onUpdateCommandList(List<Command> commands);
    }

    /**
     * A callback for before allowing Discord command execution.
     */
    @FunctionalInterface
    interface AllowCommand
    {
        /**
         * Called before allowing Discord command execution.
         *
         * <p>NB: If you cancel the execution by returning {@code null},
         * you are responsible for replying to the interaction.
         *
         * @param context Minecord command context
         * @param event   JDA slash command event to reply to
         * @param server  Minecraft server if available
         * @return an event result for if the command execution should go ahead
         */
        EventResult shouldAllowCommand(
            MinecordCommand context,
            SlashCommandInteractionEvent event,
            @Nullable MinecraftServer server
        );
    }
}
