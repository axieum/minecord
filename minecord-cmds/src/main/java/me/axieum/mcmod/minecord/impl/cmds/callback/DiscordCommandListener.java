package me.axieum.mcmod.minecord.impl.cmds.callback;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.cmds.MinecordCommands;
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
            try {
                // Check whether Minecraft is required, and hence whether the server has started
                if (command.requiresMinecraft()) {
                    if (Minecord.getInstance().getMinecraft().filter(server -> server.getTickTime() > 0).isEmpty()) {
                        LOGGER.warn("@{} used '{}' but the server was not yet ready!", username, raw);
                        event.replyEmbeds(new EmbedBuilder().setColor(0xff8800)
                                                            .setDescription(getConfig().messages.unavailable)
                                                            .build()).queue();
                        return;
                    }
                }

                // Attempt to cascade the event to the matched command
                LOGGER.info("@{} used '{}'", username, raw);
                command.execute(event);
            } catch (Throwable e) {
                LOGGER.error("@{} failed to use '{}'", username, raw, e);
            }
        }, () -> LOGGER.warn("@{} used an unknown command '{}'", username, raw));
    }
}
