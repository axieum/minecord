package me.axieum.mcmod.minecord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.DedicatedServerModInitializer;

public class Minecord implements DedicatedServerModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger("Minecord");

    @Override
    public void onInitializeServer()
    {
        LOGGER.info("Minecord is getting ready...");
    }
}
