package me.axieum.mcmod.minecord.impl.chat.callback.discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;

import me.axieum.mcmod.minecord.api.chat.event.ChatPlaceholderEvents;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import me.axieum.mcmod.minecord.impl.chat.util.MinecraftDispatcher;
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

        // Link any attachments
        for (Message.Attachment attachment : event.getMessage().getAttachments())
            onAttachment(event, attachment);
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
         * Prepare a message template.
         */

        final StringTemplate st = new StringTemplate();

        // The author's tag (i.e. username#discriminator), e.g. Axieum#1001
        st.add("tag", event.getAuthor().getAsTag());
        // The author's username, e.g. Axieum
        st.add("username", event.getAuthor().getName());
        // The author's username discriminator, e.g. 1001
        st.add("discriminator", event.getAuthor().getDiscriminator());
        // The author's nickname or username
        st.add("author", event.getMember() != null ? event.getMember().getEffectiveName()
                                                       : event.getAuthor().getName());
        // The formatted message contents
        st.add("message", StringUtils.discordToMinecraft(event.getMessage().getContentDisplay()));
        // The raw message contents
        st.add("raw", event.getMessage().getContentRaw());

        // The message is in reply to another
        if (replyMessage != null) {
            // The replied message author's tag (i.e. username#discriminator), e.g. Axieum#1001
            st.add("reply_tag", replyMessage.getAuthor().getAsTag());
            // The replied message author's username, e.g. Axieum
            st.add("reply_username", replyMessage.getAuthor().getName());
            // The replied message author's username discriminator, e.g. 1001
            st.add("reply_discriminator", replyMessage.getAuthor().getDiscriminator());
            // The replied message author's nickname or username
            st.add("reply_author", replyMessage.getMember() != null ? replyMessage.getMember().getEffectiveName()
                : replyMessage.getAuthor().getName());
            // The replied message formatted message contents
            st.add("reply_message", StringUtils.discordToMinecraft(replyMessage.getContentDisplay()));
            // The replied message raw message contents
            st.add("reply_raw", replyMessage.getContentRaw());
        }

        /*
         * Dispatch the message.
         */

        // The message is a standalone message
        if (replyMessage == null) {
            ChatPlaceholderEvents.Discord.MESSAGE_RECEIVED.invoker().onMessageReceivedPlaceholder(st, event);
            MinecraftDispatcher.json(entry -> st.format(entry.minecraft.chat),
                entry -> entry.minecraft.chat != null && entry.id == channelId);
            LOGGER.info(st.format("@${tag} > ${raw}"));

        // The message is in reply to another
        } else {
            ChatPlaceholderEvents.Discord.REPLY_RECEIVED.invoker().onReplyReceivedPlaceholder(st, event);
            MinecraftDispatcher.json(entry -> st.format(entry.minecraft.reply),
                entry -> entry.minecraft.reply != null && entry.id == channelId);
            LOGGER.info(st.format("@${tag} (in reply to @${reply_tag}) > ${raw}"));
        }
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
         * Prepare a message template.
         */

        final StringTemplate st = new StringTemplate();

        // The author's tag (i.e. username#discriminator), e.g. Axieum#1001
        st.add("tag", event.getAuthor().getAsTag());
        // The author's username, e.g. Axieum
        st.add("username", event.getAuthor().getName());
        // The author's username discriminator, e.g. 1001
        st.add("discriminator", event.getAuthor().getDiscriminator());
        // The author's nickname or username
        st.add("author", event.getMember() != null ? event.getMember().getEffectiveName()
                                                       : event.getAuthor().getName());
        // The link to the file to download
        st.add("url", attachment.getUrl());
        // The file name that was uploaded
        st.add("name", attachment.getFileName());
        // The file extension/type
        st.add("ext", attachment.getFileExtension());
        // The file size for humans
        st.add("size", StringUtils.bytesToHuman(attachment.getSize()));

        ChatPlaceholderEvents.Discord.ATTACHMENT_RECEIVED.invoker().onAttachmentReceivedPlaceholder(
            st, event, attachment
        );

        /*
         * Dispatch the message.
         */

        MinecraftDispatcher.json(entry -> st.format(entry.minecraft.attachment),
            entry -> entry.minecraft.attachment != null && entry.id == channelId);

        LOGGER.info(st.format("@${tag} attached ${name} (${size})"));
    }
}
