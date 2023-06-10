package me.axieum.mcmod.minecord.impl.config;

import java.util.HashMap;
import java.util.Map;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

/**
 * Minecord translations configuration schema.
 */
@Config(name = "i18n")
public class I18nConfig implements ConfigData
{
    /** The name of the Minecraft server in system messages. */
    @Comment("The name of the Minecraft server in system messages")
    public String serverName = "Server";

    /** A mapping of Minecraft dimension IDs to their respective names. */
    @Comment("A mapping of Minecraft dimension IDs to their respective names")
    public Map<String, String> worlds = new HashMap<>(Map.of(
        "minecraft:overworld", "Overworld",
        "minecraft:the_nether", "The Nether",
        "minecraft:the_end", "The End"
    ));
}
