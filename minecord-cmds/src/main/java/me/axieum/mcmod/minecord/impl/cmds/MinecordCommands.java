package me.axieum.mcmod.minecord.impl.cmds;

import me.shedaniel.autoconfig.ConfigHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.DedicatedServerModInitializer;

import me.axieum.mcmod.minecord.impl.commands.config.CommandConfig;

public final class MinecordCommands implements DedicatedServerModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger("Minecord|Commands");
    private static final ConfigHolder<CommandConfig> CONFIG = CommandConfig.init();

    @Override
    public void onInitializeServer()
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
