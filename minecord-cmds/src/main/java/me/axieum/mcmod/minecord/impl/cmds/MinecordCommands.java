package me.axieum.mcmod.minecord.impl.cmds;

import me.shedaniel.autoconfig.ConfigHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import me.axieum.mcmod.minecord.impl.cmds.config.CommandConfig;

public final class MinecordCommands implements PreLaunchEntrypoint
{
    public static final Logger LOGGER = LogManager.getLogger("Minecord|Commands");
    private static final ConfigHolder<CommandConfig> CONFIG = CommandConfig.init();

    @Override
    public void onPreLaunch()
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
