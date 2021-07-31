package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.awt.Color;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.event.ServerShutdownCallback;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

public class ServerLifecycleCallback implements ServerStarting, ServerStarted, ServerStopping, ServerShutdownCallback
{
    @Override
    public void onServerStarting(MinecraftServer server)
    {
        Minecord.getInstance().getJDA().ifPresent(jda ->
            // Dispatch the message
            DiscordDispatcher.embed((embed, entry) ->
                    embed.setDescription(entry.discord.starting),
                entry -> entry.discord.starting != null)
        );
    }

    @Override
    public void onServerStarted(MinecraftServer server)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare a message formatter.
             */

            final Map<String, Object> values = new HashMap<>();

            // The time taken for the server to start
            values.put("uptime", Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()));

            final StrSubstitutor formatter = new StrSubstitutor(values);

            /*
             * Dispatch the message.
             */
            DiscordDispatcher.embed((embed, entry) ->
                    embed.setColor(Color.GREEN).setDescription(formatter.replace(entry.discord.started)),
                entry -> entry.discord.started != null);
        });
    }

    @Override
    public void onServerStopping(MinecraftServer server)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare a message formatter.
             */

            final Map<String, Object> values = new HashMap<>();

            // The total time for which the server has been online for
            values.put("uptime", Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()));

            final StrSubstitutor formatter = new StrSubstitutor(values);

            /*
             * Dispatch the message.
             */
            DiscordDispatcher.embed((embed, entry) ->
                    embed.setDescription(formatter.replace(entry.discord.stopping)),
                entry -> entry.discord.stopping != null);
        });
    }

    @Override
    public void onServerShutdown(MinecraftServer server, @Nullable CrashReport crashReport)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare a message formatter.
             */

            final Map<String, Object> values = new HashMap<>();

            // The total time for which the server has been online for
            values.put("uptime", Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()));
            // The reason for the server stopping, if crashed
            if (crashReport != null) values.put("reason", crashReport.getMessage());

            final StrSubstitutor formatter = new StrSubstitutor(values);

            /*
             * Dispatch the message.
             */

            // The server stopped normally
            if (crashReport == null) {
                DiscordDispatcher.embed((embed, entry) ->
                        embed.setColor(Color.RED).setDescription(formatter.replace(entry.discord.stopped)),
                    entry -> entry.discord.stopped != null);

            // The server stopped due to an error
            } else {
                // Fetch the crash report file
                final Optional<File> file = Optional.ofNullable(crashReport.getFile()).filter(File::exists);

                // Dispatch the message
                DiscordDispatcher.embed((embed, entry) ->
                        embed.setColor(Color.ORANGE).setDescription(formatter.replace(entry.discord.crashed)),
                    (action, entry) -> {
                        // Conditionally attach the crash report if required
                        if (entry.discord.uploadCrashReport)
                            file.ifPresent(action::addFile);
                        action.queue();
                    },
                    entry -> entry.discord.crashed != null);
            }
        });
    }
}
