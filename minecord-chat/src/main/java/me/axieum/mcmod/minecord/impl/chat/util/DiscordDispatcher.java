package me.axieum.mcmod.minecord.impl.chat.util;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.Nullable;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.impl.chat.config.ChatConfig;
import me.axieum.mcmod.minecord.impl.chat.config.ChatConfig.ChatEntrySchema;
import static me.axieum.mcmod.minecord.impl.chat.MinecordChat.LOGGER;
import static me.axieum.mcmod.minecord.impl.chat.MinecordChat.getConfig;

/**
 * Utility methods for dispatching configured messages to Discord.
 *
 * @see ChatConfig#entries
 * @see ChatEntrySchema.DiscordSchema
 */
public final class DiscordDispatcher
{
    private DiscordDispatcher() {}

    /**
     * Builds and queues embed messages for each configured chat entry.
     *
     * @param builder   consumer to modify the Discord embed builder for a chat entry before queuing
     * @param predicate predicate that filters configured chat entries
     * @see #embed(BiConsumer, BiConsumer, Predicate)
     */
    public static void embed(BiConsumer<EmbedBuilder, ChatEntrySchema> builder, Predicate<ChatEntrySchema> predicate)
    {
        embed(builder, (action, entry) -> action.queue(), predicate);
    }

    /**
     * Builds and queues embed messages with Minecraft player avatars for each configured chat entry.
     *
     * @param builder   consumer to modify the Discord embed builder for a chat entry before queuing
     * @param predicate predicate that filters configured chat entries
     * @param username  Minecraft player username for the avatar embed thumbnail
     * @see #embedWithAvatar(BiConsumer, Predicate, String)
     */
    public static void embedWithAvatar(
        BiConsumer<EmbedBuilder, ChatEntrySchema> builder,
        Predicate<ChatEntrySchema> predicate,
        @Nullable String username
    )
    {
        embedWithAvatar(builder, (action, entry) -> action.queue(), predicate, username);
    }

    /**
     * Builds and acts on embed messages for each configured chat entry.
     *
     * @param builder   consumer to modify the Discord embed builder for a chat entry
     * @param action    consumer to act upon the resulting Discord message action
     * @param predicate predicate that filters configured chat entries
     * @see #dispatch(BiConsumer, BiConsumer, Predicate)
     */
    public static void embed(
        BiConsumer<EmbedBuilder, ChatEntrySchema> builder,
        BiConsumer<MessageCreateAction, ChatEntrySchema> action,
        Predicate<ChatEntrySchema> predicate
    )
    {
        dispatch(
            (message, entry) ->
                builder.andThen((m, e) -> message.setEmbeds(m.build()))
                    .accept(new EmbedBuilder(), entry),
            action,
            predicate
        );
    }

    /**
     * Builds and acts on embed messages with Minecraft player avatars for each configured chat entry.
     *
     * @param builder   consumer to modify the Discord embed builder for a chat entry before queuing
     * @param action    consumer to act upon the resulting Discord message action
     * @param predicate predicate that filters configured chat entries
     * @param username  Minecraft player username for the avatar embed thumbnail
     * @see #embed(BiConsumer, BiConsumer, Predicate)
     */
    public static void embedWithAvatar(
        BiConsumer<EmbedBuilder, ChatEntrySchema> builder,
        BiConsumer<MessageCreateAction, ChatEntrySchema> action,
        Predicate<ChatEntrySchema> predicate,
        @Nullable String username
    )
    {
        embed(
            (message, entry) -> {
                Minecord.getInstance().getAvatarUrl(username, 16).ifPresent(message::setThumbnail);
                builder.accept(message, entry);
            },
            action,
            predicate
        );
    }

    /**
     * Builds and queues messages for each configured chat entry.
     *
     * @param builder   consumer to modify the Discord message builder for a chat entry before queuing
     * @param predicate predicate that filters configured chat entries
     * @see #dispatch(BiConsumer, BiConsumer, Predicate)
     */
    public static void dispatch(
        BiConsumer<MessageCreateBuilder, ChatEntrySchema> builder, Predicate<ChatEntrySchema> predicate
    )
    {
        dispatch(builder, (action, entry) -> action.queue(), predicate);
    }

    /**
     * Builds and acts on messages for each configured chat entry.
     *
     * @param builder   consumer to modify the Discord embed builder for a chat entry
     * @param action    consumer to act upon the resulting Discord message action
     * @param predicate predicate that filters configured chat entries
     * @see ChatConfig#entries
     */
    public static void dispatch(
        BiConsumer<MessageCreateBuilder, ChatEntrySchema> builder,
        BiConsumer<MessageCreateAction, ChatEntrySchema> action,
        Predicate<ChatEntrySchema> predicate
    )
    {
        Minecord.getInstance().getJDA().ifPresent(jda ->
            // Prepare a stream of configured chat entries
            Arrays.stream(getConfig().entries)
                  .parallel()
                  // Filter message entries
                  .filter(predicate)
                  // Build and queue each chat entry
                  .forEach(entry -> {
                      // Fetch the channel
                      final @Nullable TextChannel channel = jda.getTextChannelById(entry.id);

                      // Check that the channel exists
                      if (channel == null)
                          LOGGER.warn("Could not find Discord channel with identifier {}", entry.id);

                      // Check that we have permission to the channel
                      else if (!channel.canTalk())
                          LOGGER.warn("Missing permissions for Discord channel with identifier {}", entry.id);

                      // Build and dispatch the message
                      else
                          builder.andThen((m, e) -> action.accept(channel.sendMessage(m.build()), e))
                                 .accept(new MessageCreateBuilder(), entry);
                  })
        );
    }
}
