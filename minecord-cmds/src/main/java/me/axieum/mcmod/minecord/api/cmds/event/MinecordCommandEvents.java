package me.axieum.mcmod.minecord.api.cmds.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A collection of callbacks for Minecord issued commands.
 */
public final class MinecordCommandEvents
{
    private MinecordCommandEvents() {}

    /**
     * A collection of uptime command events.
     */
    public static final class Uptime
    {
        /**
         * Called after executing an uptime command.
         */
        public static final Event<AfterExecuteUptimeCommand> AFTER_EXECUTE =
            EventFactory.createArrayBacked(AfterExecuteUptimeCommand.class, callbacks -> (event, server, embed) -> {
                EmbedBuilder builder = embed;
                for (AfterExecuteUptimeCommand callback : callbacks)
                    builder = callback.onAfterExecuteUptime(event, server, embed);
                return builder;
            });

        @FunctionalInterface
        public interface AfterExecuteUptimeCommand
        {
            /**
             * Called after executing an uptime command.
             *
             * @param event   JDA slash command event to reply to
             * @param server  Minecraft server, if present
             * @param embed   builder used to build the embed that will be sent to Discord
             * @return embed builder used to build the embed that will be sent to Discord
             */
            @NotNull EmbedBuilder onAfterExecuteUptime(
                SlashCommandInteractionEvent event, @Nullable MinecraftServer server, EmbedBuilder embed
            );
        }
    }

    /**
     * A collection of ticks-per-second (TPS) command events.
     */
    public static final class TPS
    {
        /**
         * Called after executing a ticks-per-second (TPS) command.
         */
        public static final Event<AfterExecuteTPSCommand> AFTER_EXECUTE =
            EventFactory.createArrayBacked(AfterExecuteTPSCommand.class, callbacks -> (event, server, embed) -> {
                EmbedBuilder builder = embed;
                for (AfterExecuteTPSCommand callback : callbacks)
                    builder = callback.onAfterExecuteTPS(event, server, embed);
                return builder;
            });

        @FunctionalInterface
        public interface AfterExecuteTPSCommand
        {
            /**
             * Called after executing a ticks-per-second (TPS) command.
             *
             * @param event   JDA slash command event to reply to
             * @param server  Minecraft server
             * @param embed   builder used to build the embed that will be sent to Discord
             * @return embed builder used to build the embed that will be sent to Discord
             */
            @NotNull EmbedBuilder onAfterExecuteTPS(
                SlashCommandInteractionEvent event, @NotNull MinecraftServer server, EmbedBuilder embed
            );
        }
    }

    /**
     * A collection of custom Minecraft-proxy command events.
     */
    public static final class Custom
    {
        /**
         * Called before executing a custom Minecraft-proxy command.
         */
        public static final Event<BeforeExecuteCustomCommand> BEFORE_EXECUTE =
            EventFactory.createArrayBacked(BeforeExecuteCustomCommand.class, callbacks -> (command, event, server) -> {
                for (BeforeExecuteCustomCommand callback : callbacks)
                    if (!callback.onBeforeExecuteCustom(command, event, server))
                        return false;
                return true;
            });

        /**
         * Called after executing a custom Minecraft-proxy command.
         */
        public static final Event<AfterExecuteCustomCommand> AFTER_EXECUTE =
            EventFactory.createArrayBacked(AfterExecuteCustomCommand.class, callbacks -> (ev, se, co, re, su, em) -> {
                EmbedBuilder builder = em;
                for (AfterExecuteCustomCommand callback : callbacks)
                    builder = callback.onAfterExecuteCustom(ev, se, co, re, su, em);
                return builder;
            });

        @FunctionalInterface
        public interface BeforeExecuteCustomCommand
        {
            /**
             * Called before executing a custom Minecraft-proxy command.
             *
             * @param event   JDA slash command event
             * @param server  Minecraft server
             * @param command Minecraft command that will be executed (without leading '/')
             * @return true if the execution should go ahead, or false to cancel
             */
            boolean onBeforeExecuteCustom(String command, SlashCommandInteractionEvent event, MinecraftServer server);
        }

        @FunctionalInterface
        public interface AfterExecuteCustomCommand
        {
            /**
             * Called after executing a custom Minecraft-proxy command.
             *
             * @param event   JDA slash command event to reply to
             * @param server  Minecraft server
             * @param command Minecraft command that was executed (without leading '/')
             * @param result  Minecraft command execution feedback
             * @param success true if the command was a success
             * @param embed   builder used to build the embed that will be sent to Discord
             * @return embed builder used to build the embed that will be sent to Discord
             */
            @NotNull EmbedBuilder onAfterExecuteCustom(
                SlashCommandInteractionEvent event,
                MinecraftServer server,
                String command,
                String result,
                boolean success,
                EmbedBuilder embed
            );
        }
    }
}
