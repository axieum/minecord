package me.axieum.mcmod.minecord.api.util;

import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.Placeholders.PlaceholderGetter;
import eu.pb4.placeholders.api.node.EmptyNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.parsers.MarkdownLiteParserV1;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.PatternPlaceholderParser;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.text.Text;

import me.axieum.mcmod.minecord.api.Minecord;

/**
 * Extension methods for interfacing with the {@link eu.pb4.placeholders.api.Placeholders Placeholder API}.
 */
public final class PlaceholdersExt
{
    /** The placeholder pattern used. */
    public static final Pattern PLACEHOLDER_PATTERN = Placeholders.PREDEFINED_PLACEHOLDER_PATTERN;
    /** The placeholder node parser. */
    public static final NodeParser NODE_PARSER = NodeParser.merge(
        TextParserV1.DEFAULT,
        PatternPlaceholderParser.of(
            PLACEHOLDER_PATTERN, PlaceholderContext.KEY, Placeholders.DEFAULT_PLACEHOLDER_GETTER
        )
    );

    private PlaceholdersExt() {}

    /*
     * Placeholder contexts.
     */

    /**
     * Returns the Minecraft server captured by Minecord placeholder context if it exists.
     *
     * @return Minecraft server placeholder context if present
     */
    public static @Nullable PlaceholderContext getMinecordServerContext()
    {
        return Minecord.getInstance().getMinecraft().map(PlaceholderContext::of).orElse(null);
    }

    /*
     * Text-output placeholder parsers.
     */

    public static Text parseText(@NotNull String str, @NotNull Map<String, PlaceholderHandler> placeholders)
    {
        return parseText(str, null, placeholders);
    }

    public static Text parseText(
        @NotNull String str, @Nullable PlaceholderContext context, @NotNull Map<String, PlaceholderHandler> placeholders
    )
    {
        return parseText(str, context, placeholders::get);
    }

    public static Text parseText(
        @NotNull String str, @Nullable PlaceholderContext context, @NotNull PlaceholderGetter placeholderGetter
    )
    {
        return parseText(parseNode(str), context, placeholderGetter);
    }

    public static Text parseText(@NotNull Text text, @NotNull Map<String, PlaceholderHandler> placeholders)
    {
        return parseText(text, null, placeholders);
    }

    public static Text parseText(
        @NotNull Text text, @Nullable PlaceholderContext context, @NotNull Map<String, PlaceholderHandler> placeholders
    )
    {
        return parseText(text, context, placeholders::get);
    }

    public static Text parseText(
        @NotNull Text text, @Nullable PlaceholderContext context, @NotNull PlaceholderGetter placeholderGetter
    )
    {
        return parseText(parseNode(text), context, placeholderGetter);
    }

    public static Text parseText(
        @NotNull TextNode node,
        @Nullable PlaceholderContext context,
        @NotNull Map<String, PlaceholderHandler> placeholders
    )
    {
        return parseText(node, context, placeholders::get);
    }

    public static Text parseText(
        @NotNull TextNode node, @Nullable PlaceholderContext context, @NotNull PlaceholderGetter placeholderGetter
    )
    {
        return context != null
            ? Placeholders.parseText(node, context, PLACEHOLDER_PATTERN, placeholderGetter)
            : Placeholders.parseNodes(node, PLACEHOLDER_PATTERN, placeholderGetter).toText(ParserContext.of(), true);
    }

    public static @NotNull TextNode parseNode(@Nullable Text text)
    {
        return text != null ? new ParentNode(NODE_PARSER.parseNodes(TextNode.convert(text))) : EmptyNode.INSTANCE;
    }

    /*
     * String-output placeholder parsers.
     */

    public static String parseString(@NotNull Text text, @NotNull Map<String, PlaceholderHandler> placeholders)
    {
        return parseString(text, null, placeholders);
    }

    public static String parseString(
        @NotNull Text text, @Nullable PlaceholderContext context, @NotNull Map<String, PlaceholderHandler> placeholders
    )
    {
        return parseString(text, context, placeholders::get);
    }

    public static String parseString(
        @NotNull Text text, @Nullable PlaceholderContext context, @NotNull PlaceholderGetter placeholderGetter
    )
    {
        return parseString(parseNode(text), context, placeholderGetter);
    }

    public static String parseString(@NotNull String str, @NotNull Map<String, PlaceholderHandler> placeholders)
    {
        return parseString(str, null, placeholders);
    }

    public static String parseString(
        @NotNull String str, @Nullable PlaceholderContext context, @NotNull Map<String, PlaceholderHandler> placeholders
    )
    {
        return parseString(str, context, placeholders::get);
    }

    public static String parseString(
        @NotNull String str, @Nullable PlaceholderContext context, @NotNull PlaceholderGetter placeholderGetter
    )
    {
        return parseString(parseNode(str), context, placeholderGetter);
    }

    public static String parseString(
        @NotNull TextNode node,
        @Nullable PlaceholderContext context,
        @NotNull Map<String, PlaceholderHandler> placeholders
    )
    {
        return parseString(node, context, placeholders::get);
    }

    public static String parseString(
        @NotNull TextNode node, @Nullable PlaceholderContext context, @NotNull PlaceholderGetter placeholderGetter
    )
    {
        return parseText(node, context, placeholderGetter).getString();
    }

    public static @NotNull TextNode parseNode(@Nullable String string)
    {
        return string != null && !string.isEmpty() ? NODE_PARSER.parseNode(string) : EmptyNode.INSTANCE;
    }

    /*
     * Placeholder handlers.
     */

    /**
     * Returns a {@link String} placeholder handler.
     *
     * @param string string
     * @return string placeholder handler
     */
    public static PlaceholderHandler string(final String string)
    {
        return (ctx, arg) -> PlaceholderResult.value(string);
    }

    /**
     * Returns a {@link Text} placeholder handler.
     *
     * @param text Minecraft text
     * @return text placeholder handler
     */
    public static PlaceholderHandler text(final Text text)
    {
        return (ctx, arg) -> PlaceholderResult.value(text);
    }

    /**
     * Returns a {@link Text} markdown placeholder handler.
     *
     * @param markdown markdown string
     * @return markdown placeholder handler
     */
    public static PlaceholderHandler markdown(final String markdown)
    {
        final Text markdownText = MarkdownLiteParserV1.ALL.parseText(markdown, ParserContext.of());
        return (ctx, arg) -> PlaceholderResult.value(markdownText);
    }

    /**
     * Returns a {@link Duration} placeholder handler.
     *
     * @param duration duration instance
     * @return duration placeholder handler
     */
    public static PlaceholderHandler duration(final Duration duration)
    {
        return (ctx, arg) -> PlaceholderResult.value(
            arg != null ? DurationFormatUtils.formatDuration(duration.toMillis(), arg)
                : DurationFormatUtils.formatDurationWords(duration.toMillis(), true, true)
        );
    }

    /**
     * Returns a lazy {@link Duration} placeholder handler.
     *
     * @param duration duration supplier
     * @return lazy duration placeholder handler
     */
    public static PlaceholderHandler duration(final Supplier<Duration> duration)
    {
        return (ctx, arg) -> duration(duration.get()).onPlaceholderRequest(ctx, arg);
    }
}
