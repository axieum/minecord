package me.axieum.mcmod.minecord.impl.avatars;

import me.shedaniel.autoconfig.ConfigHolder;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.axieum.mcmod.minecord.api.addon.MinecordAddon;
import me.axieum.mcmod.minecord.api.avatars.MinecordAvatars;
import me.axieum.mcmod.minecord.impl.avatars.config.AvatarConfig;

public final class MinecordAvatarsImpl implements MinecordAvatars, MinecordAddon
{
    public static final MinecordAvatars INSTANCE = new MinecordAvatarsImpl();
    public static final Logger LOGGER = LogManager.getLogger("Minecord|Avatars");
    private static final ConfigHolder<AvatarConfig> CONFIG = AvatarConfig.init();

    @Override
    public void onInitializeMinecord(JDABuilder builder)
    {
        LOGGER.info("Minecord Avatars is getting ready...");
    }

    /**
     * Returns the Minecord Avatars config instance.
     *
     * @return config instance
     */
    public static AvatarConfig getConfig()
    {
        return CONFIG.getConfig();
    }
}
