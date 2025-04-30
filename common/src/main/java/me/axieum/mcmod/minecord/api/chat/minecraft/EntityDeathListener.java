package me.axieum.mcmod.minecord.api.chat.minecraft;

import java.awt.Color;
import java.time.Duration;
import java.util.Map;
import java.util.regex.Pattern;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.DiscordDispatcher;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.duration;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when a living entity dies.
 */
public class EntityDeathListener implements EntityEvent.LivingDeath
{
    @Override
    public EventResult die(LivingEntity entity, DamageSource source)
    {
        if (entity instanceof ServerPlayer player) {
            playerDie(player, source);
        } else if (entity.hasCustomName()) {
            namedDie(entity, source);
        }
        return EventResult.pass();
    }

    /**
     * A listener for when a player dies.
     *
     * @param player The player who died.
     * @param source The damage source.
     */
    private void playerDie(ServerPlayer player, DamageSource source)
    {
        Minecord.getJDA().ifPresent(jda -> {
            final String playerName = player.getDisplayName().getString();
            final ResourceKey<Level> level = player.level().dimension();

            /*
             * Prepare the message placeholders.
             */

            final @Nullable PlaceholderContext ctx = PlaceholderContext.of(player);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The reason for the player's death
                "cause", string(source.getLocalizedDeathMessage(player).getString().replaceFirst(
                    Pattern.quote(playerName), "").trim()
                ),
                // The total time for which the player was alive for
                "lifespan", duration(Duration.ofSeconds(
                    player.getStats().getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH)) / 20
                )),
                // The player's total score before they died
                "score", string(String.valueOf(player.getScore())),
                // The player's number of experience levels before they died
                "exp", string(String.valueOf(player.experienceLevel)),
                // The name of the world the player died in
                "world", string(StringUtils.getLevelName(level))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embedWithAvatar(
                (embed, entry) -> embed.setColor(Color.RED).setDescription(
                    PlaceholdersExt.parseString(entry.discord.deathNode, ctx, placeholders)
                ),
                entry -> entry.discord.death != null && entry.hasWorld(level),
                player.getStringUUID()
            );
        });
    }

    /**
     * A listener for when a named entity dies.
     *
     * @param entity The entity that died.
     * @param source The damage source.
     */
    private void namedDie(LivingEntity entity, DamageSource source)
    {
        Minecord.getJDA().ifPresent(jda -> {
            final String entityName = entity.getDisplayName().getString();
            final ResourceKey<Level> level = entity.level().dimension();

            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(entity);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The entity's display name
                "name", string(entityName),
                // The reason for the entity's death
                "cause", string(source.getLocalizedDeathMessage(entity).getString().replaceFirst(
                    Pattern.quote(entityName), "").trim()
                ),
                // The X coordinate of where the entity died
                "pos_x", string(String.valueOf((int) entity.xOld)),
                // The Y coordinate of where the entity died
                "pos_y", string(String.valueOf((int) entity.yOld)),
                // The Z coordinate of where the entity died
                "pos_z", string(String.valueOf((int) entity.zOld))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embed(
                (embed, entry) -> embed.setColor(Color.RED).setDescription(
                    PlaceholdersExt.parseString(entry.discord.griefNode, ctx, placeholders)
                ),
                entry -> entry.discord.grief != null && entry.hasWorld(level)
            );
        });
    }
}
