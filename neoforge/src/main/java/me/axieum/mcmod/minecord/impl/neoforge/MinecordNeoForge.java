package me.axieum.mcmod.minecord.impl.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import me.axieum.mcmod.minecord.api.Minecord;

/**
 * The NeoForge platform NeoForge mod.
 */
@Mod(Minecord.MOD_ID)
public class MinecordNeoForge
{
    /**
     * Constructs a new NeoForge platform Minecord mod.
     *
     * @param eventBus the NeoForge event bus
     */
    public MinecordNeoForge(IEventBus eventBus)
    {
        // Cascade mod initialisation
        Minecord.init();
    }
}
