package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.awt.Color;
import java.util.Map;
import java.util.regex.Pattern;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.chat.event.minecraft.EntityDeathEvents;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when a Minecraft entity dies.
 */
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
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(entity);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The entity's display name
                "name", string(entityName),
                // The reason for the entity's death
                "cause", string(source.getDeathMessage(entity).getString().replaceFirst(
                    Pattern.quote(entityName), "").trim()
                ),
                // The X coordinate of where the entity died
                "pos_x", string(String.valueOf((int) entity.lastX)),
                // The Y coordinate of where the entity died
                "pos_y", string(String.valueOf((int) entity.lastY)),
                // The Z coordinate of where the entity died
                "pos_z", string(String.valueOf((int) entity.lastZ))
            );

            /*
             * Dispatch the message.
             */

            DiscordDispatcher.embed(
                (embed, entry) -> embed.setColor(Color.RED).setDescription(
                    PlaceholdersExt.parseString(entry.discord.griefNode, ctx, placeholders)
                ),
                entry -> entry.discord.grief != null && entry.hasWorld(entity.getWorld())
            );
        });
    }
}
