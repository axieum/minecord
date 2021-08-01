package me.axieum.mcmod.minecord.api.chat.event.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A collection of callbacks for living entity deaths.
 */
public final class EntityDeathEvents
{
    private EntityDeathEvents() {}

    /**
     * Called when a player had died.
     */
    public static final Event<EntityDeathEvents.Player> PLAYER =
        EventFactory.createArrayBacked(EntityDeathEvents.Player.class, callbacks -> (player, source) -> {
            for (EntityDeathEvents.Player callback : callbacks) {
                callback.onPlayerDeath(player, source);
            }
        });

    /**
     * Called when an animal or monster had died.
     */
    public static final Event<EntityDeathEvents.Entity> ANIMAL_MONSTER =
        EventFactory.createArrayBacked(EntityDeathEvents.Entity.class, callbacks -> (entity, source) -> {
            for (EntityDeathEvents.Entity callback : callbacks) {
                callback.onEntityDeath(entity, source);
            }
        });

    @FunctionalInterface
    public interface Player
    {
        /**
         * Called when a player had died.
         *
         * @param player victim player
         * @param source damage source
         */
        void onPlayerDeath(ServerPlayerEntity player, DamageSource source);
    }

    @FunctionalInterface
    public interface Entity
    {
        /**
         * Called when an animal or monster had died.
         *
         * @param entity victim animal/monster
         * @param source damage source
         */
        void onEntityDeath(LivingEntity entity, DamageSource source);
    }
}
