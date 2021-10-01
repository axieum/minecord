package me.axieum.mcmod.minecord.api.addon;

import net.dv8tion.jda.api.JDABuilder;

/**
 * A Minecord addon interface agreement.
 */
public interface MinecordAddon
{
    /**
     * Initialises a new Minecord addon.
     *
     * <p>NB: This is called before building the JDA client! To use the built
     * client once connected, you can attach an event listener and observe the
     * {@link net.dv8tion.jda.api.events.ReadyEvent} for the client.
     *
     * @param builder JDA client builder
     */
    void onInitializeMinecord(JDABuilder builder);
}
