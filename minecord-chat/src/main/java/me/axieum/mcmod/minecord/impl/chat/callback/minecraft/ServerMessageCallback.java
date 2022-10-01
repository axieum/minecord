package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents.ChatMessage;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents.CommandMessage;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.ChatPlaceholderEvents;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.TellRawMessageCallback;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

/**
 * A listener for a when a Minecraft player sends a message.
 */
public class ServerMessageCallback implements ChatMessage, CommandMessage, TellRawMessageCallback
{
    @Override
    public void onChatMessage(
        FilteredMessage<SignedMessage> message, ServerPlayerEntity player, RegistryKey<MessageType> typeKey
    )
    {
        if (!MessageType.CHAT.equals(typeKey)) return;
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare a message template.
             */

            final StringTemplate st = new StringTemplate();

            // The player's username
            st.add("username", player.getName().getString());
            // The player's display name
            st.add("player", player.getDisplayName().getString());
            // The name of the world the player logged into
            st.add("world", StringUtils.getWorldName(player.world));
            // The formatted message contents
            st.add("message", StringUtils.minecraftToDiscord(
                message.filteredOrElse(message.raw()).getContent().getString()
            ));

            ChatPlaceholderEvents.Minecraft.PLAYER_CHAT.invoker().onPlayerChatPlaceholder(st, player, message, typeKey);

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.dispatch((embed, entry) ->
                    embed.setContent(st.format(entry.discord.chat)),
                entry -> entry.discord.chat != null && entry.hasWorld(player.world));
        });
    }

    @Override
    public void onCommandMessage(
        FilteredMessage<SignedMessage> message, ServerCommandSource source, RegistryKey<MessageType> typeKey
    )
    {
        if (MessageType.EMOTE_COMMAND.equals(typeKey)) {
            // '/me <action>'
            onEmoteCommandMessage(message, source);
        } else if (MessageType.SAY_COMMAND.equals(typeKey)) {
            // '/say <message>'
            onSayCommandMessage(message, source);
        }
    }

    /**
     * Called when a player broadcasts a {@code /me} command message to all
     * players.
     *
     * @param message broadcast message with message decorators applied if
     *                applicable
     * @param source  command source that sent the message
     */
    public void onEmoteCommandMessage(FilteredMessage<SignedMessage> message, ServerCommandSource source)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            final @Nullable ServerPlayerEntity player = source.getPlayer();

            /*
             * Prepare a message template.
             */

            final StringTemplate st = new StringTemplate();

            // The player's username
            st.add("username", player != null ? player.getName().getString() : null);
            // The player's display name
            st.add("player", player != null ? player.getDisplayName().getString() : null);
            // The name of the world the player logged into
            st.add("world", player != null ? StringUtils.getWorldName(source.getWorld()) : null);
            // The formatted message contents
            st.add("action", StringUtils.minecraftToDiscord(
                message.filteredOrElse(message.raw()).getContent().getString()
            ));

            ChatPlaceholderEvents.Minecraft.EMOTE_COMMAND.invoker().onEmoteCommandPlaceholder(st, source, message);

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.dispatch((embed, entry) ->
                    embed.setContent(st.format(entry.discord.emote)),
                entry -> entry.discord.emote != null && (player == null || entry.hasWorld(source.getWorld())));
        });
    }

    /**
     * Called when a server (or player) broadcasts a {@code /say} command
     * message to all players.
     *
     * @param message broadcast message with message decorators applied if
     *                applicable
     * @param source  command source that sent the message
     */
    public void onSayCommandMessage(FilteredMessage<SignedMessage> message, ServerCommandSource source)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            final @Nullable ServerPlayerEntity player = source.getPlayer();

            /*
             * Prepare a message template.
             */

            final StringTemplate st = new StringTemplate();

            // The player's username
            st.add("username", player != null ? player.getName().getString() : null);
            // The player's display name
            st.add("player", player != null ? player.getDisplayName().getString() : null);
            // The name of the world the player logged into
            st.add("world", player != null ? StringUtils.getWorldName(source.getWorld()) : null);
            // The formatted message contents
            st.add("message", StringUtils.minecraftToDiscord(
                message.filteredOrElse(message.raw()).getContent().getString()
            ));

            ChatPlaceholderEvents.Minecraft.SAY_COMMAND.invoker().onSayCommandPlaceholder(st, source, message);

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.dispatch((embed, entry) ->
                    embed.setContent(st.format(entry.discord.say)),
                entry -> entry.discord.say != null && (player == null || entry.hasWorld(source.getWorld())));
        });
    }

    @Override
    public void onTellRawCommandMessage(Text message, ServerCommandSource source)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare a message template.
             */

            final StringTemplate st = new StringTemplate();

            // The formatted message contents
            st.add("message", StringUtils.minecraftToDiscord(message.getString()));

            ChatPlaceholderEvents.Minecraft.TELLRAW_COMMAND.invoker().onTellRawCommandPlaceholder(st, source, message);

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.dispatch((embed, entry) ->
                    embed.setContent(st.format(entry.discord.tellraw)),
                entry -> entry.discord.tellraw != null);
        });
    }
}
