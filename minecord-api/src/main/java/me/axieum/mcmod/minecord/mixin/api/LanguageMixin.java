package me.axieum.mcmod.minecord.mixin.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.util.Language;

import net.fabricmc.loader.api.FabricLoader;

import static me.axieum.mcmod.minecord.impl.MinecordImpl.LOGGER;
import static me.axieum.mcmod.minecord.impl.MinecordImpl.getConfig;

/**
 * Injects into, and loads all modded translation files.
 */
@Mixin(Language.class)
public abstract class LanguageMixin
{
    // The default language path already loaded by Minecraft - we'll skip loading this
    private static final String DEFAULT_LANGUAGE_PATH = "/assets/minecraft/lang/" + Language.DEFAULT_LANGUAGE + ".json";

    /**
     * Loads all modded translations for use when resolving translatable text.
     *
     * @param cir        mixin callback info
     * @param builder    language immutable mapping builder
     * @param biConsumer language file key/value consumer
     */
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(
        method = "create",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void create(
        CallbackInfoReturnable<Language> cir,
        ImmutableMap.Builder<String, String> builder,
        BiConsumer<String, String> biConsumer
    )
    {
        final String langCode = getConfig().i18n.lang;
        LOGGER.info("Begin loading '{}' modded translation files...", langCode);
        FabricLoader.getInstance().getAllMods().forEach(mod -> {
            final String modId = mod.getMetadata().getId();
            mod
                // Attempt to find the configured translation file
                .findPath("assets/" + modId + "/lang/" + langCode + ".json")
                // Otherwise, fallback to the default translation file
                .or(() -> mod.findPath("assets/" + modId + "/lang/" + Language.DEFAULT_LANGUAGE + ".json"))
                // Skip Minecraft translations since they are *already* loaded
                .filter(path -> !DEFAULT_LANGUAGE_PATH.equals(path.toString()))
                // If we found a translation file, load it
                .ifPresent(path -> {
                    LOGGER.info("Loading modded translation file: {}", path.toString());
                    try (InputStream is = Files.newInputStream(path)) {
                        Language.load(is, biConsumer);
                    } catch (IOException | JsonParseException e) {
                        LOGGER.error("Failed to load modded translation file '{}'", path.toString(), e);
                    }
                });
        });
    }
}
