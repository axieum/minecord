package me.axieum.mcmod.minecord.api.presence.event;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import me.axieum.mcmod.minecord.api.presence.category.PresenceCategory;
import me.axieum.mcmod.minecord.api.util.StringTemplate;

public interface PresencePlaceholderCallback
{
    /**
     * Called when providing placeholder values to Discord bot presence messages.
     */
    Event<PresencePlaceholderCallback> EVENT =
        EventFactory.createArrayBacked(PresencePlaceholderCallback.class, callbacks -> (st, stage, jda, server) -> {
            for (PresencePlaceholderCallback callback : callbacks) {
                callback.onPresencePlaceholder(st, stage, jda, server);
            }
        });

    /**
     * Called when providing placeholder values to Discord bot presence messages.
     *
     * @param template mutable string template
     * @param category Minecord presence category
     * @param jda      JDA client
     * @param server   Minecraft server, if present
     */
    void onPresencePlaceholder(
        StringTemplate template, PresenceCategory category, JDA jda, @Nullable MinecraftServer server
    );
}
