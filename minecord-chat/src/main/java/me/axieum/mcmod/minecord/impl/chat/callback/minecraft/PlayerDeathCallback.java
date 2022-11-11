package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.time.Duration;
import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.EntityDeathEvents;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.duration;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when a Minecraft player dies.
 */
public class PlayerDeathCallback implements EntityDeathEvents.Player
{
    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            final String playerName = player.getDisplayName().getString();

            /*
             * Prepare the message placeholders.
             */

            final @Nullable PlaceholderContext ctx = PlaceholderContext.of(player);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The player's username
                "username", string(player.getName().getString()),
                // The player's display name
                "player", string(playerName),
                // The reason for the player's death
                "cause", string(source.getDeathMessage(player).getString().replaceFirst(playerName, "").trim()),
                // The name of the world the player died in
                "world", string(StringUtils.getWorldName(player.world)),
                // The X coordinate of where the player died
                "x", string(String.valueOf((int) player.prevX)),
                // The Y coordinate of where the player died
                "y", string(String.valueOf((int) player.prevY)),
                // The Z coordinate of where the player died
                "z", string(String.valueOf((int) player.prevZ)),
                // The total time for which the player was alive for
                "lifespan", duration(Duration.ofMinutes(
                    player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH))
                )),
                // The player's total score before they died
                "score", string(String.valueOf(player.getScore())),
                // The player's number of experience levels before they died
                "exp", string(String.valueOf(player.experienceLevel))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embedWithAvatar(
                (embed, entry) -> embed.setDescription(
                    PlaceholdersExt.parseString(entry.discord.death, ctx, placeholders)
                ),
                entry -> entry.discord.death != null && entry.hasWorld(player.world),
                player.getUuidAsString()
            );
        });
    }
}
