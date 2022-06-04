package me.axieum.mcmod.minecord.api.util;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A string template builder, used to substitute variables within a string.
 *
 * <p>Typical usage is as follows:
 *
 * <blockquote><pre><code>
 * // Prepare the string template builder
 * final StringTemplate st = new StringTemplate();
 * st.add("name", "John Doe");
 *
 * // Apply, and hence format a string
 * String result = st.format("Hi, ${name}!");
 * </code></pre></blockquote>
 * The output is:
 * <blockquote>Hi, John Doe!</blockquote>
 *
 * <p>During replacements, a string may decide to specify how a variable should be
 * formatted when replaced (e.g. numbers or dates, etc.):
 *
 * <blockquote><pre><code>
 * // Prepare the string template builder
 * final StringTemplate st = new StringTemplate();
 * st.add("product", "keyboard")
 *   .add("price", 135.4781f)
 *   .add("date", LocalDateTime.now());
 *
 * // Apply, and hence format a string
 * String result = st.format("The ${product} costs ${price:$#.##}, and was released ${date:dd/MM/yyyy}.");
 * </code></pre></blockquote>
 * The output is:
 * <blockquote>The keyboard costs $135.48, and was released 05/08/2021.</blockquote>
 *
 * <p>If a variable is not present or evaluates to {@code null}, then a default
 * value (also known as a fallback) can be specified within the string itself:
 *
 * <blockquote><pre><code>
 * // Prepare the string template builder
 * final StringTemplate st = new StringTemplate();
 * st.add("var", null);
 *
 * // Apply, and hence format a string
 * String result = st.format("${var} -> ${var:-} -> ${var:-lorem}");
 * </code></pre></blockquote>
 * The output is:
 * <blockquote><pre>${var} ->  -> lorem</pre></blockquote>
 *
 * <p>To reduce the need for new {@link StringTemplate} instances, you can add a
 * {@link java.util.function.Supplier Supplier} that will be evaluated during string formatting:
 *
 * <blockquote><pre><code>
 * // Prepare the string template builder
 * final StringTemplate st = new StringTemplate();
 * final Random random = new Random(9);
 * st.add("roll", () -> random.nextInt(1, 6));
 *
 * // Apply, and hence format a string
 * String result_1 = st.format("You rolled a ${roll}.");
 * String result_2 = st.format("You rolled a ${roll}.");
 * </code></pre></blockquote>
 * The output is:
 * <blockquote>You rolled a 5.</blockquote>
 * <blockquote>You rolled a 2.</blockquote>
 *
 * @author Axieum
 */
public class StringTemplate
{
    public static final Logger LOGGER = LogManager.getLogger();

    private @NotNull String prefix = "${", suffix = "}";
    private final HashMap<String, @Nullable Object> variables = new HashMap<>();
    private final List<Function<String, String>> transforms = new ArrayList<>();

    public StringTemplate() {}

    /**
     * Sets the variable prefix that identifies the start of a variable.
     *
     * @param prefix the prefix for variables
     * @return {@code this} for chaining
     */
    public StringTemplate setPrefix(final @NotNull String prefix)
    {
        this.prefix = prefix;
        return this;
    }

    /**
     * Sets the variable suffix that identifies the end of a variable.
     *
     * @param suffix the suffix for variables
     * @return {@code this} for chaining
     */
    public StringTemplate setSuffix(final @NotNull String suffix)
    {
        this.suffix = suffix;
        return this;
    }

    /**
     * Adds a variable to be substituted by a name and given value.
     *
     * @param name  token name
     * @param value value to be substituted
     * @return {@code this} for chaining
     */
    public StringTemplate add(final @NotNull String name, final @Nullable Object value)
    {
        variables.put(name, value);
        return this;
    }

    /**
     * Adds a lazy variable to be substituted by a name and given value.
     *
     * @param name     token name
     * @param supplier supplier of the value to be substituted
     * @return {@code this} for chaining
     */
    public StringTemplate add(final @NotNull String name, final @Nullable Supplier<Object> supplier)
    {
        variables.put(name, supplier);
        return this;
    }

