package me.axieum.mcmod.minecord.api.chat.minecraft;

import java.awt.Color;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.Nullable;

import net.minecraft.CrashReport;
import net.minecraft.server.MinecraftServer;

import dev.architectury.event.events.common.LifecycleEvent;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.DiscordDispatcher;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.duration;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when the Minecraft server shutdown or crashed.
 */
public class ServerStoppedListener implements LifecycleEvent.ServerState
{
    /** The crash report if present. */
    public static @Nullable CrashReport crashReport = null;

    @Override
    public void stateChanged(MinecraftServer server)
    {
        Minecord.getJDA().ifPresent(jda -> {
            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(server);
            final Map<String, PlaceholderHandler> placeholders = new HashMap<>(Map.of(
                // The total time for which the server has been online for
                "uptime", duration(Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()))
            ));
            // The reason for the server stopping, if crashed
            if (crashReport != null) placeholders.put("reason", string(crashReport.getExceptionMessage()));

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
                final Optional<File> file = Optional.ofNullable(crashReport.getSaveFile())
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
