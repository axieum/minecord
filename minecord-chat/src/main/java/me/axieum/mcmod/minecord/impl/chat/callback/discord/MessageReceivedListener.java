package me.axieum.mcmod.minecord.impl.chat.callback.discord;

import java.util.HashMap;
import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;

import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import me.axieum.mcmod.minecord.impl.chat.util.MinecraftDispatcher;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.markdown;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;
import static me.axieum.mcmod.minecord.api.util.StringUtils.discordToMinecraft;
import static me.axieum.mcmod.minecord.impl.chat.MinecordChat.LOGGER;
import static me.axieum.mcmod.minecord.impl.chat.MinecordChat.getConfig;

/**
 * A listener for new Discord messages.
 */
public class MessageReceivedListener extends ListenerAdapter
{
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        // Ignore the message if the author is a bot, or is not in a configured channel
        if (event.getAuthor().isBot()) return;
        if (!getConfig().hasChannel(event.getChannel().getIdLong())) return;

        // Push any message text content
        if (!event.getMessage().getContentRaw().isEmpty())
            onText(event);

        // Link any stickers
        event.getMessage().getStickers().forEach(sticker -> onSticker(event, sticker));

        // Link any attachments
        event.getMessage().getAttachments().forEach(attachment -> onAttachment(event, attachment));
    }

    /**
     * Handles the text component of a given message received event.
     *
     * @param event message received event
     */
    public void onText(MessageReceivedEvent event)
    {
        final long channelId = event.getChannel().getIdLong();
        final @Nullable Message replyMessage = event.getMessage().getReferencedMessage();

        /*
         * Prepare the message placeholders.
         */

        final @Nullable PlaceholderContext ctx = PlaceholdersExt.getMinecordServerContext();
        final Map<String, PlaceholderHandler> placeholders = new HashMap<>(Map.of(
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
            // The formatted message contents
            "message", markdown(discordToMinecraft(event.getMessage().getContentDisplay())),
            // The raw message contents
            "raw", string(event.getMessage().getContentRaw())
        ));

        // The message is in reply to another
        if (replyMessage != null) {
            placeholders.putAll(Map.of(
                // The replied message author's tag (i.e. username#discriminator), e.g. Axieum#1001
                "reply_tag", string(replyMessage.getAuthor().getAsTag()),
                // The replied message author's username, e.g. Axieum
                "reply_username", string(replyMessage.getAuthor().getName()),
                // The replied message author's username discriminator, e.g. 1001
                "reply_discriminator", string(replyMessage.getAuthor().getDiscriminator()),
                // The replied message author's nickname or username
                "reply_author", string(
                    replyMessage.getMember() != null
                        ? replyMessage.getMember().getEffectiveName()
                        : replyMessage.getAuthor().getName()
                ),
                // The replied message formatted message contents
                "reply_message", markdown(discordToMinecraft(replyMessage.getContentDisplay())),
                // The replied message raw message contents
                "reply_raw", string(replyMessage.getContentRaw())
            ));
        }

        /*
         * Dispatch the message.
         */

        // The message is a standalone message
        if (replyMessage == null) {
            MinecraftDispatcher.dispatch(
                entry -> PlaceholdersExt.parseText(entry.minecraft.chatNode, ctx, placeholders),
                entry -> entry.minecraft.chat != null && entry.id == channelId
            );
            LOGGER.info(PlaceholdersExt.parseString("@${tag} > ${message}", ctx, placeholders));

        // The message is in reply to another
        } else {
            MinecraftDispatcher.dispatch(
                entry -> PlaceholdersExt.parseText(entry.minecraft.replyNode, ctx, placeholders),
                entry -> entry.minecraft.reply != null && entry.id == channelId
            );
            LOGGER.info(PlaceholdersExt.parseString(
                "@${tag} (in reply to @${reply_tag}) > ${message}", ctx, placeholders
            ));
        }
    }

    /**
     * Handles a sticker of a given message received event.
     *
     * @param event message received event
     * @param sticker message sticker
     */
    public void onSticker(MessageReceivedEvent event, StickerItem sticker)
    {
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
            // The link to the sticker image
            "url", string(sticker.getIconUrl()),
            // The name of the sticker
            "name", string(sticker.getName())
        );

        /*
         * Dispatch the message.
         */

        MinecraftDispatcher.dispatch(
            entry -> PlaceholdersExt.parseText(entry.minecraft.stickerNode, ctx, placeholders),
            entry -> entry.minecraft.sticker != null && entry.id == channelId
        );
        LOGGER.info(PlaceholdersExt.parseString("@${tag} sent sticker ${name}", ctx, placeholders));
    }

    /**
     * Handles an attachment of a given message received event.
     *
     * @param event message received event
     * @param attachment message attachment
     */
    public void onAttachment(MessageReceivedEvent event, Message.Attachment attachment)
    {
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
            // The link to the file to download
            "url", string(attachment.getUrl()),
            // The file name that was uploaded
            "name", string(attachment.getFileName()),
            // The file extension/type
            "ext", string(attachment.getFileExtension()),
            // The file size for humans
            "size", string(StringUtils.bytesToHuman(attachment.getSize()))
        );

        /*
         * Dispatch the message.
         */

        MinecraftDispatcher.dispatch(
            entry -> PlaceholdersExt.parseText(entry.minecraft.attachmentNode, ctx, placeholders),
            entry -> entry.minecraft.attachment != null && entry.id == channelId
        );
        LOGGER.info(PlaceholdersExt.parseString("@${tag} attached ${name} (${size})", ctx, placeholders));
    }
}
