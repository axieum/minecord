package me.axieum.mcmod.minecord.impl.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import static net.dv8tion.jda.api.EmbedBuilder.URL_PATTERN;

@Config(name = "misc")
public class MiscConfig implements ConfigData
{
    @Comment("True if player avatars are included with embeds")
    public boolean enableAvatars = true;

    @Comment("""
        The URL used for retrieving Minecraft player avatars
        Usages: ${username} and ${size} (height in pixels)""")
    public String avatarUrl = "https://minotar.net/helm/${username}/${size}";

    @Override
    public void validatePostLoad() throws ValidationException
    {
        // Validate the avatar URL is valid
        if (avatarUrl == null || avatarUrl.isBlank()) {
            throw new ValidationException("The avatar URL cannot be blank!");
        } else if (avatarUrl.length() > 2000) {
            throw new ValidationException("The avatar URL cannot be longer than 2000 characters!");
        } else if (!URL_PATTERN.matcher(avatarUrl).matches()) {
            throw new ValidationException("The avatar URL must be a valid http(s) or attachment url!");
        }
    }
}
