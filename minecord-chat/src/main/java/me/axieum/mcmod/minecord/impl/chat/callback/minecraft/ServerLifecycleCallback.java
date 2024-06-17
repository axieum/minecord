package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.awt.Color;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.event.ServerShutdownCallback;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.duration;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when the Minecraft server starts, stops or crashes.
 */
public class ServerLifecycleCallback implements ServerStarting, ServerStarted, ServerStopping, ServerShutdownCallback
{
    @Override
    public void onServerStarting(MinecraftServer server)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
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

    @Override
    public void onServerStarted(MinecraftServer server)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
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

    @Override
    public void onServerStopping(MinecraftServer server)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
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

    @Override
    public void onServerShutdown(MinecraftServer server, @Nullable CrashReport crashReport)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(server);
            final Map<String, PlaceholderHandler> placeholders = new HashMap<>(Map.of(
                // The total time for which the server has been online for
                "uptime", duration(Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()))
            ));
            // The reason for the server stopping, if crashed
            if (crashReport != null) placeholders.put("reason", string(crashReport.getMessage()));

            /*
             * Dispatch the message.
             */

            // The server stopped normally
            if (crashReport == null) {
                DiscordDispatcher.embed(
                    (embed, entry) -> embed.setColor(Color.RED).setDescription(
                        PlaceholdersExt.parseString(entry.discord.stoppedNode, ctx, placeholders)
                    ),
                    entry -> entry.discord.stopped != null
                );

            // The server stopped due to an error
            } else {
                // Fetch the crash report file
                final Optional<File> file = Optional.ofNullable(crashReport.getFile())
                    .map(Path::toFile)
                    .filter(File::exists);

                // Dispatch the message
                DiscordDispatcher.embed(
                    (embed, entry) -> embed.setColor(Color.ORANGE).setDescription(
                        PlaceholdersExt.parseString(entry.discord.crashedNode, ctx, placeholders)
                    ),
                    (action, entry) -> {
                        // Conditionally attach the crash report if required
                        if (entry.discord.uploadCrashReport)
                            file.map(FileUpload::fromData).ifPresent(action::addFiles);
                        action.queue();
                    },
                    entry -> entry.discord.crashed != null
                );
            }
        });
    }
}
