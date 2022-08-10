package me.axieum.mcmod.minecord.api.chat.event;

import java.util.List;

import com.github.difflib.text.DiffRow;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import org.jetbrains.annotations.Nullable;

import net.minecraft.advancement.Advancement;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import me.axieum.mcmod.minecord.api.util.StringTemplate;

/**
 * A collection of callbacks for providing placeholder values to Minecraft/Discord message formatters.
 */
public final class ChatPlaceholderEvents
{
    private ChatPlaceholderEvents() {}

    /**
     * A collection of Discord-related chat placeholder events.
     */
    public static final class Discord
    {
        /**
         * Called when a user sent a message.
         */
        public static final Event<MessageReceived> MESSAGE_RECEIVED =
            EventFactory.createArrayBacked(MessageReceived.class, callbacks -> (st, event) -> {
                for (MessageReceived callback : callbacks) {
                    callback.onMessageReceivedPlaceholder(st, event);
                }
            });

        /**
         * Called when a user edited their recently sent message.
         */
        public static final Event<MessageUpdated> MESSAGE_UPDATED =
            EventFactory.createArrayBacked(MessageUpdated.class, callbacks -> (st, event, context, diff) -> {
                for (MessageUpdated callback : callbacks) {
                    callback.onMessageUpdatedPlaceholder(st, event, context, diff);
                }
            });

        /**
         * Called when a user sent a message that contained attachments (for each attachment).
         */
        public static final Event<AttachmentReceived> ATTACHMENT_RECEIVED =
            EventFactory.createArrayBacked(AttachmentReceived.class, callbacks -> (st, event, attachment) -> {
                for (AttachmentReceived callback : callbacks) {
                    callback.onAttachmentReceivedPlaceholder(st, event, attachment);
                }
            });

        /**
         * Called when a user reacted to a recent message.
         */
        public static final Event<ReactionAdded> REACTION =
            EventFactory.createArrayBacked(ReactionAdded.class, callbacks -> (st, event) -> {
                for (ReactionAdded callback : callbacks) {
                    callback.onReactionPlaceholder(st, event);
                }
            });

        @FunctionalInterface
        public interface MessageReceived
        {
            /**
             * Called when a user sent a message.
             *
             * @param template mutable string template
             * @param event    JDA message received event instance
             */
            void onMessageReceivedPlaceholder(StringTemplate template, MessageReceivedEvent event);
        }

        @FunctionalInterface
        public interface MessageUpdated
        {
            /**
             * Called when a user edited their recently sent message.
             *
             * @param template mutable string template
             * @param event    JDA message update event instance
             * @param context  original JDA message instance
             * @param diff     computed textual differences
             */
            void onMessageUpdatedPlaceholder(
                StringTemplate template, MessageUpdateEvent event, Message context, List<DiffRow> diff
            );
        }

        @FunctionalInterface
        public interface AttachmentReceived
        {
            /**
             * Called when a user sent a message that contained attachments (for each attachment).
             *
             * @param template   mutable string template
             * @param event      JDA message received event instance
             * @param attachment JDA message attachment
             * @see MessageReceived#onMessageReceivedPlaceholder(StringTemplate, MessageReceivedEvent) for message text
             */
            void onAttachmentReceivedPlaceholder(
                StringTemplate template, MessageReceivedEvent event, Message.Attachment attachment
            );
        }

        @FunctionalInterface
        public interface ReactionAdded
        {
            /**
             * Called when a user reacted to a recent message.
             *
             * @param template mutable string template
             * @param event    JDA reaction event instance
             */
            void onReactionPlaceholder(StringTemplate template, GenericMessageReactionEvent event);
        }
    }

    /**
     * A collection of Minecraft-related chat placeholder events.
     */
    public static final class Minecraft
    {
        /**
         * Called when the server began to start.
         */
        public static final Event<ServerStarting> SERVER_STARTING =
            EventFactory.createArrayBacked(ServerStarting.class, callbacks -> (st, server) -> {
                for (ServerStarting callback : callbacks) {
                    callback.onServerStartingPlaceholder(st, server);
                }
            });

        /**
         * Called when the server started and is accepting connections.
         */
        public static final Event<ServerStarted> SERVER_STARTED =
            EventFactory.createArrayBacked(ServerStarted.class, callbacks -> (st, server) -> {
                for (ServerStarted callback : callbacks) {
                    callback.onServerStartedPlaceholder(st, server);
                }
            });

        /**
         * Called when the server began to stop.
         */
        public static final Event<ServerStopping> SERVER_STOPPING =
            EventFactory.createArrayBacked(ServerStopping.class, callbacks -> (st, server) -> {
                for (ServerStopping callback : callbacks) {
                    callback.onServerStoppingPlaceholder(st, server);
                }
            });

