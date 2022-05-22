package me.axieum.mcmod.minecord.impl.cmds.command.discord;

import java.awt.Color;
import java.util.stream.LongStream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import me.axieum.mcmod.minecord.api.cmds.command.MinecordCommand;
import me.axieum.mcmod.minecord.api.cmds.event.MinecordCommandEvents;
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
    public TPSCommand(CommandConfig.BaseCommandSchema config)
    {
        super(config.name, config.description);
        data.setDefaultEnabled(config.allowByDefault);
        setEphemeral(config.ephemeral);
        setCooldown(config.cooldown);
        setCooldownScope(config.cooldownScope);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @Nullable MinecraftServer server)
    {
        assert server != null;

        // Average the last tick lengths, and convert from nanoseconds to milliseconds
        final double meanTPSTime = LongStream.of(server.lastTickLengths).average().orElse(0) * 1e-6d;
        // Compute the server's mean ticks per second
        final double meanTPS = Math.min(1000f / meanTPSTime, 20);

        // Prepare an embed to be sent to the user
        EmbedBuilder embed = new EmbedBuilder()
            // Set the message
            .setDescription(String.format("%.2f TPS @ %.3fms", meanTPS, meanTPSTime))
            // Set the embed colour on a red to green scale (scale down to a 4-step gradient)
            .setColor(Color.HSBtoRGB(Math.round(meanTPS / 5d) / 4f / 3f, 1f, 1f));

        // Fire an event to allow the embed to be mutated or cancelled
        embed = MinecordCommandEvents.TPS.AFTER_EXECUTE.invoker().onAfterExecuteTPS(event, server, embed);

        // Build and reply with the resulting embed
        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
