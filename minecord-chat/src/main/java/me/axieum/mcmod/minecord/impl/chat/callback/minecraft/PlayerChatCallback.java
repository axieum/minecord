package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.PlaceholderEvents;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.ReceiveChatCallback;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

public class PlayerChatCallback implements ReceiveChatCallback
{
    @Override
    public void onReceiveChat(ServerPlayerEntity player, TextStream.Message message)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare a message formatter.
             */

            final Map<String, Object> values = new HashMap<>();

            /*
             * Dispatch the message.
             */

            PlaceholderEvents.PLAYER_CHAT.invoker().onPlayerChat(values, player, message);
            final StrSubstitutor formatter = new StrSubstitutor(values);

            DiscordDispatcher.embed((embed, entry) ->
                    embed.setDescription(formatter.replace(entry.discord.chat)),
                entry -> entry.discord.chat != null);
        });
    }
}
