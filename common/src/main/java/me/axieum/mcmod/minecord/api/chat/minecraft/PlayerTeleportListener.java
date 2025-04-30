package me.axieum.mcmod.minecord.api.chat.minecraft;

import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import dev.architectury.event.events.common.PlayerEvent;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.DiscordDispatcher;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when a player teleports to another dimension.
 */
public class PlayerTeleportListener implements PlayerEvent.ChangeDimension
{
    @Override
    public void change(ServerPlayer player, ResourceKey<Level> oldLevel, ResourceKey<Level> newLevel)
    {
        Minecord.getJDA().ifPresent(jda -> {
            final BlockPos lastBlockPos = player.getLastSectionPos().origin();

            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(player);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The name of the world the player entered
                "world", string(StringUtils.getLevelName(newLevel)),
                // The X coordinate of where the player entered
                "pos_x", string(String.valueOf(player.getBlockX())),
                // The Y coordinate of where the player entered
                "pos_y", string(String.valueOf(player.getBlockY())),
                // The Z coordinate of where the player entered
                "pos_z", string(String.valueOf(player.getBlockZ())),
                // The name of the world the player left
                "origin", string(StringUtils.getLevelName(oldLevel)),
                // The X coordinate of where the player left
                "origin_pos_x", string(String.valueOf(lastBlockPos.getX())),
                // The Y coordinate of where the player left
                "origin_pos_y", string(String.valueOf(lastBlockPos.getY())),
                // The Z coordinate of where the player left
                "origin_pos_z", string(String.valueOf(lastBlockPos.getZ()))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embedWithAvatar(
                (embed, entry) -> embed.setDescription(
                    PlaceholdersExt.parseString(entry.discord.teleportNode, ctx, placeholders)
                ),
                entry -> entry.discord.teleport != null && entry.hasWorld(newLevel),
                player.getStringUUID()
            );
        });
    }
}
