package me.axieum.mcmod.minecord.api.presence;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

import me.axieum.mcmod.minecord.impl.presence.MinecordPresenceImpl;

/**
 * A gateway into the Minecord Presence addon.
 */
public interface MinecordPresence
{
    /**
     * Returns the Minecord Commands instance.
     *
     * @return Minecord Commands instance
     */
    static MinecordPresence getInstance()
    {
        return MinecordPresenceImpl.INSTANCE;
    }

    void start(@NotNull JDA jda);

    void stop();
}
