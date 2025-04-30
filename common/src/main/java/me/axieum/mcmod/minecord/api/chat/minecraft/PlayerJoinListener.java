package me.axieum.mcmod.minecord.api.chat.minecraft;

import java.util.Collections;
import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import dev.architectury.event.events.common.PlayerEvent;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.DiscordDispatcher;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;

/**
 * A listener for when a player disconnects.
 */
public class PlayerJoinListener implements PlayerEvent.PlayerJoin
{
    @Override
    public void join(ServerPlayer player)
    {
        Minecord.getJDA().ifPresent(jda -> {
            final ResourceKey<Level> level = player.level().dimension();

            /*
             * Prepare the message placeholders.
             */

            final @Nullable PlaceholderContext ctx = PlaceholderContext.of(player);
            final Map<String, PlaceholderHandler> placeholders = Collections.emptyMap();

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embedWithAvatar(
                (embed, entry) -> embed.setDescription(
                    PlaceholdersExt.parseString(entry.discord.joinNode, ctx, placeholders)
                ),
                entry -> entry.discord.join != null && entry.hasWorld(level),
                player.getStringUUID()
            );
        });
    }
}
