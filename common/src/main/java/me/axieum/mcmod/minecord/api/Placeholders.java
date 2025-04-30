package me.axieum.mcmod.minecord.api;

import eu.pb4.placeholders.api.PlaceholderResult;

import net.minecraft.resources.ResourceLocation;

import me.axieum.mcmod.minecord.api.config.I18nConfig;
import me.axieum.mcmod.minecord.api.util.StringUtils;

/**
 * {@link eu.pb4.placeholders.api.Placeholders Placeholder API} global placeholders.
 */
public final class Placeholders
{
    private Placeholders() {}

    /**
     * Registers global placeholders with {@link eu.pb4.placeholders.api.Placeholders Placeholder API}.
     */
    public static void register()
    {
        // {minecord:player}
        eu.pb4.placeholders.api.Placeholders.register(
            ResourceLocation.fromNamespaceAndPath("minecord", "player"),
            (ctx, arg) -> PlaceholderResult.value(
                ctx.player() != null ? ctx.player().getDisplayName().getString() : I18nConfig.serverName
            )
        );

        // {minecord:world}
        eu.pb4.placeholders.api.Placeholders.register(
            ResourceLocation.fromNamespaceAndPath("minecord", "world"),
            (ctx, arg) -> PlaceholderResult.value(
                ctx.hasWorld() ? StringUtils.getLevelName(ctx.world().dimension()) : "∞"
            )
        );
    }
}
