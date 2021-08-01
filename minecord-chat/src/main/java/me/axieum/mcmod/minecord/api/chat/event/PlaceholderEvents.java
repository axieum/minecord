package me.axieum.mcmod.minecord.api.chat.event;

import java.util.Map;

import net.minecraft.advancement.Advancement;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashReport;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A collection of callbacks for changing placeholder values provided to message formatters.
 */
public final class PlaceholderEvents
{
    private PlaceholderEvents() {}

    /**
     * Called when the server began to start.
     */
    public static final Event<ServerStarting> SERVER_STARTING =
        EventFactory.createArrayBacked(ServerStarting.class, callbacks -> (values, server) -> {
            for (ServerStarting callback : callbacks) {
                callback.onServerStarting(values, server);
            }
        });

    /**
     * Called when the server started and is accepting connections.
     */
    public static final Event<ServerStarted> SERVER_STARTED =
        EventFactory.createArrayBacked(ServerStarted.class, callbacks -> (values, server) -> {
            for (ServerStarted callback : callbacks) {
                callback.onServerStarted(values, server);
            }
        });

    /**
     * Called when the server began to stop.
     */
    public static final Event<ServerStopping> SERVER_STOPPING =
        EventFactory.createArrayBacked(ServerStopping.class, callbacks -> (values, server) -> {
            for (ServerStopping callback : callbacks) {
                callback.onServerStopping(values, server);
            }
        });

    /**
     * Called when the server stopped and is offline.
     */
    public static final Event<ServerStopped> SERVER_STOPPED =
        EventFactory.createArrayBacked(ServerStopped.class, callbacks -> (values, server) -> {
            for (ServerStopped callback : callbacks) {
                callback.onServerStopped(values, server);
            }
        });

    /**
     * Called when the server stopped unexpectedly and is inaccessible.
     */
    public static final Event<ServerCrashed> SERVER_CRASHED =
        EventFactory.createArrayBacked(ServerCrashed.class, callbacks -> (values, server, crashReport) -> {
            for (ServerCrashed callback : callbacks) {
                callback.onServerCrashed(values, server, crashReport);
            }
        });

    /**
     * Called when a named animal/monster (with name tag) had died.
     */
    public static final Event<EntityDeath> ENTITY_DEATH =
        EventFactory.createArrayBacked(EntityDeath.class, callbacks -> (values, entity, source) -> {
            for (EntityDeath callback : callbacks) {
                callback.onEntityDeath(values, entity, source);
            }
        });

    /**
     * Called when a player joined the game.
     */
    public static final Event<PlayerConnect> PLAYER_CONNECT =
        EventFactory.createArrayBacked(PlayerConnect.class, callbacks -> (values, player) -> {
            for (PlayerConnect callback : callbacks) {
                callback.onPlayerConnect(values, player);
            }
        });

    /**
     * Called when a player left the game.
     */
    public static final Event<PlayerDisconnect> PLAYER_DISCONNECT =
        EventFactory.createArrayBacked(PlayerDisconnect.class, callbacks -> (values, player) -> {
            for (PlayerDisconnect callback : callbacks) {
                callback.onPlayerDisconnect(values, player);
            }
        });

    /**
     * Called when a player sent an in-game chat message.
     */
    public static final Event<PlayerChat> PLAYER_CHAT =
        EventFactory.createArrayBacked(PlayerChat.class, callbacks -> (values, player, message) -> {
            for (PlayerChat callback : callbacks) {
                callback.onPlayerChat(values, player, message);
            }
        });

    /**
     * Called when a player unlocked an advancement.
     */
    public static final Event<PlayerAdvancement> PLAYER_ADVANCEMENT =
        EventFactory.createArrayBacked(PlayerAdvancement.class, callbacks -> (values, player, adv, criterion) -> {
            for (PlayerAdvancement callback : callbacks) {
                callback.onPlayerAdvancement(values, player, adv, criterion);
            }
        });

    /**
     * Called when a player teleported to another dimension.
     */
    public static final Event<PlayerChangeWorld> PLAYER_CHANGE_WORLD =
        EventFactory.createArrayBacked(PlayerChangeWorld.class, callbacks -> (values, player, origin, destination) -> {
            for (PlayerChangeWorld callback : callbacks) {
                callback.onPlayerChangeWorld(values, player, origin, destination);
            }
        });

