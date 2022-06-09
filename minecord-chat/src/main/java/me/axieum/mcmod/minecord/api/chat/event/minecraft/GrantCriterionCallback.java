package me.axieum.mcmod.minecord.api.chat.event.minecraft;

import net.minecraft.advancement.Advancement;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface GrantCriterionCallback
{
    /**
     * Called when a player is granted an advancement criterion.
     */
    Event<GrantCriterionCallback> EVENT =
        EventFactory.createArrayBacked(GrantCriterionCallback.class, callbacks -> (player, advancement, criterion) -> {
            for (GrantCriterionCallback callback : callbacks) {
                callback.onGrantCriterion(player, advancement, criterion);
            }
        });

    /**
     * Called when a player is granted an advancement criterion.
     *
     * @param player      redeeming player
     * @param advancement parent advancement
     * @param criterion   name of the criterion granted
     */
    void onGrantCriterion(ServerPlayerEntity player, Advancement advancement, String criterion);
}
