package me.axieum.mcmod.minecord.api;

import java.util.Optional;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Builds and returns a URL for retrieving a Minecraft player's avatar.
     *
     * @param username the Minecraft player username
     * @param height   the desired height of the avatar in pixels
     * @return the URL for the Minecraft player's avatar if enabled
     * @see me.axieum.mcmod.minecord.impl.config.MiscConfig#enableAvatars
     * @see me.axieum.mcmod.minecord.impl.config.MiscConfig#avatarUrl
     */
    Optional<String> getAvatarUrl(@Nullable String username, int height);
}
