package me.axieum.mcmod.minecord.impl.config;

import eu.pb4.placeholders.api.node.TextNode;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import static net.dv8tion.jda.api.EmbedBuilder.URL_PATTERN;

import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.parseNode;

/**
 * Minecord miscellaneous configuration schema.
 */
@Config(name = "misc")
public class MiscConfig implements ConfigData
{
    /** True if player avatars are included with embeds. */
    @Comment("True if player avatars are included with embeds")
    public boolean enableAvatars = true;

    /**
     * The URL used for retrieving Minecraft player avatars.
     *
     * <ul>
     *   <li>{@code ${uuid}} &mdash; the UUID of the Minecraft player</li>
     *   <li>{@code ${skin_id}} &mdash; the skin id (from Fabric Tailor mod) or UUID of the Minecraft player</li>
     *   <li>{@code ${size}} &mdash; the desired avatar height in pixels</li>
     * </ul>
     */
    @Comment("""
        The URL used for retrieving Minecraft player avatars
        Usages: ${uuid}, ${skin_id} and ${size} (height in pixels)""")
    public String avatarUrl = "https://api.tydiumcraft.net/v1/players/skin?uuid=${skin_id}&type=avatar&size=${size}";

    /** Pre-parsed 'avatarUrl' text node. */
    public transient TextNode avatarUrlNode;

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
        avatarUrlNode = parseNode(avatarUrl);
    }
}
