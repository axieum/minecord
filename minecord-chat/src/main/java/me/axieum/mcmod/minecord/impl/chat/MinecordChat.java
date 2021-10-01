package me.axieum.mcmod.minecord.impl.chat;

import me.shedaniel.autoconfig.ConfigHolder;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import me.axieum.mcmod.minecord.api.addon.MinecordAddon;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.EntityDeathEvents;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.GrantCriterionCallback;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.ReceiveChatCallback;
import me.axieum.mcmod.minecord.api.event.ServerShutdownCallback;
import me.axieum.mcmod.minecord.impl.chat.callback.discord.MessageReactionListener;
import me.axieum.mcmod.minecord.impl.chat.callback.discord.MessageReceivedListener;
import me.axieum.mcmod.minecord.impl.chat.callback.discord.MessageUpdateListener;
import me.axieum.mcmod.minecord.impl.chat.callback.minecraft.EntityDeathCallback;
import me.axieum.mcmod.minecord.impl.chat.callback.minecraft.PlayerAdvancementCallback;
import me.axieum.mcmod.minecord.impl.chat.callback.minecraft.PlayerChangeWorldCallback;
import me.axieum.mcmod.minecord.impl.chat.callback.minecraft.PlayerChatCallback;
import me.axieum.mcmod.minecord.impl.chat.callback.minecraft.PlayerConnectionCallback;
import me.axieum.mcmod.minecord.impl.chat.callback.minecraft.PlayerDeathCallback;
import me.axieum.mcmod.minecord.impl.chat.callback.minecraft.ServerLifecycleCallback;
import me.axieum.mcmod.minecord.impl.chat.config.ChatConfig;

public final class MinecordChat implements MinecordAddon, DedicatedServerModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger("Minecord|Chat");
    private static final ConfigHolder<ChatConfig> CONFIG = ChatConfig.init();

    @Override
    public void onInitializeMinecord(JDABuilder builder)
    {
        LOGGER.info("Minecord Chat is getting ready...");

        /*
         * Register Discord callbacks.
         */

        builder.addEventListeners(
            // A user sent a message
            // A user sent a message that contained attachments
            new MessageReceivedListener(),
            // A user edited their recently sent message
            new MessageUpdateListener(),
            // A user reacted to a recent message
            // A user removed their reaction from a recent message
            new MessageReactionListener()
        );
    }

    @Override
    public void onInitializeServer()
    {
        /*
         * Register Minecraft server-related callbacks.
         */

        final ServerLifecycleCallback lifecycleCallback = new ServerLifecycleCallback();

        // The server began to start
        ServerLifecycleEvents.SERVER_STARTING.register(lifecycleCallback);
        // The server started and is accepting connections
        ServerLifecycleEvents.SERVER_STARTED.register(lifecycleCallback);
        // The server began to stop
        ServerLifecycleEvents.SERVER_STOPPING.register(lifecycleCallback);
        // The server stopped and is offline
        // The server stopped unexpectedly and is inaccessible
        ServerShutdownCallback.EVENT.register(lifecycleCallback);

        /*
         * Register Minecraft player-related callbacks.
         */

        final PlayerConnectionCallback playerConnectionCallback = new PlayerConnectionCallback();

        // A player joined the game
        ServerPlayConnectionEvents.JOIN.register(playerConnectionCallback);
        // A player left the game
        ServerPlayConnectionEvents.DISCONNECT.register(playerConnectionCallback);
        // A player sent an in-game chat message
        ReceiveChatCallback.EVENT.register(new PlayerChatCallback());
        // A player unlocked an advancement
        GrantCriterionCallback.EVENT.register(new PlayerAdvancementCallback());
        // A player teleported to another dimension
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(new PlayerChangeWorldCallback());
        // A player died
        EntityDeathEvents.PLAYER.register(new PlayerDeathCallback());

        /*
         * Register Minecraft miscellaneous callbacks.
         */

        // A named animal/monster (with name tag) died
        EntityDeathEvents.ANIMAL_MONSTER.register(new EntityDeathCallback());
    }

    /**
     * Returns the Minecord Chat config instance.
     *
     * @return config instance
     */
    public static ChatConfig getConfig()
    {
        return CONFIG.getConfig();
    }
}
