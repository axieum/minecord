package me.axieum.mcmod.minecord.api.chat.util;

import java.util.HashMap;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static me.axieum.mcmod.minecord.impl.chat.MinecordChat.getConfig;

/**
 * Utility methods for chat related strings.
 */
public final class ChatStringUtils
{
    // Mapping of Minecraft world identifiers to their human-readable names
    public static final HashMap<Identifier, String> WORLD_NAMES = new HashMap<>(3);

    private ChatStringUtils() {}

    /**
     * Attempts to retrieve the world name from the config files first,
     * otherwise derives it from the registry key.
     *
     * @param world Minecraft world
     * @return name of the given world
     * @see #deriveWorldName(Identifier)
     */
    public static String getWorldName(final World world)
    {
        final Identifier identifier = world.getRegistryKey().getValue();
        return getConfig().worldNames.getOrDefault(identifier.toString(), deriveWorldName(identifier));
    }

    /**
     * Attempts to compute and cache the world name from its registry key.
     * NB: At present, the world name is not stored in any resources, apart
     * from in the registry key, e.g. 'the_nether'.
     *
     * @param identifier Minecraft world identifier
     * @return derived name of the given world identifier
     */
    public static String deriveWorldName(final Identifier identifier)
    {
        return WORLD_NAMES.computeIfAbsent(identifier, id -> {
            // Space delimited identifier path, with leading 'the' keywords removed
            final String path = id.getPath().replace('_', ' ').replaceFirst("(?i)the\\s", "");
            // Capitalise the first character in each word
            char[] chars = path.toCharArray();
            boolean capitalizeNext = true;
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == ' ') {
                    capitalizeNext = true;
                } else if (capitalizeNext) {
                    chars[i] = Character.toTitleCase(chars[i]);
                    capitalizeNext = false;
                }
            }
            // Return the computed world name
            return new String(chars);
        });
    }
}
