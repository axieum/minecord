package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.PlaceholderEvents;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.ReceiveChatCallback;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

public class PlayerChatCallback implements ReceiveChatCallback
{
    @Override
    public void onReceiveChat(ServerPlayerEntity player, TextStream.Message message)
    {
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
            // todo: st.add("world", StringUtils.getWorldName(player.world));
            // The formatted message contents
            st.add("message", message.getRaw());

            PlaceholderEvents.Minecraft.PLAYER_CHAT.invoker().onPlayerChat(st, player, message);

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.dispatch((builder, entry) ->
                    builder.setContent(st.format(entry.discord.chat)),
                entry -> entry.discord.chat != null);
        });
    }
}
