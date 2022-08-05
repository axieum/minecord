package me.axieum.mcmod.minecord.api.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.IMentionable;

import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import me.axieum.mcmod.minecord.api.Minecord;
import static me.axieum.mcmod.minecord.impl.MinecordImpl.getConfig;

/**
 * Utility methods for building and manipulating strings.
 */
public final class StringUtils
{
    // Mapping of Minecraft world identifiers to their human-readable names
    public static final HashMap<Identifier, String> WORLD_NAMES = new HashMap<>(3);
    // String templates for translating between Minecraft and Discord formatted strings
    public static StringTemplate discordMinecraftST, minecraftDiscordST;

    private StringUtils() {}

    /**
     * Converts bytes to a human-readable string, base 10.
     *
     * @param bytes number of bytes
     * @return human-readable bytes in base 10
     */
    public static String bytesToHuman(long bytes)
    {
        if (-1000 < bytes && bytes < 1000) return bytes + " B";
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    static {
        final Pattern bold = Pattern.compile("\\*\\*(.+?)\\*\\*");
        final Pattern underline = Pattern.compile("__(.+?)__");
        final Pattern italics = Pattern.compile("_(.+?)_");
        final Pattern italics2 = Pattern.compile("\\*(.+?)\\*");
        final Pattern strike = Pattern.compile("~~(.+?)~~");
        final Pattern spoilers = Pattern.compile("\\|\\|(.+?)\\|\\|");
        final Pattern code = Pattern.compile("(?s)```(\\w+?)\\n(.*?)```");
        final Pattern code2 = Pattern.compile("(?s)```(.*?)```");
        final Pattern code3 = Pattern.compile("(?s)`(.*?)`");
        discordMinecraftST = new StringTemplate()
            // Translate bold
            .transform(s -> bold.matcher(s).replaceAll("\u00A7l$1\u00A7r"))
            // Translate underline
            .transform(s -> underline.matcher(s).replaceAll("\u00A7n$1\u00A7r"))
            // Translate italics
            .transform(s -> italics.matcher(s).replaceAll("\u00A7o$1\u00A7r"))
            .transform(s -> italics2.matcher(s).replaceAll("\u00A7o$1\u00A7r"))
            // Translate strikethrough
            .transform(s -> strike.matcher(s).replaceAll("\u00A7m$1\u00A7r"))
            // Obfuscate spoilers
            .transform(s -> spoilers.matcher(s).replaceAll("\u00A7k$1\u00A7r"))
            // Darken code blocks
            .transform(s -> code.matcher(s).replaceAll("($1) \u00A77$2\u00A7r"))
            .transform(s -> code2.matcher(s).replaceAll("\u00A77$1\u00A7r"))
            .transform(s -> code3.matcher(s).replaceAll("\u00A77$1\u00A7r"))
            // Translate emojis from unicode
            .transform(EmojiParser::parseToAliases);
    }

    static {
        final Pattern breaks = Pattern.compile("(?s)\\n+");
        final Pattern bold = Pattern.compile("(?<=[\u00A7]l)(.+?)(?=\\s?[\u00A7]r|$)");
        final Pattern underline = Pattern.compile("(?<=[\u00A7]n)(.+?)(?=\\s?[\u00A7]r|$)");
        final Pattern italics = Pattern.compile("(?<=[\u00A7]o)(.+?)(?=\\s?[\u00A7]r|$)");
        final Pattern strike = Pattern.compile("(?<=[\u00A7]m)(.+?)(?=\\s?[\u00A7]r|$)");
        final Pattern spoilers = Pattern.compile("(?<=[\u00A7]k)(.+?)(?=\\s?[\u00A7]r|$)");
        final Pattern mention = Pattern.compile("@(\\w+?)#(\\d{4})");
        final Pattern mention2 = Pattern.compile("@((?!everyone|here)\\w+)(?!#\\d{4})\\b");
        final Pattern channel = Pattern.compile("#([^\\s]+)");
        final Function<MatchResult, String> resolveMention = m ->
            Minecord.getInstance().getJDA()
                .flatMap(jda -> Optional.ofNullable(jda.getUserByTag(m.group(1), m.group(2))))
                .map(IMentionable::getAsMention)
                .orElse(m.group(0));
        final Function<MatchResult, String> resolveMention2 = m ->
            Minecord.getInstance().getJDA()
                .flatMap(jda -> jda.getGuilds().stream()
                    .flatMap(g -> g.getMembersByEffectiveName(m.group(1), true).stream())
                    .findFirst())
                .map(IMentionable::getAsMention)
                .orElse(m.group(0));
        final Function<MatchResult, String> resolveChannel = m ->
            Minecord.getInstance().getJDA()
                .flatMap(jda -> jda.getTextChannelsByName(m.group(1), true).stream().findFirst())
                .map(IMentionable::getAsMention)
                .orElse(m.group(0));
        // Construct the string template
        minecraftDiscordST = new StringTemplate()
            // Collapse line breaks
            .transform(s -> breaks.matcher(s).replaceAll(" ")) // new line
            // Translate bold
            .transform(s -> bold.matcher(s).replaceAll("**$1**"))
            // Translate underline
            .transform(s -> underline.matcher(s).replaceAll("__$1__"))
            // Translate italics
            .transform(s -> italics.matcher(s).replaceAll("_$1_"))
            // Translate strikethrough
            .transform(s -> strike.matcher(s).replaceAll("~~$1~~"))
            // Obfuscate spoilers
            .transform(s -> spoilers.matcher(s).replaceAll("||$1||"))
            // Resolve @mention#discriminator
            .transform(s -> mention.matcher(s).replaceAll(resolveMention))
            // Resolve @mention
            .transform(s -> mention2.matcher(s).replaceAll(resolveMention2))
            // Resolve #channel
            .transform(s -> channel.matcher(s).replaceAll(resolveChannel))
            // Suppress @everyone and @here mentions
            .transform(s -> s.replace("@everyone", "@_everyone_"))
            .transform(s -> s.replace("@here", "@_here_"))
            // Strip any leftover formatting
            .transform(Formatting::strip);
    }

    /**
     * Translates a Discord flavoured markdown string to a
     * Minecraft-formatted string.
     *
     * @param contents Discord flavoured markdown string
     * @return Minecraft-formatted string
     */
    public static String discordToMinecraft(final String contents)
    {
        // Apply the appropriate string template against the given contents and return
        return discordMinecraftST.format(contents);
    }

    /**
     * Translates a Minecraft-formatted string to Discord flavoured markdown.
     *
     * @param contents Minecraft-formatted string
     * @return Discord flavoured markdown string
     */
    public static String minecraftToDiscord(final String contents)
    {
        // Apply the appropriate string template against the given contents and return
        return minecraftDiscordST.format(contents);
    }

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
        return getConfig().i18n.worlds.getOrDefault(identifier.toString(), deriveWorldName(identifier));
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

    /**
     * Attempts to retrieve the advancement type name from the config files
     * first, otherwise uses it symbol name.
     *
     * @param type Minecraft advancement frame/type
     * @return name of the advancement type
     */
    public static String getAdvancementTypeName(final AdvancementFrame type)
    {
        return getConfig().i18n.advancementTypes.getOrDefault(type.getId(), type.getId());
    }
}
