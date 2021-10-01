package me.axieum.mcmod.minecord.impl.cmds;

import me.shedaniel.autoconfig.ConfigHolder;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.axieum.mcmod.minecord.api.addon.MinecordAddon;
import me.axieum.mcmod.minecord.impl.cmds.config.CommandConfig;

public final class MinecordCommands implements MinecordAddon
{
    public static final Logger LOGGER = LogManager.getLogger("Minecord|Commands");
    private static final ConfigHolder<CommandConfig> CONFIG = CommandConfig.init();

    @Override
    public void onInitializeMinecord(JDABuilder builder)
    {
        LOGGER.info("Minecord Commands is getting ready...");
    }

    /**
     * Returns the Minecord Commands config instance.
     *
     * @return config instance
     */
    public static CommandConfig getConfig()
    {
        return CONFIG.getConfig();
    }
}
