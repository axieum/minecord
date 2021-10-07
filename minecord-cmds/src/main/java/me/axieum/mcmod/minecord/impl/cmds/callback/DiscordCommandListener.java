package me.axieum.mcmod.minecord.impl.cmds.callback;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.minecraft.server.MinecraftServer;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.cmds.MinecordCommands;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import static me.axieum.mcmod.minecord.impl.cmds.MinecordCommandsImpl.LOGGER;
import static me.axieum.mcmod.minecord.impl.cmds.MinecordCommandsImpl.getConfig;

public class DiscordCommandListener extends ListenerAdapter
{
    @Override
    public void onReady(ReadyEvent event)
    {
        // Register all commands as event listeners
        MinecordCommands.getInstance().getCommands().forEach(event.getJDA()::addEventListener);

        // Update the command list in Discord
        MinecordCommands.getInstance().updateCommandList();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        final String username = event.getUser().getAsTag();
        final String raw = event.getCommandString();

        // Lookup the command name against all registered commands
        MinecordCommands.getInstance().getCommand(event.getName()).ifPresentOrElse(command -> {
            // Let them know that their request is in-progress
            final boolean isEphemeral = command.isEphemeral();
            event.deferReply(isEphemeral).queue();
            event.getHook().setEphemeral(isEphemeral);

            // Attempt to run the command
            try {
                // Check whether Minecraft is required, and hence whether the server has started
                final MinecraftServer server = Minecord.getInstance().getMinecraft().orElse(null);
                if (command.requiresMinecraft() && (server == null || server.getTickTime() == 0)) {
                    LOGGER.warn("@{} used '{}' but the server is not yet ready!", username, raw);
                    event.getHook().sendMessageEmbeds(
                        new EmbedBuilder().setColor(0xff8800).setDescription(getConfig().messages.unavailable).build()
                    ).queue();
                    return;
                }

                // Attempt to cascade the event to the matched command
                LOGGER.info("@{} used '{}'", username, raw);
                command.execute(event, server);
            } catch (Exception e) {
                LOGGER.error("@{} failed to use '{}'", username, raw, e);
                event.getHook().sendMessageEmbeds(
                    new EmbedBuilder().setColor(0xff0000).setDescription(
                        new StringTemplate().add("reason", e.getMessage()).format(getConfig().messages.failed)
                    ).build()
                ).queue();
            }
        }, () -> LOGGER.warn("@{} used an unknown command '{}'", username, raw));
    }
}
