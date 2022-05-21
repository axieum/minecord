package me.axieum.mcmod.minecord.impl.chat.callback.discord;

import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import me.axieum.mcmod.minecord.api.chat.event.ChatPlaceholderEvents;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.chat.util.MinecraftDispatcher;
import static me.axieum.mcmod.minecord.impl.chat.MinecordChat.LOGGER;
import static me.axieum.mcmod.minecord.impl.chat.MinecordChat.getConfig;

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
            final MessageReaction.ReactionEmote reaction = event.getReactionEmote();
            final String emote = reaction.isEmote() ? ":" + reaction.getName() + ":" : reaction.getName();

            /*
             * Prepare a message template.
             */

            final StringTemplate st = new StringTemplate();

            // The issuer's nickname or username
            st.add("issuer", event.getMember() != null ? event.getMember().getEffectiveName()
                                                           : event.getUser().getName());
            // The issuer's tag (i.e. username#discriminator), e.g. Axieum#1001
            st.add("issuer_tag", event.getUser().getAsTag());
            // The issuer's username, e.g. Axieum
            st.add("issuer_username", event.getUser().getName());
            // The issuer's username discriminator, e.g. 1001
            st.add("issuer_discriminator", event.getUser().getDiscriminator());
            // The author's nickname or username
            st.add("author", event.getMember() != null ? event.getMember().getEffectiveName()
                                                           : event.getUser().getName());
            // The author's tag (i.e. username#discriminator), e.g. Axieum#1001
            st.add("author_tag", event.getUser().getAsTag());
            // The author's username, e.g. Axieum
            st.add("author_username", event.getUser().getName());
            // The author's username discriminator, e.g. 1001
            st.add("author_discriminator", event.getUser().getDiscriminator());
            // The emote used to react
            st.add("emote", emote);

            ChatPlaceholderEvents.Discord.REACTION.invoker().onReactionPlaceholder(st, event);

            /*
             * Dispatch the message.
             */

            MinecraftDispatcher.json(entry -> st.format(isAdded ? entry.minecraft.react : entry.minecraft.unreact),
                entry -> (isAdded ? entry.minecraft.react : entry.minecraft.unreact) != null && entry.id == channelId);

            LOGGER.info(st.format(isAdded ? "@${issuer_tag} reacted with ${emote} to ${author_tag}'s message"
                : "@${issuer_tag} removed their reaction of ${emote} from ${author_tag}'s message"));
        });
    }
}
