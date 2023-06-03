package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when a Minecraft player changes world.
 */
public class PlayerChangeWorldCallback implements ServerEntityWorldChangeEvents.AfterPlayerChange
{
    @Override
    public void afterChangeWorld(ServerPlayerEntity player, ServerWorld origin, ServerWorld dest)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(player);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The name of the world the player entered
                "world", string(StringUtils.getWorldName(dest)),
                // The X coordinate of where the player entered
                "pos_x", string(String.valueOf(player.getBlockX())),
                // The Y coordinate of where the player entered
                "pos_y", string(String.valueOf(player.getBlockY())),
                // The Z coordinate of where the player entered
                "pos_z", string(String.valueOf(player.getBlockZ())),
                // The name of the world the player left
                "origin", string(StringUtils.getWorldName(origin)),
                // The X coordinate of where the player left
                "origin_pos_x", string(String.valueOf((int) player.prevX)),
                // The Y coordinate of where the player left
                "origin_pos_y", string(String.valueOf((int) player.prevY)),
                // The Z coordinate of where the player left
                "origin_pos_z", string(String.valueOf((int) player.prevZ))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embedWithAvatar(
                (embed, entry) -> embed.setDescription(
                    PlaceholdersExt.parseString(entry.discord.teleport, ctx, placeholders)
                ),
                entry -> entry.discord.teleport != null && entry.hasWorld(dest),
                player.getUuidAsString()
            );
        });
    }
}
