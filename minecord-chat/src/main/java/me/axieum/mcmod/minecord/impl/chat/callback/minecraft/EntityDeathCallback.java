package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.awt.Color;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.ChatPlaceholderEvents;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.EntityDeathEvents;
import me.axieum.mcmod.minecord.api.chat.util.ChatStringUtils;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;

public class EntityDeathCallback implements EntityDeathEvents.Entity
{
    @Override
    public void onEntityDeath(LivingEntity entity, DamageSource source)
    {
        // Only listen for named animal/monsters (with a name tag)
        if (!entity.hasCustomName()) return;

        Minecord.getInstance().getJDA().ifPresent(jda -> {
            final String entityName = entity.getDisplayName().getString();

            /*
             * Prepare a message template.
             */

            final StringTemplate st = new StringTemplate();

            // The entity's display name
            st.add("name", entityName);
            // The reason for the entity's death
            st.add(
                "cause", source.getDeathMessage(entity).getString().replaceFirst(entityName, "").trim()
            );
            // The name of the world the entity died in
            st.add("world", ChatStringUtils.getWorldName(entity.world));
            // The X coordinate of where the entity died
            st.add("x", String.valueOf((int) entity.prevX));
            // The Y coordinate of where the entity died
            st.add("y", String.valueOf((int) entity.prevY));
            // The Z coordinate of where the entity died
            st.add("z", String.valueOf((int) entity.prevZ));

            ChatPlaceholderEvents.Minecraft.ENTITY_DEATH.invoker().onEntityDeathPlaceholder(st, entity, source);

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embed((embed, entry) ->
                    embed.setColor(Color.RED).setDescription(st.format(entry.discord.grief)),
                entry -> entry.discord.grief != null && entry.hasWorld(entity.world));
        });
    }
}
