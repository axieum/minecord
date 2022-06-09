package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.ChatPlaceholderEvents;
import me.axieum.mcmod.minecord.api.chat.util.ChatStringUtils;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

public class PlayerChangeWorldCallback implements ServerEntityWorldChangeEvents.AfterPlayerChange
{
    @Override
    public void afterChangeWorld(ServerPlayerEntity player, ServerWorld origin, ServerWorld dest)
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
            // The name of the world the player entered
            st.add("world", ChatStringUtils.getWorldName(dest));
            // The X coordinate of where the player entered
            st.add("x", String.valueOf(player.getBlockX()));
            // The Y coordinate of where the player entered
            st.add("y", String.valueOf(player.getBlockY()));
            // The Z coordinate of where the player entered
            st.add("z", String.valueOf(player.getBlockZ()));
            // The name of the world the player left
            st.add("origin", ChatStringUtils.getWorldName(origin));
            // The X coordinate of where the player left
            st.add("origin_x", String.valueOf((int) player.prevX));
            // The Y coordinate of where the player left
            st.add("origin_y", String.valueOf((int) player.prevY));
            // The Z coordinate of where the player left
            st.add("origin_z", String.valueOf((int) player.prevZ));

            ChatPlaceholderEvents.Minecraft.PLAYER_CHANGE_WORLD.invoker().onPlayerChangeWorldPlaceholder(
                st, player, origin, dest
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embed((embed, entry) ->
                    embed.setDescription(st.format(entry.discord.teleport)),
                entry -> entry.discord.teleport != null && entry.hasWorld(dest));
        });
    }
}
