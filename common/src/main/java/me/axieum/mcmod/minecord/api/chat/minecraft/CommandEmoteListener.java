package me.axieum.mcmod.minecord.api.chat.minecraft;

import java.util.HashMap;
import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import org.jetbrains.annotations.Nullable;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.DiscordDispatcher;
import me.axieum.mcmod.minecord.api.event.MinecraftEvents;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import static me.axieum.mcmod.minecord.api.chat.minecraft.ChatReceivedListener.replaceLinks;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when a player broadcasts a message via the {@code /me} command.
 */
public class CommandEmoteListener implements MinecraftEvents.Emote
{
    @Override
    public void emote(Component component, CommandSourceStack source)
    {
        Minecord.getJDA().ifPresent(jda -> {
            final @Nullable ServerPlayer player = source.getPlayer();
            final ResourceKey<Level> level = source.getLevel().dimension();

            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(source);
            final Map<String, PlaceholderHandler> placeholders = new HashMap<>(Map.of(
                // The formatted message contents
                "action", string(StringUtils.minecraftToDiscord(component.getString()))
            ));

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.dispatch(
                (embed, entry) -> embed.setContent(
                    replaceLinks(PlaceholdersExt.parseString(entry.discord.emoteNode, ctx, placeholders), entry)
                ),
                entry -> entry.discord.emote != null && (player == null || entry.hasWorld(level))
            );
        });
    }
}
