package me.axieum.mcmod.minecord.api.presence.event;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import me.axieum.mcmod.minecord.api.util.StringTemplate;

public interface PlaceholderCallback
{
    /**
     * Called when providing placeholder values to Discord bot presence messages.
     */
    Event<PlaceholderCallback> EVENT =
        EventFactory.createArrayBacked(PlaceholderCallback.class, callbacks -> (st, stage, jda, server) -> {
            for (PlaceholderCallback callback : callbacks) {
                callback.onPlaceholderPresence(st, stage, jda, server);
            }
        });

    /**
     * Called when providing placeholder values to Discord bot presence messages.
     *
     * @param template mutable string template
     * @param stage    stage (or category) name
     * @param jda      JDA client
     * @param server   Minecraft server, if present
     */
    void onPlaceholderPresence(StringTemplate template, String stage, JDA jda, @Nullable MinecraftServer server);
}
