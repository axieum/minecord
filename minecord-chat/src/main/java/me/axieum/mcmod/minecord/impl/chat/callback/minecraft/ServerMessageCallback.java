package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents.ChatMessage;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.ChatPlaceholderEvents;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

public class ServerMessageCallback implements ChatMessage
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
}