    /**
     * Returns a variable from its token name.
     *
     * @param name token name
     * @return token variable value
     */
    public @Nullable Object get(final @NotNull String name)
    {
        return variables.get(name);
    }

    /**
     * Removes a variable by its token name.
     *
     * @param name token name
     * @return {@code this} for chaining
     */
    public StringTemplate remove(final @NotNull String name)
    {
        variables.remove(name);
        return this;
    }

    /**
     * Adds a function to be applied to the resulting string *after* any
     * variable substitutions.
     *
     * @param transformer string transformer
     * @return {@code this} for chaining
     */
    public StringTemplate transform(final Function<String, String> transformer)
    {
        transforms.add(transformer);
        return this;
    }

    /**
     * Returns the underlying mapping of token names to variables.
     *
     * @return mapping of token names to variable values
     */
    public HashMap<String, Object> getVariables()
    {
        return variables;
    }

    /**
     * Returns the underlying list of transforms.
     *
     * @return list of transforms
     */
    public List<Function<String, String>> getTransforms()
    {
        return transforms;
    }

    /**
     * Formats a template string, substituting all added variables.
     *
     * @param template string to apply formatting to
     * @return formatted string
     */
    public String format(final @Nullable String template)
    {
        // Immediately bail on an empty template
        if (template == null || template.isBlank()) return template;

        // Prepare a resulting string builder and token regex matcher
        final StringBuilder builder = new StringBuilder();
        final Matcher matcher = Pattern.compile(
            Pattern.quote(prefix)
                + "(?<name>.+?)(?::(?!-)(?<format>.*?))?(?::-(?<default>.*?))?"
                + Pattern.quote(suffix)
        ).matcher(template);

        // For each token match, handle its replacement
        while (matcher.find()) {
            // Fetch the token capture groups
            final String name = matcher.group("name");
            final @Nullable String format = matcher.group("format");
            final @Nullable String fallback = matcher.group("default");

            // Attempt to match the token name to a known variable
            @Nullable Object value = variables.get(name);

            // If the matched variable is a supplier, resolve it
            if (value instanceof Supplier) {
                try {
                    value = ((Supplier<?>) value).get();
                } catch (Exception e) {
                    LOGGER.warn("Failed to evaluate lazy variable '{}': {}", name, e.getMessage());
                    value = null;
                }
            }

            // If the matched variable is non-null, replace the token with its value
            if (value != null) {
                try {
                    // If a format was specified, use it against its class type
                    if (format != null && !format.isBlank()) {
                        // Number
                        if (value instanceof Number) {
                            matcher.appendReplacement(builder, new DecimalFormat(format).format(value));
                        // Date & Time
                        } else if (value instanceof Temporal) {
                            matcher.appendReplacement(
                                builder, DateTimeFormatter.ofPattern(format).format((Temporal) value)
                            );
                        // Duration
                        } else if (value instanceof Duration) {
                            matcher.appendReplacement(
                                builder, DurationFormatUtils.formatDuration(((Duration) value).toMillis(), format)
                            );
                        // Other
                        } else {
                            throw new IllegalArgumentException("Format provided for unsupported variable type!");
                        }
                    // Otherwise, just use the string value of the object
                    } else {
                        // Duration
                        if (value instanceof Duration) {
                            matcher.appendReplacement(
                                builder,
                                DurationFormatUtils.formatDurationWords(((Duration) value).toMillis(), true, true)
                            );
                        // Other
                        } else {
                            matcher.appendReplacement(builder, String.valueOf(value));
                        }
                    }
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Could not replace variable '{}': {}", name, e.getMessage());
                }
            // Else, if the value is null or a fallback was provided, replace it with a default value
            } else if (variables.containsKey(name) || fallback != null) {
                matcher.appendReplacement(builder, fallback != null ? fallback : "");
            }
        }

        // Append any remaining contents and capture the resulting string
        String result = matcher.appendTail(builder).toString();

        // Apply any transforms to the result
        for (Function<String, String> transformer : transforms)
            result = transformer.apply(result);

        // Finally, return the resulting string
        return result;
    }
}
