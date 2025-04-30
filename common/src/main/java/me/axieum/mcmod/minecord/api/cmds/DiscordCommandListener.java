package me.axieum.mcmod.minecord.api.cmds;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.MinecordCommands;
import me.axieum.mcmod.minecord.api.config.CommandConfig;
import me.axieum.mcmod.minecord.api.event.JDAEvents;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import static me.axieum.mcmod.minecord.api.Minecord.LOGGER;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.duration;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for Discord commands.
 */
public final class DiscordCommandListener extends ListenerAdapter
{
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        final String username = event.getUser().getName();
        final String raw = event.getCommandString();

        // Lookup the command name against all registered commands
        MinecordCommands.getCommand(event.getName()).ifPresentOrElse(command -> {
            // Let them know that their request is in-progress
            final boolean isEphemeral = command.isEphemeral();
            event.deferReply(isEphemeral).queue();
            event.getHook().setEphemeral(isEphemeral);

            // Fetch the Minecraft server instance
            final @Nullable MinecraftServer server = Minecord.getMinecraft().orElse(null);
            final @Nullable PlaceholderContext pCtx = server != null ? PlaceholderContext.of(server) : null;

            // Attempt to run the command
            try {
                // Check whether Minecraft is required, and hence whether the server has started
                if (command.requiresMinecraft() && (server == null || server.getAverageTickTimeNanos() == 0)) {
                    LOGGER.warn("@{} used '{}' but the server is not yet ready!", username, raw);
                    event.getHook().sendMessageEmbeds(
                        new EmbedBuilder().setColor(0xff8800).setDescription(
                            PlaceholdersExt.parseString(
                                CommandConfig.Messages.unavailableNode, pCtx, Collections.emptyMap()
                            )
                        ).build()
                    ).queue();
                    return;
                }

                // Check and apply any command cooldowns where required
                if (command.getCooldown() > 0) {
                    final String cooldownKey = command.getCooldownScope().getKey(event);
                    final int remaining = MinecordCommands.getCooldown(cooldownKey);
                    if (remaining > 0) {
                        LOGGER.warn("@{} used '{}' but must wait another {} seconds!", username, raw, remaining);
                        event.getHook().setEphemeral(true).sendMessageEmbeds(
                            new EmbedBuilder().setColor(0xff8800).setDescription(
                                PlaceholdersExt.parseString(
                                    CommandConfig.Messages.cooldownNode, pCtx, Map.of(
                                        // The total cooldown before the command can be used again
                                        "cooldown", duration(Duration.ofSeconds(command.getCooldown())),
                                        // The remaining time before the command can be used again
                                        "remaining", duration(Duration.ofSeconds(remaining))
                                    )
                                )
                            ).build()
                        ).queue();
                        return;
                    } else {
                        LOGGER.debug("Applying cooldown '{}' for {} seconds", cooldownKey, command.getCooldown());
                        MinecordCommands.applyCooldown(cooldownKey, command.getCooldown());
                    }
                }

                // Fire an event to allow the command execution to be cancelled
                if (JDAEvents.ALLOW_COMMAND.invoker().shouldAllowCommand(command, event, server).isFalse()) {
                    LOGGER.debug("@{} used '{}' but its execution was externally cancelled!", username, raw);
                    return;
                }

                // Attempt to cascade the event to the matched command
                LOGGER.info("@{} used '{}'", username, raw);
                command.execute(event, server);
            } catch (Exception e) {
                LOGGER.error("@{} failed to use '{}'", username, raw, e);
                event.getHook().sendMessageEmbeds(
                    new EmbedBuilder().setColor(0xff0000).setDescription(
                        PlaceholdersExt.parseString(
                            CommandConfig.Messages.failedNode, pCtx, Map.of(
                                // The reason for the command failing
                                "reason", string(e.getMessage())
                            )
                        )
                    ).build()
                ).queue();
            } finally {
                // Clear any inactive/stale cooldowns
                MinecordCommands.clearInactiveCooldowns();
            }
        }, () -> LOGGER.warn("@{} used an unknown command '{}'", username, raw));
    }
}