    /**
     * Called when a player had died.
     */
    public static final Event<PlayerDeath> PLAYER_DEATH =
        EventFactory.createArrayBacked(PlayerDeath.class, callbacks -> (values, player, source) -> {
            for (PlayerDeath callback : callbacks) {
                callback.onPlayerDeath(values, player, source);
            }
        });

    @FunctionalInterface
    public interface ServerStarting
    {
        /**
         * Called when the server began to start.
         *
         * @param values mutable placeholder values
         * @param server Minecraft server
         */
        void onServerStarting(Map<String, Object> values, MinecraftServer server);
    }

    @FunctionalInterface
    public interface ServerStarted
    {
        /**
         * Called when the server started and is accepting connections.
         *
         * @param values mutable placeholder values
         * @param server Minecraft server
         */
        void onServerStarted(Map<String, Object> values, MinecraftServer server);
    }

    @FunctionalInterface
    public interface ServerStopping
    {
        /**
         * Called when the server began to stop.
         *
         * @param values mutable placeholder values
         * @param server Minecraft server
         */
        void onServerStopping(Map<String, Object> values, MinecraftServer server);
    }

    @FunctionalInterface
    public interface ServerStopped
    {
        /**
         * Called when the server stopped and is offline.
         *
         * @param values mutable placeholder values
         * @param server Minecraft server
         */
        void onServerStopped(Map<String, Object> values, MinecraftServer server);
    }

    @FunctionalInterface
    public interface ServerCrashed
    {
        /**
         * Called when the server stopped unexpectedly and is inaccessible.
         *
         * @param values      mutable placeholder values
         * @param server      Minecraft server
         * @param crashReport a crash report that describes the reason for the server stopping
         */
        void onServerCrashed(Map<String, Object> values, MinecraftServer server, CrashReport crashReport);
    }

    @FunctionalInterface
    public interface EntityDeath
    {
        /**
         * Called when a named animal/monster (with name tag) had died.
         *
         * @param values mutable placeholder values
         * @param entity victim animal/monster
         * @param source damage source
         */
        void onEntityDeath(Map<String, Object> values, LivingEntity entity, DamageSource source);
    }

    @FunctionalInterface
    public interface PlayerConnect
    {
        /**
         * Called when a player joined the game.
         *
         * @param values mutable placeholder values
         * @param player player who logged in
         */
        void onPlayerConnect(Map<String, Object> values, ServerPlayerEntity player);
    }

    @FunctionalInterface
    public interface PlayerDisconnect
    {
        /**
         * Called when a player left the game.
         *
         * @param values mutable placeholder values
         * @param player player who logged out
         */
        void onPlayerDisconnect(Map<String, Object> values, ServerPlayerEntity player);
    }

    @FunctionalInterface
    public interface PlayerChat
    {
        /**
         * Called when a player sent an in-game chat message.
         *
         * @param values  mutable placeholder values
         * @param player  author of the message
         * @param message received message contents
         */
        void onPlayerChat(Map<String, Object> values, ServerPlayerEntity player, TextStream.Message message);
    }

    @FunctionalInterface
    public interface PlayerAdvancement
    {
        /**
         * Called when a player unlocked an advancement.
         *
         * @param values      mutable placeholder values
         * @param player      redeeming player
         * @param advancement parent advancement
         * @param criterion   name of the criterion granted
         */
        void onPlayerAdvancement(
            Map<String, Object> values, ServerPlayerEntity player, Advancement advancement, String criterion
        );
    }

    @FunctionalInterface
    public interface PlayerChangeWorld
    {
        /**
         * Called when a player teleported to another dimension.
         *
         * @param values      mutable placeholder values
         * @param player      affected player
         * @param origin      source world of the player
         * @param destination target world
         */
        void onPlayerChangeWorld(
            Map<String, Object> values, ServerPlayerEntity player, ServerWorld origin, ServerWorld destination
        );
    }

    @FunctionalInterface
    public interface PlayerDeath
    {
        /**
         * Called when a player had died.
         *
         * @param values mutable placeholder values
         * @param player victim player
         * @param source damage source
         */
        void onPlayerDeath(Map<String, Object> values, ServerPlayerEntity player, DamageSource source);
    }
}
