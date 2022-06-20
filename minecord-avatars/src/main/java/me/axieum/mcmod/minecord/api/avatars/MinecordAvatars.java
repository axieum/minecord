package me.axieum.mcmod.minecord.api.avatars;

import me.axieum.mcmod.minecord.impl.avatars.MinecordAvatarsImpl;

/**
 * A gateway into the Minecord Avatars addon.
 */
public interface MinecordAvatars
{
    /**
     * Returns the Minecord Avatars instance.
     *
     * @return Minecord Avatars instance
     */
    static MinecordAvatars getInstance()
    {
        return MinecordAvatarsImpl.INSTANCE;
    }
}
