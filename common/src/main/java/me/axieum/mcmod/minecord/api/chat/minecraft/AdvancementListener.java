package me.axieum.mcmod.minecord.api.chat.minecraft;

import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import dev.architectury.event.events.common.PlayerEvent;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.DiscordDispatcher;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when a player unlocked an advancement.
 */
public class AdvancementListener implements PlayerEvent.PlayerAdvancement
{
    @Override
    public void award(ServerPlayer player, AdvancementHolder advancement)
    {
        Minecord.getJDA().ifPresent(jda -> {
            // Only listen for advancements that should be announced
            final DisplayInfo info = advancement.value().display().orElse(null);
            if (info == null || !info.shouldAnnounceChat()) return;

            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(player);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The title of the advancement
                "title", string(info.getTitle().getString()),
                // A description of the advancement
                "description", string(info.getDescription().getString())
            );

            /*
             * Dispatch the message.
             */

            final ResourceKey<Level> level = player.level().dimension();
            switch (info.getType()) {
                // A player reached an advancement goal
                case GOAL -> DiscordDispatcher.embed(
                    (embed, entry) -> embed.setDescription(
                        PlaceholdersExt.parseString(entry.discord.advancementGoalNode, ctx, placeholders)
                    ),
                    entry -> entry.discord.advancementGoal != null && entry.hasWorld(level)
                );

                // A player completed an advancement challenge
                case CHALLENGE -> DiscordDispatcher.embed(
                    (embed, entry) -> embed.setDescription(
                        PlaceholdersExt.parseString(entry.discord.advancementChallengeNode, ctx, placeholders)
                    ),
                    entry -> entry.discord.advancementChallenge != null && entry.hasWorld(level)
                );

                // A player unlocked an advancement task
                default -> DiscordDispatcher.embed(
                    (embed, entry) -> embed.setDescription(
                        PlaceholdersExt.parseString(entry.discord.advancementTaskNode, ctx, placeholders)
                    ),
                    entry -> entry.discord.advancementTask != null && entry.hasWorld(level)
                );
            }
        });
    }
}
