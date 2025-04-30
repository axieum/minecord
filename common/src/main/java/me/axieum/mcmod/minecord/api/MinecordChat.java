package me.axieum.mcmod.minecord.api;

import net.dv8tion.jda.api.JDA;

import dev.architectury.event.events.common.ChatEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;

import me.axieum.mcmod.minecord.api.chat.discord.MessageReactionCallback;
import me.axieum.mcmod.minecord.api.chat.discord.MessageReceivedCallback;
import me.axieum.mcmod.minecord.api.chat.discord.MessageUpdateCallback;
import me.axieum.mcmod.minecord.api.chat.minecraft.AdvancementListener;
import me.axieum.mcmod.minecord.api.chat.minecraft.ChatReceivedListener;
import me.axieum.mcmod.minecord.api.chat.minecraft.CommandEmoteListener;
import me.axieum.mcmod.minecord.api.chat.minecraft.CommandSayListener;
import me.axieum.mcmod.minecord.api.chat.minecraft.CommandTellRawListener;
import me.axieum.mcmod.minecord.api.chat.minecraft.EntityDeathListener;
import me.axieum.mcmod.minecord.api.chat.minecraft.PlayerJoinListener;
import me.axieum.mcmod.minecord.api.chat.minecraft.PlayerQuitListener;
import me.axieum.mcmod.minecord.api.chat.minecraft.PlayerTeleportListener;
import me.axieum.mcmod.minecord.api.chat.minecraft.ServerStartedListener;
import me.axieum.mcmod.minecord.api.chat.minecraft.ServerStartingListener;
import me.axieum.mcmod.minecord.api.chat.minecraft.ServerStoppedListener;
import me.axieum.mcmod.minecord.api.chat.minecraft.ServerStoppingListener;
import me.axieum.mcmod.minecord.api.cmds.DiscordCommandListener;
import me.axieum.mcmod.minecord.api.event.MinecraftEvents;

/**
 * A gateway into the Minecord Chat addon.
 */
public final class MinecordChat
{
    private MinecordChat() {}

    public static void init(JDA jda)
    {
        /*
         * Register Discord events.
         */

        jda.addEventListener(
            // A user sent a message
            // A user sent a message that contained attachments
            new MessageReceivedCallback(),
            // A user edited their recently sent message
            new MessageUpdateCallback(),
            // A user reacted to a recent message
            // A user removed their reaction from a recent message
            new MessageReactionCallback(),
            // A user ran a slash command
            new DiscordCommandListener()
        );

        /*
         * Register Minecraft server events.
         */

        // The server began to start
        LifecycleEvent.SERVER_STARTING.register(new ServerStartingListener());
        // The server started and is accepting connections
        LifecycleEvent.SERVER_STARTED.register(new ServerStartedListener());
        // The server began to stop
        LifecycleEvent.SERVER_STOPPING.register(new ServerStoppingListener());
        // The server stopped and is offline
        // The server stopped unexpectedly and is inaccessible
        LifecycleEvent.SERVER_STOPPED.register(new ServerStoppedListener());
        // A player joined the game
        PlayerEvent.PLAYER_JOIN.register(new PlayerJoinListener());
        // A player left the game
        PlayerEvent.PLAYER_QUIT.register(new PlayerQuitListener());
        // A player sent an in-game chat message
        ChatEvent.RECEIVED.register(new ChatReceivedListener());
        // A player unlocked an advancement
        PlayerEvent.PLAYER_ADVANCEMENT.register(new AdvancementListener());
        // A player teleported to another dimension
        PlayerEvent.CHANGE_DIMENSION.register(new PlayerTeleportListener());
        // A player died
        // A named animal/monster (with name tag) died
        EntityEvent.LIVING_DEATH.register(new EntityDeathListener());
        // A player sent an in-game message via the '/me' command
        MinecraftEvents.EMOTE.register(new CommandEmoteListener());
        // An admin broadcast an in-game message via the '/say' command
        MinecraftEvents.SAY.register(new CommandSayListener());
        // An admin broadcast an in-game message to all players via the '/tellraw' command
        MinecraftEvents.TELL_RAW.register(new CommandTellRawListener());
    }
}
