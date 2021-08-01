package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.PlaceholderEvents;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

public class PlayerChangeWorldCallback implements ServerEntityWorldChangeEvents.AfterPlayerChange
{
    @Override
    public void afterChangeWorld(ServerPlayerEntity player, ServerWorld origin, ServerWorld destination)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare a message formatter.
             */

            final Map<String, Object> values = new HashMap<>();

            /*
             * Dispatch the message.
             */

            PlaceholderEvents.PLAYER_CHANGE_WORLD.invoker().onPlayerChangeWorld(values, player, origin, destination);
            final StrSubstitutor formatter = new StrSubstitutor(values);

            DiscordDispatcher.embed((embed, entry) ->
                    embed.setDescription(formatter.replace(entry.discord.teleport)),
                entry -> entry.discord.teleport != null);
        });
    }
}
