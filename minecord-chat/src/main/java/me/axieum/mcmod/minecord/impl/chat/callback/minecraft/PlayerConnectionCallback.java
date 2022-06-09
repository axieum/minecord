package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.ChatPlaceholderEvents;
import me.axieum.mcmod.minecord.api.chat.util.ChatStringUtils;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

public class PlayerConnectionCallback implements Join, Disconnect
{
    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare a message template.
             */

            final StringTemplate st = new StringTemplate();
            final ServerPlayerEntity player = handler.player;

            // The player's username
            st.add("username", player.getName().getString());
            // The player's display name
            st.add("player", player.getDisplayName().getString());
            // The name of the world the player logged into
            st.add("world", ChatStringUtils.getWorldName(player.world));
            // The X coordinate of where the player logged into
            st.add("x", String.valueOf(player.getBlockX()));
            // The Y coordinate of where the player logged into
            st.add("y", String.valueOf(player.getBlockY()));
            // The Z coordinate of where the player logged into
            st.add("z", String.valueOf(player.getBlockZ()));

            ChatPlaceholderEvents.Minecraft.PLAYER_CONNECT.invoker().onPlayerConnectPlaceholder(st, player);

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embed((embed, entry) ->
                    embed.setDescription(st.format(entry.discord.join)),
                entry -> entry.discord.join != null && entry.hasWorld(player.world));
        });
    }

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare a message template.
             */

            final StringTemplate st = new StringTemplate();
            final ServerPlayerEntity player = handler.player;

            // The player's username
            st.add("username", player.getName().getString());
            // The player's display name
            st.add("player", player.getDisplayName().getString());
            // The name of the world the player logged out
            st.add("world", ChatStringUtils.getWorldName(player.world));
            // The X coordinate of where the player logged out
            st.add("x", String.valueOf(player.getBlockX()));
            // The Y coordinate of where the player logged out
            st.add("y", String.valueOf(player.getBlockY()));
            // The Z coordinate of where the player logged out
            st.add("z", String.valueOf(player.getBlockZ()));

            ChatPlaceholderEvents.Minecraft.PLAYER_DISCONNECT.invoker().onPlayerDisconnectPlaceholder(st, player);

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embed((embed, entry) ->
                    embed.setDescription(st.format(entry.discord.leave)),
                entry -> entry.discord.leave != null && entry.hasWorld(player.world));
        });
    }
}
