package me.axieum.mcmod.minecord.api.event;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;

import me.axieum.mcmod.minecord.api.cmds.MinecordCommand;

/**
 * A collection of custom callbacks for Minecraft.
 */
public interface MinecraftEvents
{
    /**
     * Called when a server (or player) broadcasts a message to
     * *all* players via the {@code /tellraw} command.
     */
    Event<TellRaw> TELL_RAW = EventFactory.createLoop();

    /**
     * Called when a server (or player) broadcasts a message to
     * *all* players via the {@code /say} command.
     */
    Event<Say> SAY = EventFactory.createLoop();
    /**
     * Called when a server (or player) broadcasts a message to
     * *all* players via the {@code /me} command.
     */
    Event<Emote> EMOTE = EventFactory.createLoop();

    /**
     * Called before allowing custom Minecraft-proxy command execution.
     *
     * <p>NB: If you cancel the execution, you are responsible for replying to the interaction.
     */
    Event<AllowCommand> ALLOW_COMMAND = EventFactory.createEventResult();

    /**
     * A callback for when a server (or player) broadcasts a message to
     * *all* players via the {@code /tellraw} command.
     */
    @FunctionalInterface
    interface TellRaw
    {
        /**
         * Called when a server (or player) broadcasts a message to
         * *all* players via the {@code /tellraw} command.
         *
         * @param component The message to be sent.
         * @param source    The command source that sent the message.
         */
        void tellRaw(Component component, CommandSourceStack source);
    }

    /**
     * A callback for when a server (or player) broadcasts a message to
     * *all* players via the {@code /say} command.
     */
    @FunctionalInterface
    interface Say
    {
        /**
         * Called when a server (or player) broadcasts a message to
         * *all* players via the {@code /say} command.
         *
         * @param component The message to be sent.
         * @param source    The command source that sent the message.
         */
        void say(Component component, CommandSourceStack source);
    }

    /**
     * A callback for when a server (or player) broadcasts a message to
     * *all* players via the {@code /me} command.
     */
    @FunctionalInterface
    interface Emote
    {
        /**
         * Called when a server (or player) broadcasts a message to
         * *all* players via the {@code /me} command.
         *
         * @param component The message to be sent.
         * @param source    The command source that sent the message.
         */
        void emote(Component component, CommandSourceStack source);
    }

    /**
     * A callback for before allowing custom Minecraft-proxy command execution.
     */
    @FunctionalInterface
    interface AllowCommand
    {
        /**
         * Called before allowing custom Minecraft-proxy command execution.
         *
         * <p>NB: If you cancel the execution by returning {@code null},
         * you are responsible for replying to the interaction.
         *
         * @param context Minecord command context
         * @param event   JDA slash command event
         * @param server  Minecraft server
         * @param command Minecraft command that will be executed (without leading '/')
         * @return an event result for if the command execution should go ahead
         */
        EventResult shouldAllowCommand(
            MinecordCommand context,
            SlashCommandInteractionEvent event,
            @NotNull MinecraftServer server,
            String command
        );
    }
}
