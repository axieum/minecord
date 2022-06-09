package me.axieum.mcmod.minecord.mixin.api;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;

import me.axieum.mcmod.minecord.api.event.ServerShutdownCallback;

/**
 * Injects into, and captures any server crash reports before broadcasting
 * that the server has exited.
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin
{
    // Captured Minecraft server crash report
    private static @Nullable CrashReport crashReport = null;

    /**
     * Broadcasts a server shutdown event, be it gracefully or forcefully exited.
     *
     * @param info mixin callback info
     */
    @Inject(method = "runServer", at = @At("TAIL"))
    private void runServer(CallbackInfo info)
    {
        ServerShutdownCallback.EVENT.invoker().onServerShutdown((MinecraftServer) (Object) this, crashReport);
    }

    /**
     * Captures any server crash reports.
     *
     * @param crashReport Minecraft crash report being set
     * @param info        mixin callback info
     */
    @Inject(method = "setCrashReport", at = @At("TAIL"))
    private void setCrashReport(CrashReport crashReport, CallbackInfo info)
    {
        MinecraftServerMixin.crashReport = crashReport;
    }
}
