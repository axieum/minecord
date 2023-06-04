package me.axieum.mcmod.minecord.impl.chat.callback.discord;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Formatting;

import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.impl.chat.util.MinecraftDispatcher;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.markdown;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;
import static me.axieum.mcmod.minecord.api.util.StringUtils.discordToMinecraft;
import static me.axieum.mcmod.minecord.impl.chat.MinecordChat.LOGGER;
import static me.axieum.mcmod.minecord.impl.chat.MinecordChat.getConfig;

/**
 * A listener for when a Discord user edits their recently sent message.
 */
public class MessageUpdateListener extends ListenerAdapter
{
    /** A circular mapping of message ids, with a fixed size. */
    private static final CircularLinkedHashMap<String, Message> MESSAGE_CACHE = new CircularLinkedHashMap<>(32);
    /** A text diff generator for message updates. */
    private static final DiffRowGenerator DIFF_GENERATOR = DiffRowGenerator.create()
        .showInlineDiffs(true)
        .mergeOriginalRevised(true)
        .inlineDiffByWord(true)
        .oldTag(f -> f ? Formatting.RED + "~~" : "~~" + Formatting.RESET)
        .newTag(f -> (f ? Formatting.GREEN : Formatting.RESET).toString())
        .build();

    @Override
    public void onMessageUpdate(MessageUpdateEvent event)
    {
        // Only listen to message updates if we have the original cached
        MESSAGE_CACHE.computeIfPresent(event.getMessageId(), (id, context) -> {
            // Compute the textual difference
            final String original = discordToMinecraft(context.getContentDisplay()),
                message = discordToMinecraft(event.getMessage().getContentDisplay());
            final List<DiffRow> diffs = DIFF_GENERATOR.generateDiffRows(
                Collections.singletonList(original), Collections.singletonList(message)
            );

            // If, and only if there is a visual difference, forward the event
            if (!diffs.isEmpty()) {
                final long channelId = event.getChannel().getIdLong();

                /*
                 * Prepare the message placeholders.
                 */

                final @Nullable PlaceholderContext ctx = PlaceholdersExt.getMinecordServerContext();
                final Map<String, PlaceholderHandler> placeholders = Map.of(
                    // The author's tag (i.e. username#discriminator), e.g. Axieum#1001
                    "tag", string(event.getAuthor().getAsTag()),
                    // The author's username, e.g. Axieum
                    "username", string(event.getAuthor().getName()),
                    // The author's username discriminator, e.g. 1001
                    "discriminator", string(event.getAuthor().getDiscriminator()),
                    // The author's nickname or username
                    "author", string(
                        event.getMember() != null ? event.getMember().getEffectiveName() : event.getAuthor().getName()
                    ),
                    // The old formatted message contents
                    "original", markdown(original),
                    // The old raw message contents
                    "original_raw", string(context.getContentRaw()),
                    // The new formatted message contents
                    "message", markdown(message),
                    // The new raw message contents
                    "raw", string(event.getMessage().getContentRaw()),
                    // The difference between the original and new message
                    "diff", markdown(diffs.get(0).getOldLine())
                );

                /*
                 * Dispatch the message.
                 */

                MinecraftDispatcher.dispatch(
                    entry -> PlaceholdersExt.parseText(entry.minecraft.editNode, ctx, placeholders),
                    entry -> entry.minecraft.edit != null && entry.id == channelId
                );
                LOGGER.info(PlaceholdersExt.parseString("@${tag} > ${message}", ctx, placeholders));
            }

            // Update the message cache
            return event.getMessage();
        });
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        // Update the message cache
        if (!event.getAuthor().isBot() && getConfig().hasChannel(event.getChannel().getIdLong())) {
            MESSAGE_CACHE.put(event.getMessageId(), event.getMessage());
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event)
    {
        // Update the message cache
        MESSAGE_CACHE.remove(event.getMessageId());
    }

    /**
     * A circular {@link LinkedHashMap} with a fixed capacity that removes
     * the eldest entries to make room for newer ones.
     *
     * @param <K> the type of keys maintained by this map
     * @param <V> the type of mapped values
     */
    private static final class CircularLinkedHashMap<K, V> extends LinkedHashMap<K, V>
    {
        private final int capacity;

        /**
         * Constructs a new Linked Circular Hash Map with a maximum capacity.
         *
         * @param capacity maximum capacity
         */
        private CircularLinkedHashMap(int capacity)
        {
            super(capacity, 1.0f);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest)
        {
            return size() > capacity;
        }
    }
}
