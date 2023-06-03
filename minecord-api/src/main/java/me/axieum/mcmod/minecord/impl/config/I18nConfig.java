package me.axieum.mcmod.minecord.impl.config;

import java.util.HashMap;
import java.util.Map;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.RequiresRestart;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import net.minecraft.util.Language;

/**
 * Minecord translations configuration schema.
 */
@Config(name = "i18n")
public class I18nConfig implements ConfigData
{
    /** The language code used to load translations from. */
    @Comment("The language code used to load translations from")
    @RequiresRestart
    public String lang = Language.DEFAULT_LANGUAGE;

    /** A mapping of Minecraft dimension IDs to their respective names. */
    @Comment("A mapping of Minecraft dimension IDs to their respective names")
    public Map<String, String> worlds = new HashMap<>(Map.ofEntries(
        Map.entry("minecraft:overworld", "Overworld"),
        Map.entry("minecraft:the_nether", "Nether"),
        Map.entry("minecraft:the_end", "The End")
    ));
}
