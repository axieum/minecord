package me.axieum.mcmod.minecord.api.cmds.builtin;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import me.axieum.mcmod.minecord.api.cmds.MinecordCommand;
import me.axieum.mcmod.minecord.api.config.CommandConfig;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.duration;

/**
 * Built-in uptime Minecord command.
 */
public class UptimeCommand extends MinecordCommand
{
    /** Reusable placeholders for all uptime commands. */
    public static final Map<String, PlaceholderHandler> PLACEHOLDERS = Map.of(
        // The total process uptime (to the nearest minute)
        "uptime", duration(() ->
            Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()).truncatedTo(ChronoUnit.MINUTES)
        )
    );

    /**
     * Constructs a new uptime command.
     *
     * @param config uptime command config
     */
    public UptimeCommand(CommandConfig.BaseCommand config)
    {
        super(config.name, config.description);
        data.setDefaultPermissions(
            config.allowByDefault ? DefaultMemberPermissions.ENABLED : DefaultMemberPermissions.DISABLED
        );
        setRequiresMinecraft(false); // we only report the total uptime of the process, not the server itself!
        setEphemeral(config.ephemeral);
        setCooldown(config.cooldown);
        setCooldownScope(config.cooldownScope);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @Nullable MinecraftServer server)
    {
        // Prepare the message placeholders
        final @Nullable PlaceholderContext ctx = server != null ? PlaceholderContext.of(server) : null;

        // Prepare an embed to be sent to the user
        EmbedBuilder embed = new EmbedBuilder().setDescription(
            PlaceholdersExt.parseString(CommandConfig.BuiltinCommands.uptime.messageNode, ctx, PLACEHOLDERS)
        );

        // Build and reply with the resulting embed
        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
