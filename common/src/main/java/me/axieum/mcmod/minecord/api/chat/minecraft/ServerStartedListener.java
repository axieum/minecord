package me.axieum.mcmod.minecord.api.chat.minecraft;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;

import net.minecraft.server.MinecraftServer;

import dev.architectury.event.events.common.LifecycleEvent;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.DiscordDispatcher;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.duration;

/**
 * A listener for when the Minecraft server has started.
 */
public class ServerStartedListener implements LifecycleEvent.ServerState
{
    @Override
    public void stateChanged(MinecraftServer server)
    {
        Minecord.getJDA().ifPresent(jda -> {
            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(server);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The time taken for the server to start
                "uptime", duration(Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embed(
                (embed, entry) -> embed.setColor(Color.GREEN).setDescription(
                    PlaceholdersExt.parseString(entry.discord.startedNode, ctx, placeholders)
                ),
                entry -> entry.discord.started != null
            );
        });
    }
}
