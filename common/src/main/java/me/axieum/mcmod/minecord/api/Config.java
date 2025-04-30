package me.axieum.mcmod.minecord.api;

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo.Link;

import me.axieum.mcmod.minecord.api.config.BotConfig;
import me.axieum.mcmod.minecord.api.config.ChatConfig;
import me.axieum.mcmod.minecord.api.config.CommandConfig;
import me.axieum.mcmod.minecord.api.config.I18nConfig;
import me.axieum.mcmod.minecord.api.config.MiscConfig;
import me.axieum.mcmod.minecord.api.config.PresenceConfig;

/**
 * The mod configuration.
 */
@com.teamresourceful.resourcefulconfig.api.annotations.Config(
    value = Minecord.MOD_ID,
    categories = {
        BotConfig.class,
        ChatConfig.class,
        CommandConfig.class,
        I18nConfig.class,
        MiscConfig.class,
        PresenceConfig.class,
    }
)
@ConfigInfo(
    titleTranslation = "text.rconfig.minecord.title",
    descriptionTranslation = "text.rconfig.minecord.description",
    icon = "message-square",
    links = {
        @Link(text = "CurseForge", icon = "curseforge",
            value = "https://curseforge.com/minecraft/mc-mods/minecord-for-discord"),
        @Link(text = "Modrinth", icon = "modrinth", value = "https://modrinth.com/mod/minecord"),
        @Link(text = "GitHub", icon = "github", value = "https://github.com/axieum/minecord"),
    }
)
public final class Config
{
    private Config() {}

    /**
     * Validates the configuration.
     */
    public static void validate() throws IllegalArgumentException
    {
        // Cascade validation
        ChatConfig.validate();
        CommandConfig.validate();
        MiscConfig.validate();
        PresenceConfig.validate();
    }
}
