package me.axieum.mcmod.minecord.api.cmds.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

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
     * Called before executing a Minecord command.
     */
    public static final Event<BeforeExecute> BEFORE_EXECUTE =
        EventFactory.createArrayBacked(BeforeExecute.class, callbacks -> (ctx, event, server) -> {
            for (BeforeExecute callback : callbacks) {
                if (!callback.onBeforeMinecordCommand(ctx, event, server))
                    return false;
            }
            return true;
        });

    /**
     * Called after executing a Minecord command.
     */
    public static final Event<AfterExecute> AFTER_EXECUTE =
        EventFactory.createArrayBacked(AfterExecute.class, callbacks -> (ctx, event, server) -> {
            for (AfterExecute callback : callbacks) {
                callback.onMinecordCommand(ctx, event, server);
            }
        });

    @FunctionalInterface
    public interface BeforeExecute
    {
        /**
         * Called before executing a Minecord command.
         *
         * @param context Minecord command context
         * @param event   JDA slash command event to reply to
         * @param server  Minecraft server
         * @return true if the Minecord command should execute
         */
        boolean onBeforeMinecordCommand(
            MinecordCommand context,
            SlashCommandInteractionEvent event,
            @Nullable MinecraftServer server
        );
    }

    @FunctionalInterface
    public interface AfterExecute
    {
        /**
         * Called after executing a Minecord command.
         *
         * @param context Minecord command context
         * @param event   JDA slash command event to reply to
         * @param server  Minecraft server
         */
        void onMinecordCommand(
            MinecordCommand context,
            SlashCommandInteractionEvent event,
            @Nullable MinecraftServer server
        );
    }

    /**
     * A collection of built-in Minecord command events.
     */
    public static final class Builtin
    {
        /**
         * Called after executing the ticks-per-second (TPS) command.
         */
        public static final Event<TPSCommand> TPS =
            EventFactory.createArrayBacked(TPSCommand.class, callbacks -> (ctx, event, server, embed) -> {
                for (TPSCommand callback : callbacks) {
                    embed = callback.onTPSCommand(ctx, event, server, embed);
                }
                return embed;
            });

        /**
         * Called after executing the uptime command.
         */
        public static final Event<UptimeCommand> UPTIME =
            EventFactory.createArrayBacked(UptimeCommand.class, callbacks -> (ctx, event, srv, embed) -> {
                for (UptimeCommand callback : callbacks) {
                    embed = callback.onUptimeCommand(ctx, event, srv, embed);
                }
                return embed;
            });

        @FunctionalInterface
        public interface TPSCommand
        {
            /**
             * Called after executing the ticks-per-second (TPS) command.
             *
             * @param context Minecord command context
             * @param event   JDA slash command event to reply to
             * @param server  Minecraft server
             * @param embed   builder used to build the embed that will be sent to Discord
             * @return embed builder used to build the embed that will be sent to Discord
             */
            @NotNull EmbedBuilder onTPSCommand(
                MinecordCommand context,
                SlashCommandInteractionEvent event,
                @NotNull MinecraftServer server,
                EmbedBuilder embed
            );
        }

        @FunctionalInterface
        public interface UptimeCommand
        {
            /**
             * Called after executing the uptime command.
             *
             * @param context Minecord command
             * @param event   JDA slash command event to reply to
             * @param server  Minecraft server, if present
             * @param embed   builder used to build the embed that will be sent to Discord
             * @return embed builder used to build the embed that will be sent to Discord
             */
            @NotNull EmbedBuilder onUptimeCommand(
                MinecordCommand context,
                SlashCommandInteractionEvent event,
                @Nullable MinecraftServer server,
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
         * Called before allowing custom Minecraft-proxy command execution.
         *
         * <p>NB: If you cancel the execution by returning {@code null},
         * you are responsible for replying to the interaction.
         */
        public static final Event<AllowExecute> ALLOW_EXECUTE =
            EventFactory.createArrayBacked(AllowExecute.class, callbacks -> (ctx, event, server, cmd) -> {
                for (AllowExecute callback : callbacks) {
                    cmd = callback.onAllowCustomCommand(ctx, event, server, cmd);
                    if (cmd == null) return null;
                }
                return cmd;
            });

        /**
         * Called before executing a custom Minecraft-proxy command.
         */
        public static final Event<BeforeExecute> BEFORE_EXECUTE =
            EventFactory.createArrayBacked(BeforeExecute.class, callbacks -> (ctx, event, server, cmd, source) -> {
                for (BeforeExecute callback : callbacks) {
                    source = callback.onBeforeCustomCommand(ctx, event, server, cmd, source);
                }
                return source;
            });

        /**
         * Called during execution of a custom Minecraft-proxy command when
         * providing feedback to the executor.
         *
         * <p>NB: This is not called if no command feedback was provided!
         */
        public static final Event<Feedback> FEEDBACK =
            EventFactory.createArrayBacked(Feedback.class, callbacks -> (ctx, event, server, cmd, msg, s, embed) -> {
                for (Feedback callback : callbacks) {
                    embed = callback.onCustomCommandFeedback(ctx, event, server, cmd, msg, s, embed);
                    if (embed == null) return null;
                }
                return embed;
            });

        /**
         * Called after executing a custom Minecraft-proxy command.
         */
        public static final Event<AfterExecute> AFTER_EXECUTE =
            EventFactory.createArrayBacked(AfterExecute.class, callbacks -> (ctx, event, server, cmd, s, res, exc) -> {
                for (AfterExecute callback : callbacks) {
                    callback.onCustomCommand(ctx, event, server, cmd, s, res, exc);
                }
            });

        @FunctionalInterface
        public interface AllowExecute
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
             * @return the command to be executed if the execution should go ahead, or {@code null} to cancel
             */
            @Nullable String onAllowCustomCommand(
                MinecordCommand context,
                SlashCommandInteractionEvent event,
                @NotNull MinecraftServer server,
                String command
            );
        }

        @FunctionalInterface
        public interface BeforeExecute
        {
            /**
             * Called before executing a custom Minecraft-proxy command.
             *
             * @param context Minecord command context
             * @param event   JDA slash command event
             * @param server  Minecraft server
             * @param command Minecraft command that will be executed (without leading '/')
             * @param source  Minecraft command source
             * @return Minecraft command source used to execute the command
             */
            @NotNull ServerCommandSource onBeforeCustomCommand(
                MinecordCommand context,
                SlashCommandInteractionEvent event,
                @NotNull MinecraftServer server,
                String command,
                @NotNull ServerCommandSource source
            );
        }

        @FunctionalInterface
        public interface Feedback
        {
            /**
             * Called during execution of a custom Minecraft-proxy command when
             * providing feedback to the executor.
             *
             * <p>NB: This is not called if no command feedback was provided!
             *
             * @param context Minecord command context
             * @param event   JDA slash command event to reply to
             * @param server  Minecraft server
             * @param command Minecraft command that was executed (without leading '/')
             * @param text    Minecraft command execution feedback
             * @param success true if the command was a success
             * @param embed   builder used to build the embed that will be sent to Discord
             * @return embed builder used to build the embed that will be sent to Discord, or {@code null} to cancel
             */
            @Nullable EmbedBuilder onCustomCommandFeedback(
                MinecordCommand context,
                SlashCommandInteractionEvent event,
                @NotNull MinecraftServer server,
                String command,
                Text text,
                boolean success,
                EmbedBuilder embed
            );
        }

        @FunctionalInterface
        public interface AfterExecute
        {
            /**
             * Called after executing a custom Minecraft-proxy command.
             *
             * @param context Minecord command context
             * @param event   JDA slash command event to reply to
             * @param server  Minecraft server
             * @param command Minecraft command that was executed (without leading '/')
             * @param success true if the command was a success
             * @param result  Minecraft command execution result
             * @param exc     Minecraft command syntax exception if present, else {@code null}
             */
            void onCustomCommand(
                MinecordCommand context,
                SlashCommandInteractionEvent event,
                @NotNull MinecraftServer server,
                String command,
                boolean success,
                int result,
                @Nullable CommandSyntaxException exc
            );
        }
    }
}