        /**
         * Called when the server stopped and is offline, be it gracefully or not.
         */
        public static final Event<ServerShutdown> SERVER_SHUTDOWN =
            EventFactory.createArrayBacked(ServerShutdown.class, callbacks -> (st, server, crashReport) -> {
                for (ServerShutdown callback : callbacks) {
                    callback.onServerShutdownPlaceholder(st, server, crashReport);
                }
            });

        /**
         * Called when a named animal/monster (with name tag) had died.
         */
        public static final Event<EntityDeath> ENTITY_DEATH =
            EventFactory.createArrayBacked(EntityDeath.class, callbacks -> (st, entity, source) -> {
                for (EntityDeath callback : callbacks) {
                    callback.onEntityDeathPlaceholder(st, entity, source);
                }
            });

        /**
         * Called when a player joined the game.
         */
        public static final Event<PlayerConnect> PLAYER_CONNECT =
            EventFactory.createArrayBacked(PlayerConnect.class, callbacks -> (st, player) -> {
                for (PlayerConnect callback : callbacks) {
                    callback.onPlayerConnectPlaceholder(st, player);
                }
            });

        /**
         * Called when a player left the game.
         */
        public static final Event<PlayerDisconnect> PLAYER_DISCONNECT =
            EventFactory.createArrayBacked(PlayerDisconnect.class, callbacks -> (st, player) -> {
                for (PlayerDisconnect callback : callbacks) {
                    callback.onPlayerDisconnectPlaceholder(st, player);
                }
            });

        /**
         * Called when a player sent an in-game chat message.
         */
        public static final Event<PlayerChat> PLAYER_CHAT =
            EventFactory.createArrayBacked(PlayerChat.class, callbacks -> (st, player, message, typeKey) -> {
                for (PlayerChat callback : callbacks) {
                    callback.onPlayerChatPlaceholder(st, player, message, typeKey);
                }
            });

        /**
         * Called when a player unlocked an advancement.
         */
        public static final Event<PlayerAdvancement> PLAYER_ADVANCEMENT =
            EventFactory.createArrayBacked(PlayerAdvancement.class, callbacks -> (st, player, adv, criterion) -> {
                for (PlayerAdvancement callback : callbacks) {
                    callback.onPlayerAdvancementPlaceholder(st, player, adv, criterion);
                }
            });

        /**
         * Called when a player teleported to another dimension.
         */
        public static final Event<PlayerChangeWorld> PLAYER_CHANGE_WORLD =
            EventFactory.createArrayBacked(PlayerChangeWorld.class, callbacks -> (st, player, origin, destination) -> {
                for (PlayerChangeWorld callback : callbacks) {
                    callback.onPlayerChangeWorldPlaceholder(st, player, origin, destination);
                }
            });

        /**
         * Called when a player had died.
         */
        public static final Event<PlayerDeath> PLAYER_DEATH =
            EventFactory.createArrayBacked(PlayerDeath.class, callbacks -> (st, player, source) -> {
                for (PlayerDeath callback : callbacks) {
                    callback.onPlayerDeathPlaceholder(st, player, source);
                }
            });

        /**
         * Called when a player sent an in-game message via the {@code /me} command.
         */
        public static final Event<EmoteCommand> EMOTE_COMMAND =
            EventFactory.createArrayBacked(EmoteCommand.class, callbacks -> (st, source, action) -> {
                for (EmoteCommand callback : callbacks) {
                    callback.onEmoteCommandPlaceholder(st, source, action);
                }
            });

        /**
         * Called when an admin broadcast an in-game message via the {@code /say} command.
         */
        public static final Event<SayCommand> SAY_COMMAND =
            EventFactory.createArrayBacked(SayCommand.class, callbacks -> (st, source, action) -> {
                for (SayCommand callback : callbacks) {
                    callback.onSayCommandPlaceholder(st, source, action);
                }
            });

        /**
         * Called when an admin broadcast an in-game message to all players via the {@code /tellraw @a} command.
         */
        public static final Event<TellRawCommand> TELLRAW_COMMAND =
            EventFactory.createArrayBacked(TellRawCommand.class, callbacks -> (st, source, action) -> {
                for (TellRawCommand callback : callbacks) {
                    callback.onTellRawCommandPlaceholder(st, source, action);
                }
            });

        @FunctionalInterface
        public interface ServerStarting
        {
            /**
             * Called when the server began to start.
             *
             * @param template mutable string template
             * @param server   Minecraft server
             */
            void onServerStartingPlaceholder(StringTemplate template, MinecraftServer server);
        }

        @FunctionalInterface
        public interface ServerStarted
        {
            /**
             * Called when the server started and is accepting connections.
             *
             * @param template mutable string template
             * @param server   Minecraft server
             */
            void onServerStartedPlaceholder(StringTemplate template, MinecraftServer server);
        }

        @FunctionalInterface
        public interface ServerStopping
        {
            /**
             * Called when the server began to stop.
             *
             * @param template mutable string template
             * @param server   Minecraft server
             */
            void onServerStoppingPlaceholder(StringTemplate template, MinecraftServer server);
        }

