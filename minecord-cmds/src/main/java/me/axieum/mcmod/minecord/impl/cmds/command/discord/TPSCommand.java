package me.axieum.mcmod.minecord.impl.cmds.command.discord;

import java.awt.Color;
import java.util.stream.LongStream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import me.axieum.mcmod.minecord.api.cmds.command.MinecordCommand;
import me.axieum.mcmod.minecord.impl.cmds.config.CommandConfig;

/**
 * Built-in ticks-per-second (TPS) Minecord command.
 */
public class TPSCommand extends MinecordCommand
{
    /**
     * Initialises a new ticks-per-second (TPS) command.
     *
     * @param config ticks-per-second (TPS) command config
     */
    public TPSCommand(CommandConfig.BaseCommand config)
    {
        super(config.name, config.description);
        data.setDefaultEnabled(config.allowByDefault);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event, @Nullable MinecraftServer server)
    {
        assert server != null;

        // Average the last tick lengths, and convert from nanoseconds to milliseconds
        final double meanTPSTime = LongStream.of(server.lastTickLengths).average().orElse(0) * 1e-6d;
        // Compute the server's mean ticks per second
        final double meanTPS = Math.min(1000f / meanTPSTime, 20);

        // Build and send a message embed
        event.replyEmbeds(new EmbedBuilder()
            // Set the message
            .setDescription(String.format("%.2f TPS @ %.3fms", meanTPS, meanTPSTime))
            // Set the embed colour on a red to green scale (scale down to a 4-step gradient)
            .setColor(Color.HSBtoRGB(Math.round(meanTPS / 5d) / 4f / 3f, 1f, 1f))
            // Build the embed
            .build()
        ).queue();
    }
}
