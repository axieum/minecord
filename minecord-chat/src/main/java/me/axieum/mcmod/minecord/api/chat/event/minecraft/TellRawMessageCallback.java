package me.axieum.mcmod.minecord.api.chat.event.minecraft;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A callback for when a server (or player) broadcasts a {@code /tellraw} command
 * message to *all* players.
 */
@FunctionalInterface
public interface TellRawMessageCallback
{
    /**
     * Called when a server (or player) broadcasts a {@code /tellraw} command
     * message to *all* players.
     */
    Event<TellRawMessageCallback> EVENT =
        EventFactory.createArrayBacked(TellRawMessageCallback.class, callbacks -> (message, source) -> {
            for (TellRawMessageCallback callback : callbacks) {
                callback.onTellRawCommandMessage(message, source);
            }
        });

    /**
     * Called when a server (or player) broadcasts a {@code /tellraw} command
     * message to *all* players.
     *
     * @param message broadcast message with message decorators applied if applicable
     * @param source  command source that sent the message
     */
    void onTellRawCommandMessage(Text message, ServerCommandSource source);
}
