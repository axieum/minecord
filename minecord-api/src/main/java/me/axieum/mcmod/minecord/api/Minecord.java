package me.axieum.mcmod.minecord.api;

import java.util.Optional;

import net.dv8tion.jda.api.JDA;

import net.minecraft.server.MinecraftServer;

import me.axieum.mcmod.minecord.impl.MinecordImpl;

/**
 * A gateway into the Minecord environment.
 */
public interface Minecord
{
    /**
     * Returns the Minecord instance.
     *
     * @return Minecord instance
     */
    static Minecord getInstance()
    {
        return MinecordImpl.INSTANCE;
    }

    /**
     * Returns the underlying Minecraft server.
     *
     * @return the Minecraft server instance if present
     */
    Optional<MinecraftServer> getMinecraft();

    /**
     * Returns the underlying JDA client.
     *
     * @return the JDA client if built
     */
    Optional<JDA> getJDA();
}
