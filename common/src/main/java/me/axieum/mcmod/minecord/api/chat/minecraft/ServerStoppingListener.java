package me.axieum.mcmod.minecord.api.chat.minecraft;

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
 * A listener for when the Minecraft server begins stopping.
 */
public class ServerStoppingListener implements LifecycleEvent.ServerState
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
                // The total time for which the server has been online for
                "uptime", duration(Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embed(
                (embed, entry) -> embed.setDescription(
                    PlaceholdersExt.parseString(entry.discord.stoppingNode, ctx, placeholders)
                ),
                entry -> entry.discord.stopping != null
            );
        });
    }
}
