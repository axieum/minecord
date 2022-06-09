package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.time.Duration;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.ChatPlaceholderEvents;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.EntityDeathEvents;
import me.axieum.mcmod.minecord.api.chat.util.ChatStringUtils;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

public class PlayerDeathCallback implements EntityDeathEvents.Player
{
    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            final String playerName = player.getDisplayName().getString();

            /*
             * Prepare a message template.
             */

            final StringTemplate st = new StringTemplate();

            // The player's username
            st.add("username", player.getName().getString());
            // The player's display name
            st.add("player", playerName);
            // The reason for the player's death
            st.add(
                "cause", source.getDeathMessage(player).getString().replaceFirst(playerName, "").trim()
            );
            // The name of the world the player died in
            st.add("world", ChatStringUtils.getWorldName(player.world));
            // The X coordinate of where the player died
            st.add("x", String.valueOf((int) player.prevX));
            // The Y coordinate of where the player died
            st.add("y", String.valueOf((int) player.prevY));
            // The Z coordinate of where the player died
            st.add("z", String.valueOf((int) player.prevZ));
            // The total time for which the player was alive for
            st.add("lifespan", Duration.ofMinutes(
                player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH))
            ));
            // The player's total score before they died
            st.add("score", String.valueOf(player.getScore()));
            // The player's number of experience levels before they died
            st.add("exp", String.valueOf(player.experienceLevel));

            ChatPlaceholderEvents.Minecraft.PLAYER_DEATH.invoker().onPlayerDeathPlaceholder(st, player, source);

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embed((embed, entry) ->
                    embed.setDescription(st.format(entry.discord.death)),
                entry -> entry.discord.death != null && entry.hasWorld(player.world));
        });
    }
}
