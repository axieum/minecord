package me.axieum.mcmod.minecord.impl.placeholder;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;

import net.minecraft.util.Identifier;

import me.axieum.mcmod.minecord.api.util.StringUtils;
import static me.axieum.mcmod.minecord.impl.MinecordImpl.getConfig;

/**
 * {@link eu.pb4.placeholders.api.Placeholders Placeholder API} global placeholders for Minecord API.
 */
public final class MinecordPlaceholders
{
    private MinecordPlaceholders() {}

    /**
     * Registers Minecord API global placeholders with {@link eu.pb4.placeholders.api.Placeholders Placeholder API}.
     */
    public static void register()
    {
        // minecord:player (or server)
        Placeholders.register(new Identifier("minecord", "player"), (ctx, arg) ->
            PlaceholderResult.value(
                ctx.player() != null
                    ? ctx.player().getDisplayName().getString()
                    : getConfig().i18n.serverName
            )
        );
        // minecord:world
        Placeholders.register(new Identifier("minecord", "world"), (ctx, arg) ->
            PlaceholderResult.value(ctx.world() != null ? StringUtils.getWorldName(ctx.world()) : "∞")
        );
    }
}
