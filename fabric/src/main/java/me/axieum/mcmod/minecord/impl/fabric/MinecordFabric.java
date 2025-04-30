package me.axieum.mcmod.minecord.impl.fabric;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import me.axieum.mcmod.minecord.api.Minecord;

/**
 * The Fabric platform Minecord mod.
 */
public class MinecordFabric implements PreLaunchEntrypoint
{
    @Override
    public void onPreLaunch()
    {
        // Cascade mod initialisation
        Minecord.init();
    }
}
