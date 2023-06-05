package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.util.HashMap;
import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents.ChatMessage;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents.CommandMessage;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.TellRawMessageCallback;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for a when a Minecraft player sends a message.
 */
public class ServerMessageCallback implements ChatMessage, CommandMessage, TellRawMessageCallback
{
    @Override
    public void onChatMessage(
        SignedMessage message, ServerPlayerEntity player, MessageType.Parameters params
    )
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(player);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The formatted message contents
                "message", string(StringUtils.minecraftToDiscord(message.getContent().getString()))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.dispatch(
                (embed, entry) -> embed.setContent(
                    PlaceholdersExt.parseString(entry.discord.chatNode, ctx, placeholders)
                ),
                entry -> entry.discord.chat != null && entry.hasWorld(player.getWorld())
            );
        });
    }

    @Override
    public void onCommandMessage(
        SignedMessage message, ServerCommandSource source, MessageType.Parameters params
    )
    {
        final String typeKey = params.type().chat().translationKey();
        if ("chat.type.emote".equals(typeKey)) {
            // '/me <action>'
            onEmoteCommandMessage(message, source, params);
        } else if ("chat.type.announcement".equals(typeKey)) {
            // '/say <message>'
            onSayCommandMessage(message, source, params);
        }
    }

    /**
     * Called when a player broadcasts a {@code /me} command message to all
     * players.
     *
     * @param message broadcast message with message decorators applied if
     *                applicable
     * @param source  command source that sent the message
     * @param params  message parameters
     */
    public void onEmoteCommandMessage(SignedMessage message, ServerCommandSource source, MessageType.Parameters params)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            final @Nullable ServerPlayerEntity player = source.getPlayer();

            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(source);
            final Map<String, PlaceholderHandler> placeholders = new HashMap<>(Map.of(
                // The formatted message contents
                "action", string(StringUtils.minecraftToDiscord(message.getContent().getString()))
            ));

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.dispatch(
                (embed, entry) -> embed.setContent(
                    PlaceholdersExt.parseString(entry.discord.emoteNode, ctx, placeholders)
                ),
                entry -> entry.discord.emote != null && (player == null || entry.hasWorld(source.getWorld()))
            );
        });
    }

    /**
     * Called when a server (or player) broadcasts a {@code /say} command
     * message to all players.
     *
     * @param message broadcast message with message decorators applied if
     *                applicable
     * @param source  command source that sent the message
     * @param params  message parameters
     */
    public void onSayCommandMessage(SignedMessage message, ServerCommandSource source, MessageType.Parameters params)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            final @Nullable ServerPlayerEntity player = source.getPlayer();

            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(source);
            final Map<String, PlaceholderHandler> placeholders = new HashMap<>(Map.of(
                // The formatted message contents
                "message", string(StringUtils.minecraftToDiscord(message.getContent().getString()))
            ));

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.dispatch(
                (embed, entry) -> embed.setContent(
                    PlaceholdersExt.parseString(entry.discord.sayNode, ctx, placeholders)
                ),
                entry -> entry.discord.say != null && (player == null || entry.hasWorld(source.getWorld()))
            );
        });
    }

    @Override
    public void onTellRawCommandMessage(Text message, ServerCommandSource source)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(source);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The formatted message contents
                "message", string(StringUtils.minecraftToDiscord(message.getString()))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.dispatch(
                (embed, entry) -> embed.setContent(
                    PlaceholdersExt.parseString(entry.discord.tellrawNode, ctx, placeholders)
                ),
                entry -> entry.discord.tellraw != null
            );
        });
    }
}
