package me.axieum.mcmod.minecord.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.CrashReport;
import net.minecraft.server.MinecraftServer;

import me.axieum.mcmod.minecord.api.chat.minecraft.ServerStoppedListener;

/**
 * Injects into and captures any server crash reports.
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin
{
    /**
     * Captures any server crash reports.
     *
     * @param crashReport Minecraft crash report being set
     * @param ci          mixin callback info
     */
    @Inject(method = "onServerCrash", at = @At("TAIL"))
    private void onServerCrash(CrashReport crashReport, CallbackInfo ci)
    {
        ServerStoppedListener.crashReport = crashReport;
    }
}
