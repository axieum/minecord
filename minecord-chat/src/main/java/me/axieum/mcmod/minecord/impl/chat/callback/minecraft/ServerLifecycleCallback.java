package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping;

import me.axieum.mcmod.minecord.api.event.ServerShutdownCallback;

public class ServerLifecycleCallback implements ServerStarting, ServerStarted, ServerStopping, ServerShutdownCallback
{
    @Override
    public void onServerStarting(MinecraftServer server)
    {
        //
    }

    @Override
    public void onServerStarted(MinecraftServer server)
    {
        //
    }

    @Override
    public void onServerStopping(MinecraftServer server)
    {
        //
    }

    @Override
    public void onServerShutdown(MinecraftServer server, @Nullable CrashReport crashReport)
    {
        //
    }
}
