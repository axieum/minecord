package me.axieum.mcmod.minecord.mixin.api;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.crash.CrashReport;

/**
 * Minecraft crash report accessor mixin.
 */
@Mixin(CrashReport.class)
public interface CrashReportAccessor
{
    /**
     * Retrieves the private file of the crash report.
     *
     * @return crash report file
     */
    @Accessor(value = "file")
    File getFile();
}