        @FunctionalInterface
        public interface ServerShutdown
        {
            /**
             * Called when the server stopped and is offline, be it gracefully or not.
             *
             * @param template    mutable string template
             * @param server      Minecraft server
             * @param crashReport a crash report if the server crashed
             */
            void onServerShutdownPlaceholder(
                StringTemplate template, MinecraftServer server, @Nullable CrashReport crashReport
            );
        }

        @FunctionalInterface
        public interface EntityDeath
        {
            /**
             * Called when a named animal/monster (with name tag) had died.
             *
             * @param template mutable string template
             * @param entity   victim animal/monster
             * @param source   damage source
             */
            void onEntityDeathPlaceholder(StringTemplate template, LivingEntity entity, DamageSource source);
        }

        @FunctionalInterface
        public interface PlayerConnect
        {
            /**
             * Called when a player joined the game.
             *
             * @param template mutable string template
             * @param player   player who logged in
             */
            void onPlayerConnectPlaceholder(StringTemplate template, ServerPlayerEntity player);
        }

        @FunctionalInterface
        public interface PlayerDisconnect
        {
            /**
             * Called when a player left the game.
             *
             * @param template mutable string template
             * @param player   player who logged out
             */
            void onPlayerDisconnectPlaceholder(StringTemplate template, ServerPlayerEntity player);
        }

        @FunctionalInterface
        public interface PlayerChat
        {
            /**
             * Called when a player sent an in-game chat message.
             *
             * @param template mutable string template
             * @param player   author of the message
             * @param message  received message contents
             * @param typeKey  received message type
             * @see net.fabricmc.fabric.api.message.v1.ServerMessageEvents#CHAT_MESSAGE
             */
            void onPlayerChatPlaceholder(
                StringTemplate template,
                ServerPlayerEntity player,
                FilteredMessage<SignedMessage> message,
                RegistryKey<MessageType> typeKey
            );
        }

        @FunctionalInterface
        public interface PlayerAdvancement
        {
            /**
             * Called when a player unlocked an advancement.
             *
             * @param template    mutable string template
             * @param player      redeeming player
             * @param advancement parent advancement
             * @param criterion   name of the criterion granted
             */
            void onPlayerAdvancementPlaceholder(
                StringTemplate template, ServerPlayerEntity player, Advancement advancement, String criterion
            );
        }

        @FunctionalInterface
        public interface PlayerChangeWorld
        {
            /**
             * Called when a player teleported to another dimension.
             *
             * @param template    mutable string template
             * @param player      affected player
             * @param origin      source world of the player
             * @param destination target world
             */
            void onPlayerChangeWorldPlaceholder(
                StringTemplate template, ServerPlayerEntity player, ServerWorld origin, ServerWorld destination
            );
        }

        @FunctionalInterface
        public interface PlayerDeath
        {
            /**
             * Called when a player had died.
             *
             * @param template mutable string template
             * @param player   victim player
             * @param source   damage source
             */
            void onPlayerDeathPlaceholder(StringTemplate template, ServerPlayerEntity player, DamageSource source);
        }

        @FunctionalInterface
        public interface EmoteCommand
        {
            /**
             * Called when a player sent an in-game message via the {@code /me}
             * command.
             *
             * @param template mutable string template
             * @param source   source of the command, e.g. a player
             * @param action   received message contents
             * @see net.minecraft.network.message.MessageType#EMOTE_COMMAND
             * @see net.fabricmc.fabric.api.message.v1.ServerMessageEvents#COMMAND_MESSAGE
             */
            void onEmoteCommandPlaceholder(
                StringTemplate template,
                ServerCommandSource source,
                FilteredMessage<SignedMessage> action
            );
        }

        @FunctionalInterface
        public interface SayCommand
        {
            /**
             * Called when an admin broadcast an in-game message via the
             * {@code /say} command.
             *
             * @param template mutable string template
             * @param source   source of the message, e.g. a player
             * @param action   received message contents
             * @see net.minecraft.network.message.MessageType#SAY_COMMAND
             * @see net.fabricmc.fabric.api.message.v1.ServerMessageEvents#COMMAND_MESSAGE
             */
            void onSayCommandPlaceholder(
                StringTemplate template,
                ServerCommandSource source,
                FilteredMessage<SignedMessage> action
            );
        }

        @FunctionalInterface
        public interface TellRawCommand
        {
            /**
             * Called when an admin broadcast an in-game message to *all*
             * players via the {@code /tellraw @a} command.
             *
             * @param template mutable string template
             * @param source   source of the message, e.g. a player
             * @param message  received message contents
             * @see net.minecraft.network.message.MessageType#TELLRAW_COMMAND
             */
            void onTellRawCommandPlaceholder(StringTemplate template, ServerCommandSource source, Text message);
        }
    }
}
