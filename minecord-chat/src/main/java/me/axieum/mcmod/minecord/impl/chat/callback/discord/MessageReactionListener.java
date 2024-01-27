package me.axieum.mcmod.minecord.impl.chat.callback.discord;

import java.util.Map;

import com.vdurmont.emoji.EmojiParser;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;

import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.impl.chat.util.MinecraftDispatcher;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;
import static me.axieum.mcmod.minecord.impl.chat.MinecordChat.LOGGER;
import static me.axieum.mcmod.minecord.impl.chat.MinecordChat.getConfig;

/**
 * A listener for when a Discord user reacts to a message.
 */
public class MessageReactionListener extends ListenerAdapter
{
    @Override
    public void onGenericMessageReaction(GenericMessageReactionEvent event)
    {
        // Ignore the reaction if there is no member associated with it
        if (event.getMember() == null) return;

        // Ignore the message if not in a configured channel
        final long channelId = event.getChannel().getIdLong();
        if (!getConfig().hasChannel(channelId)) return;

        // First, retrieve the message context
        event.retrieveMessage().queue(context -> {
            // Compute some useful properties of the event
            final boolean isAdded = event instanceof MessageReactionAddEvent;
            final String emote = EmojiParser.parseToAliases(event.getEmoji().getName());

            /*
             * Prepare the message placeholders.
             */

            final @Nullable PlaceholderContext ctx = PlaceholdersExt.getMinecordServerContext();
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The issuer's tag (i.e. username#discriminator), e.g. Axieum#1001
                "issuer_tag", string(event.getUser().getName()),
                // The issuer's username, e.g. Axieum
                "issuer_username", string(event.getUser().getName()),
                // The issuer's username discriminator, e.g. 1001
                "issuer_discriminator", string(event.getUser().getDiscriminator()),
                // The issuer's nickname or username
                "issuer", string(
                    event.getMember() != null ? event.getMember().getEffectiveName() : event.getUser().getName()
                ),
                // The author's tag (i.e. username#discriminator), e.g. Axieum#1001
                "author_tag", string(context.getAuthor().getName()),
                // The author's username, e.g. Axieum
                "author_username", string(context.getAuthor().getName()),
                // The author's username discriminator, e.g. 1001
                "author_discriminator", string(context.getAuthor().getDiscriminator()),
                // The author's nickname or username
                "author", string(
                    context.getMember() != null ? context.getMember().getEffectiveName() : context.getAuthor().getName()
                ),
                // The emote used to react
                "emote", string(emote)
            );

            /*
             * Dispatch the message.
             */

            // A user reacted to a recent message
            if (isAdded) {
                MinecraftDispatcher.dispatch(
                    entry -> PlaceholdersExt.parseText(entry.minecraft.reactNode, ctx, placeholders),
                    entry -> entry.minecraft.react != null && entry.id == channelId
                );
                LOGGER.info(PlaceholdersExt.parseString(
                    "@${issuer_tag} reacted with ${emote} to ${author_tag}'s message", ctx, placeholders
                ));

            // A user removed their reaction from a recent message
            } else {
                MinecraftDispatcher.dispatch(
                    entry -> PlaceholdersExt.parseText(entry.minecraft.unreactNode, ctx, placeholders),
                    entry -> entry.minecraft.unreact != null && entry.id == channelId
                );
                LOGGER.info(PlaceholdersExt.parseString(
                    "@${issuer_tag} removed their reaction of ${emote} from ${author_tag}'s message",
                    ctx,
                    placeholders
                ));
            }
        });
    }
}
