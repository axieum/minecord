package me.axieum.mcmod.minecord.impl.cmds.command.discord;

import java.lang.management.ManagementFactory;
import java.time.Duration;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import me.axieum.mcmod.minecord.api.cmds.command.MinecordCommand;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.cmds.config.CommandConfig;
import static me.axieum.mcmod.minecord.impl.cmds.MinecordCommandsImpl.getConfig;

/**
 * Built-in uptime Minecord command.
 */
public class UptimeCommand extends MinecordCommand
{
    // todo: Prepare a reusable string template for all uptime commands

    /**
     * Initialises a new uptime command.
     *
     * @param config uptime command config
     */
    public UptimeCommand(CommandConfig.BaseCommand config)
    {
        super(config.name, config.description);
        data.setDefaultEnabled(config.allowByDefault);
    }

    @Override
    public boolean requiresMinecraft()
    {
        // We only report the total uptime of the process, not the server itself!
        return false;
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event, @Nullable MinecraftServer server)
    {
        event.replyEmbeds(
            new EmbedBuilder().setDescription(
                new StringTemplate()
                    .add("uptime", Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()))
                    .format(getConfig().builtin.uptime.message)
            ).build()
        ).queue();
    }
}
