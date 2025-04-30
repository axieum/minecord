package me.axieum.mcmod.minecord.api.chat.minecraft;

import java.util.HashMap;
import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.DiscordDispatcher;
import me.axieum.mcmod.minecord.api.event.MinecraftEvents;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when a server (or player) broadcasts a message to
 * *all* players via the {@code /say} command.
 */
public class CommandSayListener implements MinecraftEvents.Say
{
    @Override
    public void say(Component component, CommandSourceStack source)
    {
        Minecord.getJDA().ifPresent(jda -> {
            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(source);
            final Map<String, PlaceholderHandler> placeholders = new HashMap<>(Map.of(
                // The formatted message contents
                "message", string(StringUtils.minecraftToDiscord(component.getString()))
            ));

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.dispatch(
                (embed, entry) -> embed.setContent(
                    PlaceholdersExt.parseString(entry.discord.sayNode, ctx, placeholders)
                ),
                entry -> entry.discord.say != null
            );
        });
    }
}
