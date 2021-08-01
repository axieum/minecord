package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.PlaceholderEvents;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.EntityDeathEvents;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

public class PlayerDeathCallback implements EntityDeathEvents.Player
{
    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            /*
             * Prepare a message formatter.
             */

            final Map<String, Object> values = new HashMap<>();

            /*
             * Dispatch the message.
             */

            PlaceholderEvents.PLAYER_DEATH.invoker().onPlayerDeath(values, player, source);
            final StrSubstitutor formatter = new StrSubstitutor(values);

            DiscordDispatcher.embed((embed, entry) ->
                    embed.setDescription(formatter.replace(entry.discord.death)),
                entry -> entry.discord.death != null);
        });
    }
}
