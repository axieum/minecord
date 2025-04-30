package me.axieum.mcmod.minecord.api.chat.minecraft;

import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import org.jetbrains.annotations.Nullable;
import static net.dv8tion.jda.api.EmbedBuilder.URL_PATTERN;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.DiscordDispatcher;
import me.axieum.mcmod.minecord.api.config.ChatConfig;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when a chat message is received.
 */
public class ChatReceivedListener implements ChatEvent.Received
{
    @Override
    public EventResult received(@Nullable ServerPlayer player, Component component)
    {
        Minecord.getJDA().ifPresent(jda -> {
            final ResourceKey<Level> level = player.level().dimension();

            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(player);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The formatted message contents
                "message", string(StringUtils.minecraftToDiscord(component.getString()))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.dispatch(
                (embed, entry) -> embed.setContent(
                    replaceLinks(PlaceholdersExt.parseString(entry.discord.chatNode, ctx, placeholders), entry)
                ),
                entry -> entry.discord.chat != null && entry.hasWorld(level)
            );
        });

        return EventResult.pass();
    }

    /**
     * Wrapper method for replacing URLs found in messages if configured to disallow links.
     *
     * @param text formatted message
     * @param entry chat config entry
     * @return sanitised text
     */
    public static String replaceLinks(String text, ChatConfig.ChatEntry entry)
    {
        return entry.discord.purgeLinks ? URL_PATTERN.matcher(text).replaceAll(" … ") : text;
    }
}
