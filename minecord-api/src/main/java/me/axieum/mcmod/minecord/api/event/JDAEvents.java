package me.axieum.mcmod.minecord.api.event;

import net.dv8tion.jda.api.JDABuilder;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A collection of callbacks for the managed JDA client.
 */
public final class JDAEvents
{
    private JDAEvents() {}

    /**
     * Called before building the JDA client.
     */
    public static final Event<BuildClient> BUILD_CLIENT =
        EventFactory.createArrayBacked(BuildClient.class, callbacks -> builder -> {
            for (BuildClient callback : callbacks)
                callback.onBuildClient(builder);
        });

    @FunctionalInterface
    public interface BuildClient
    {
        /**
         * Called before building the JDA client.
         *
         * @param builder JDA client builder
         */
        void onBuildClient(JDABuilder builder);
    }
}
