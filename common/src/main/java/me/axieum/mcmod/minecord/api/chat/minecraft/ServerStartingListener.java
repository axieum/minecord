package me.axieum.mcmod.minecord.api.chat.minecraft;

import java.util.Collections;
import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;

import net.minecraft.server.MinecraftServer;

import dev.architectury.event.events.common.LifecycleEvent;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.DiscordDispatcher;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;

/**
 * A listener for when the Minecraft server begins starting.
 */
public class ServerStartingListener implements LifecycleEvent.ServerState
{
    @Override
    public void stateChanged(MinecraftServer server)
    {
        Minecord.getJDA().ifPresent(jda -> {
            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(server);
            final Map<String, PlaceholderHandler> placeholders = Collections.emptyMap();

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embed(
                (embed, entry) -> embed.setDescription(
                    PlaceholdersExt.parseString(entry.discord.startingNode, ctx, placeholders)
                ),
                entry -> entry.discord.starting != null
            );
        });
    }
}
