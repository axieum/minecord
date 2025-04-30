package me.axieum.mcmod.minecord.api.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import eu.pb4.placeholders.api.node.TextNode;
import static net.dv8tion.jda.api.EmbedBuilder.URL_PATTERN;

import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.parseNode;

/**
 * Minecord miscellaneous configuration schema.
 */
@Category(value = "misc")
@ConfigInfo(
    titleTranslation = "text.rconfig.minecord.misc.title",
    descriptionTranslation = "text.rconfig.minecord.misc.description"
)
public final class MiscConfig
{
    private MiscConfig() {}

    /** True if player avatars are included with embeds. */
    @ConfigEntry(id = "enableAvatars")
    public static boolean enableAvatars = true;

    /**
     * The URL used for retrieving Minecraft player avatars.
     *
     * <ul>
     *   <li>{@code ${uuid}} &mdash; the UUID of the Minecraft player</li>
     *   <li>{@code ${skin_id}} &mdash; the skin id (from Fabric Tailor mod) or UUID of the Minecraft player</li>
     *   <li>{@code ${size}} &mdash; the desired avatar height in pixels</li>
     * </ul>
     */
    @ConfigEntry(id = "avatarUrl")
    public static String avatarUrl = "https://starlightskins.lunareclipse.studio/render/pixel"
        + "/${uuid}/face?cameraWidth=${size}";

    /** Pre-parsed 'avatarUrl' text node. */
    public static TextNode avatarUrlNode;

    /**
     * Validates the configuration.
     */
    public static void validate() throws IllegalArgumentException
    {
        // Validate the avatar URL is valid
        if (avatarUrl == null || avatarUrl.isBlank()) {
            throw new IllegalArgumentException("The avatar URL cannot be blank!");
        } else if (avatarUrl.length() > 2000) {
            throw new IllegalArgumentException("The avatar URL cannot be longer than 2000 characters!");
        } else if (!URL_PATTERN.matcher(avatarUrl).matches()) {
            throw new IllegalArgumentException("The avatar URL must be a valid http(s) or attachment url!");
        }
        avatarUrlNode = parseNode(avatarUrl);
    }
}
