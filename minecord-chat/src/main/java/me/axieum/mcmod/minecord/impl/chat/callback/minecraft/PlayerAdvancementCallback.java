package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.ChatPlaceholderEvents;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.GrantCriterionCallback;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

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
             * Prepare a message template.
             */

            final StringTemplate st = new StringTemplate();

            // The player's username
            st.add("username", player.getName().getString());
            // The player's display name
            st.add("player", player.getDisplayName().getString());
            // The type of advancement
            st.add("type", info.getFrame().getId());
            // The title of the advancement
            st.add("title", info.getTitle().getString());
            // A description of the advancement
            st.add("description", info.getDescription().getString());

            ChatPlaceholderEvents.Minecraft.PLAYER_ADVANCEMENT.invoker().onPlayerAdvancementPlaceholder(
                st, player, advancement, criterion
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embed((embed, entry) ->
                    embed.setDescription(st.format(entry.discord.advancement)),
                entry -> entry.discord.advancement != null && entry.hasWorld(player.world));
        });
    }
}
