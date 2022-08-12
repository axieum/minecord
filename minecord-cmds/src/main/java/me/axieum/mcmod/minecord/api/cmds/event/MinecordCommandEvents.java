package me.axieum.mcmod.minecord.api.cmds.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import me.axieum.mcmod.minecord.api.cmds.command.MinecordCommand;

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
            EventFactory.createArrayBacked(AfterExecuteUptimeCommand.class, callbacks -> (ctx, event, srv, embed) -> {
                for (AfterExecuteUptimeCommand callback : callbacks) {
                    embed = callback.onAfterExecuteUptime(ctx, event, srv, embed);
                }
                return embed;
            });

        @FunctionalInterface
        public interface AfterExecuteUptimeCommand
        {
            /**
             * Called after executing an uptime command.
             *
             * @param context Minecord command
             * @param event   JDA slash command event to reply to
             * @param server  Minecraft server, if present
             * @param embed   builder used to build the embed that will be sent to Discord
             * @return embed builder used to build the embed that will be sent to Discord
             */
            @NotNull EmbedBuilder onAfterExecuteUptime(
                MinecordCommand context,
                SlashCommandInteractionEvent event,
                @Nullable MinecraftServer server,
                EmbedBuilder embed
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
            EventFactory.createArrayBacked(AfterExecuteTPSCommand.class, callbacks -> (ctx, event, server, embed) -> {
                for (AfterExecuteTPSCommand callback : callbacks) {
                    embed = callback.onAfterExecuteTPS(ctx, event, server, embed);
                }
                return embed;
            });

        @FunctionalInterface
        public interface AfterExecuteTPSCommand
        {
            /**
             * Called after executing a ticks-per-second (TPS) command.
             *
             * @param context Minecord command context
             * @param event   JDA slash command event to reply to
             * @param server  Minecraft server
             * @param embed   builder used to build the embed that will be sent to Discord
             * @return embed builder used to build the embed that will be sent to Discord
             */
            @NotNull EmbedBuilder onAfterExecuteTPS(
                MinecordCommand context,
                SlashCommandInteractionEvent event,
                @NotNull MinecraftServer server,
                EmbedBuilder embed
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
            EventFactory.createArrayBacked(BeforeExecuteCustomCommand.class, callbacks -> (ctx, event, server, cmd) -> {
                for (BeforeExecuteCustomCommand callback : callbacks) {
                    cmd = callback.onBeforeExecuteCustom(ctx, event, server, cmd);
                    if (cmd == null) break;
                }
                return cmd;
            });

        /**
         * Called after executing a custom Minecraft-proxy command.
         * NB: This not called if the command does not provide any feedback!
         */
        public static final Event<AfterExecuteCustomCommand> AFTER_EXECUTE =
            EventFactory.createArrayBacked(AfterExecuteCustomCommand.class, callbacks -> (x, ev, se, c, re, su, em) -> {
                for (AfterExecuteCustomCommand callback : callbacks) {
                    em = callback.onAfterExecuteCustom(x, ev, se, c, re, su, em);
                }
                return em;
            });

        @FunctionalInterface
        public interface BeforeExecuteCustomCommand
        {
            /**
             * Called before executing a custom Minecraft-proxy command.
             *
             * @param context Minecord command context
             * @param event   JDA slash command event
             * @param server  Minecraft server
             * @param command Minecraft command that will be executed (without leading '/')
             * @return the command to be executed if the execution should go ahead, or null to cancel
             */
            @Nullable String onBeforeExecuteCustom(
                MinecordCommand context,
                SlashCommandInteractionEvent event,
                @NotNull MinecraftServer server,
                String command
            );
        }

        @FunctionalInterface
        public interface AfterExecuteCustomCommand
        {
            /**
             * Called after executing a custom Minecraft-proxy command.
             * NB: This not called if the command does not provide any feedback!
             *
             * @param context Minecord command context
             * @param event   JDA slash command event to reply to
             * @param server  Minecraft server
             * @param command Minecraft command that was executed (without leading '/')
             * @param result  Minecraft command execution feedback
             * @param success true if the command was a success
             * @param embed   builder used to build the embed that will be sent to Discord
             * @return embed builder used to build the embed that will be sent to Discord
             */
            @NotNull EmbedBuilder onAfterExecuteCustom(
                MinecordCommand context,
                SlashCommandInteractionEvent event,
                @NotNull MinecraftServer server,
                String command,
                String result,
                boolean success,
                EmbedBuilder embed
            );
        }
    }
}
