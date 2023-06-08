package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.GrantCriterionCallback;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when a Minecraft player is granted an advancement.
 */
public class PlayerAdvancementCallback implements GrantCriterionCallback
{
    @Override
    public void onGrantCriterion(ServerPlayerEntity player, Advancement advancement, String criterion)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            // Only listen for advancements that should be announced
            final AdvancementDisplay info = advancement.getDisplay();
            if (info == null || !info.shouldAnnounceToChat()) return;

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

            switch (info.getFrame()) {
                // A player reached an advancement goal
                case GOAL -> DiscordDispatcher.embed(
                    (embed, entry) -> embed.setDescription(
                        PlaceholdersExt.parseString(entry.discord.advancementGoalNode, ctx, placeholders)
                    ),
                    entry -> entry.discord.advancementGoal != null && entry.hasWorld(player.getWorld())
                );

                // A player completed an advancement challenge
                case CHALLENGE -> DiscordDispatcher.embed(
                    (embed, entry) -> embed.setDescription(
                        PlaceholdersExt.parseString(entry.discord.advancementChallengeNode, ctx, placeholders)
                    ),
                    entry -> entry.discord.advancementChallenge != null && entry.hasWorld(player.getWorld())
                );

                // A player unlocked an advancement task
                default -> DiscordDispatcher.embed(
                    (embed, entry) -> embed.setDescription(
                        PlaceholdersExt.parseString(entry.discord.advancementTaskNode, ctx, placeholders)
                    ),
                    entry -> entry.discord.advancementTask != null && entry.hasWorld(player.getWorld())
                );
            }
        });
    }
}
