package me.axieum.mcmod.minecord.mixin.api;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.crash.CrashReport;

@Mixin(CrashReport.class)
public interface CrashReportAccessor
{
    @Accessor(value = "file")
    File getFile();
}
