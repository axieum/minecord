package me.axieum.mcmod.minecord.impl.cmds.command.discord;

import java.lang.management.ManagementFactory;
import java.time.Duration;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import me.axieum.mcmod.minecord.api.cmds.command.MinecordCommand;
import me.axieum.mcmod.minecord.api.cmds.event.MinecordCommandEvents;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.cmds.config.CommandConfig;
import static me.axieum.mcmod.minecord.impl.cmds.MinecordCommandsImpl.getConfig;

/**
 * Built-in uptime Minecord command.
 */
public class UptimeCommand extends MinecordCommand
{
    /** A reusable string template for all uptime commands. */
    public static final StringTemplate TEMPLATE = new StringTemplate()
        .add("uptime", () -> Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()));

    /**
     * Constructs a new uptime command.
     *
     * @param config uptime command config
     */
    public UptimeCommand(CommandConfig.BaseCommandSchema config)
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
        // Prepare an embed to be sent to the user
        EmbedBuilder embed = new EmbedBuilder().setDescription(TEMPLATE.format(getConfig().builtin.uptime.message));

        // Fire an event to allow the embed to be mutated
        embed = MinecordCommandEvents.Builtin.UPTIME.invoker().onUptimeCommand(this, event, server, embed);

        // Build and reply with the resulting embed
        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
