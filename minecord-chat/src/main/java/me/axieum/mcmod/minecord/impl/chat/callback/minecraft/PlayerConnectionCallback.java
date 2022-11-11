package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when a Minecraft player joins or leaves.
 */
public class PlayerConnectionCallback implements Join, Disconnect
{
    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            final ServerPlayerEntity player = handler.player;

            /*
             * Prepare the message placeholders.
             */

            final @Nullable PlaceholderContext ctx = PlaceholderContext.of(player);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The player's username
                "username", string(player.getName().getString()),
                // The player's display name
                "player", string(player.getDisplayName().getString()),
                // The name of the world the player logged into
                "world", string(StringUtils.getWorldName(player.world)),
                // The X coordinate of where the player logged into
                "x", string(String.valueOf(player.getBlockX())),
                // The Y coordinate of where the player logged into
                "y", string(String.valueOf(player.getBlockY())),
                // The Z coordinate of where the player logged into
                "z", string(String.valueOf(player.getBlockZ()))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embedWithAvatar(
                (embed, entry) -> embed.setDescription(
                    PlaceholdersExt.parseString(entry.discord.join, ctx, placeholders)
                ),
                entry -> entry.discord.join != null && entry.hasWorld(player.world),
                player.getUuidAsString()
            );
        });
    }

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            final ServerPlayerEntity player = handler.player;

            /*
             * Prepare the message placeholders.
             */

            final @Nullable PlaceholderContext ctx = PlaceholderContext.of(player);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The player's username
                "username", string(player.getName().getString()),
                // The player's display name
                "player", string(player.getDisplayName().getString()),
                // The name of the world the player logged out
                "world", string(StringUtils.getWorldName(player.world)),
                // The X coordinate of where the player logged out
                "x", string(String.valueOf(player.getBlockX())),
                // The Y coordinate of where the player logged out
                "y", string(String.valueOf(player.getBlockY())),
                // The Z coordinate of where the player logged out
                "z", string(String.valueOf(player.getBlockZ()))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embedWithAvatar(
                (embed, entry) -> embed.setDescription(
                    PlaceholdersExt.parseString(entry.discord.leave, ctx, placeholders)
                ),
                entry -> entry.discord.leave != null && entry.hasWorld(player.world),
                player.getUuidAsString()
            );
        });
    }
}
