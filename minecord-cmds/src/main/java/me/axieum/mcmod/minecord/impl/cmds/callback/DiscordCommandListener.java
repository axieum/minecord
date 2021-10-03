package me.axieum.mcmod.minecord.impl.cmds.callback;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import me.axieum.mcmod.minecord.api.cmds.MinecordCommands;
import static me.axieum.mcmod.minecord.impl.cmds.MinecordCommandsImpl.LOGGER;

public class DiscordCommandListener extends ListenerAdapter
{
    @Override
    public void onReady(ReadyEvent event)
    {
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
                // Attempt to cascade the event to the matched command
                LOGGER.info("@{} used the '{}' command", username, raw);
                command.execute(event);
            } catch (Throwable e) {
                LOGGER.error("@{} failed to use the '{}' command!", username, raw, e);
            }
        }, () -> LOGGER.warn("@{} used an unknown command: {}", username, raw));
    }
}
