package me.axieum.mcmod.minecord.api.config;

import java.util.HashMap;
import java.util.Map;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;

/**
 * Minecord translations configuration schema.
 */
@Category(value = "i18n")
@ConfigInfo(
    titleTranslation = "text.rconfig.minecord.i18n.title",
    descriptionTranslation = "text.rconfig.minecord.i18n.description"
)
public final class I18nConfig
{
    private I18nConfig() {}

    /** The name of the Minecraft server in system messages. */
    @ConfigEntry(id = "serverName")
    public static String serverName = "Server";

    /** A mapping of Minecraft dimension IDs to their respective names. */
    @ConfigEntry(id = "worlds")
    public static Map<String, String> worlds = new HashMap<>(Map.of(
        "minecraft:overworld", "Overworld",
        "minecraft:the_nether", "The Nether",
        "minecraft:the_end", "The End"
    ));
}
