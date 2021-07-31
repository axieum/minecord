package me.axieum.mcmod.minecord.api.chat;

import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ReceiveChatCallback
{
    /**
     * Called when a chat message is received from a player.
     */
    Event<ReceiveChatCallback> EVENT =
        EventFactory.createArrayBacked(ReceiveChatCallback.class, callbacks -> (player, message) -> {
            for (ReceiveChatCallback callback : callbacks) {
                callback.onReceiveChat(player, message);
            }
        });

    /**
     * Called when a chat message is received from a player.
     *
     * @param player  author of the message
     * @param message received message contents
     */
    void onReceiveChat(ServerPlayerEntity player, TextStream.Message message);
}
