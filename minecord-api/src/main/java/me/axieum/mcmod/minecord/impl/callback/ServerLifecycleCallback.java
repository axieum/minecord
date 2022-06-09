package me.axieum.mcmod.minecord.impl.callback;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.event.ServerShutdownCallback;
import static me.axieum.mcmod.minecord.impl.MinecordImpl.LOGGER;
import static me.axieum.mcmod.minecord.impl.MinecordImpl.getConfig;

public class ServerLifecycleCallback implements ServerStarting, ServerStarted, ServerStopping, ServerShutdownCallback
{
    @Override
    public void onServerStarting(MinecraftServer server)
    {
        // Update the Discord bot status
        Minecord.getInstance().getJDA().ifPresent(jda -> jda.getPresence().setStatus(getConfig().status.starting));
    }

    @Override
    public void onServerStarted(MinecraftServer server)
    {
        // Update the Discord bot status
        Minecord.getInstance().getJDA().ifPresent(jda -> jda.getPresence().setStatus(getConfig().status.started));
    }

    @Override
    public void onServerStopping(MinecraftServer server)
    {
        // Update the Discord bot status
        Minecord.getInstance().getJDA().ifPresent(jda -> jda.getPresence().setStatus(getConfig().status.stopping));
    }

    @Override
    public void onServerShutdown(MinecraftServer server, @Nullable CrashReport crashReport)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            // Update the Discord bot status
            jda.getPresence().setStatus(getConfig().status.stopped);
            // Shutdown the JDA client
            LOGGER.info("Minecord is wrapping up...");
            jda.shutdown();
        });
    }
}
