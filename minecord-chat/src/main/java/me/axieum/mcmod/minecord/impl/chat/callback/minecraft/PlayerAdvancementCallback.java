package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import net.minecraft.advancement.Advancement;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.PlaceholderEvents;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.GrantCriterionCallback;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

public class PlayerAdvancementCallback implements GrantCriterionCallback
{
    @Override
    public void onGrantCriterion(ServerPlayerEntity player, Advancement advancement, String criterion)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare a message formatter.
             */

            final Map<String, Object> values = new HashMap<>();

            /*
             * Dispatch the message.
             */

            PlaceholderEvents.PLAYER_ADVANCEMENT.invoker().onPlayerAdvancement(values, player, advancement, criterion);
            final StrSubstitutor formatter = new StrSubstitutor(values);

            DiscordDispatcher.embed((embed, entry) ->
                    embed.setDescription(formatter.replace(entry.discord.advancement)),
                entry -> entry.discord.advancement != null);
        });
    }
}
