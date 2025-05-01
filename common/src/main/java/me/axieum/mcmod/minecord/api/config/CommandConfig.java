package me.axieum.mcmod.minecord.api.config;

import java.util.Arrays;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.Comment;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject;
import eu.pb4.placeholders.api.node.TextNode;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import me.axieum.mcmod.minecord.api.cmds.CooldownScope;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.parseNode;

/**
 * Minecord Commands configuration schema.
 */
@Category(value = "commands")
@ConfigInfo(
    titleTranslation = "text.rconfig.minecord.cmds.title",
    descriptionTranslation = "text.rconfig.minecord.cmds.description"
)
public final class CommandConfig
{
    private CommandConfig() {}

    /** Feedback provided to the user who triggered the command. */
    @Category(value = "messages")
    @ConfigInfo(title = "Messages", description = "Feedback provided to the user who triggered the command.")
    public static class Messages
    {
        /** The error message used when the Minecraft server is unavailable. */
        @ConfigEntry(id = "unavailable")
        public static String unavailable = "The server is not yet ready - please wait :warning:";

        /** Pre-parsed 'unavailable' text node. */
        public static TextNode unavailableNode;

        /**
         * The error message used when any command unexpectedly fails.
         *
         * <ul>
         *   <li>{@code ${reason}} &mdash; the reason for the command failing</li>
         * </ul>
         */
        @ConfigEntry(id = "failed")
        public static String failed = "**Oh no** - something went wrong! :warning:\n_${reason}_";

        /** Pre-parsed 'failed' text node. */
        public static TextNode failedNode;

        /**
         * The error message used when a user must wait before executing a command.
         *
         * <ul>
         *   <li>{@code ${cooldown}} &mdash; the total cooldown before the command can be used again</li>
         *   <li>{@code ${remaining}} &mdash; the remaining time before the command can be used again</li>
         * </ul>
         */
        @ConfigEntry(id = "cooldown")
        public static String cooldown = "Please wait another ${remaining} before doing that! :alarm_clock:";

        /** Pre-parsed 'cooldown' text node. */
        public static TextNode cooldownNode;

        /** The default message used when a command does not provide any feedback of its own, e.g. {@code /say} */
        @ConfigEntry(id = "feedback")
        public static String feedback = "Consider it done! :thumbsup:";

        /** Pre-parsed 'feedback' text node. */
        public static TextNode feedbackNode;
    }

    /** Built-in Discord commands. */
    @Category(value = "builtin")
    @ConfigInfo(title = "Built-in Commands", description = "Built-in Discord commands.")
    public static class BuiltinCommands
    {
        /** Built-in uptime command. */
        @ConfigEntry(id = "uptime")
        @Comment("Displays for how long the Minecraft server has been online")
        public static UptimeCommand uptime = new UptimeCommand();

        /** Uptime built-in command configuration schema. */
        @ConfigObject
        public static final class UptimeCommand extends BaseCommand
        {
            /** Constructs a new built-in uptime command config. */
            public UptimeCommand()
            {
                name = "uptime";
                description = "Shows for how long the server has been online";
            }

            /**
             * A message template that is formatted and sent for the server's uptime.
             *
             * <ul>
             *   <li>{@code ${uptime [format]}} &mdash; the total process uptime (to the nearest minute)</li>
             * </ul>
             */
            @ConfigEntry(id = "message")
            public String message = "The server has been online for ${uptime} :hourglass_flowing_sand:";

            /** Pre-parsed 'message' text node. */
            public TextNode messageNode;
        }

        /** Displays the Minecraft server's current ticks-per-second. */
        @ConfigEntry(id = "tps")
        @Comment("Displays the Minecraft server's current ticks-per-second")
        public static TPSCommand tps = new TPSCommand();

        /**
         * Ticks-per-second (TPS) built-in command configuration schema.
         */
        @ConfigObject
        public static final class TPSCommand extends BaseCommand
        {
            /** Constructs a new built-in TPS command config. */
            public TPSCommand()
            {
                name = "tps";
                description = "Shows the server's current ticks-per-second";
            }
        }
    }

    /** Custom Discord commands. */
    @ConfigEntry(id = "custom")
    @Comment("Custom Discord commands.")
    public static CustomCommand[] custom = new CustomCommand[] {new CustomCommand()};

    /** A custom Discord command. */
    @ConfigObject
    public static final class CustomCommand extends BaseCommand
    {
        /** Constructs a new custom command config. */
        public CustomCommand()
        {
            name = "whitelist";
            description = "Manages the whitelist for the server";
            allowByDefault = false; // do not allow example commands by default
            enabled = false; // do not enable example commands by default
        }

        /**
         * A Minecraft command to execute.
         *
         * <ul>
         *   <li>{@code ${name}} &mdash; for "name" option value</li>
         * </ul>
         */
        @ConfigEntry(id = "command")
        public String command = "/whitelist ${args:-}";

        /** Pre-parsed 'command' text node. */
        public TextNode commandNode;

        /** A list of command options. */
        @ConfigEntry(id = "options")
        @Comment("A list of command options.")
        public Option[] options = new Option[] {new Option()};
    }

    /**
     * Base command configuration schema.
     */
    @ConfigObject
    public abstract static class BaseCommand
    {
        /** True if the command should be available for use. */
        @ConfigEntry(id = "enabled")
        public boolean enabled = true;

        /** Trigger name for the command. */
        @ConfigEntry(id = "name")
        public String name;

        /** A brief description of what the command does. */
        @ConfigEntry(id = "description")
        public String description;

        /** True if the command feedback is only visible to the executor. */
        @ConfigEntry(id = "ephemeral")
        public boolean ephemeral = false;

        /** True if anyone can use the command by default. */
        @ConfigEntry(id = "allowByDefault")
        public boolean allowByDefault = true;

        /** The number of seconds a user must wait before using the command again. */
        @ConfigEntry(id = "cooldown")
        public int cooldown = 0;

        /** To whom the cooldown applies. */
        @ConfigEntry(id = "cooldownScope")
        public CooldownScope cooldownScope = CooldownScope.USER;

        /** Command option configuration schema. */
        @ConfigObject
        public static final class Option
        {
            /** The type of option. */
            @ConfigEntry(id = "type")
            public OptionType type = OptionType.STRING;

            /** The option name. */
            @ConfigEntry(id = "name")
            public String name = "args";

            /** A brief description of what the option does. */
            @ConfigEntry(id = "description")
            public String description = "Any additional command arguments";

            /** True if the option is required. */
            @ConfigEntry(id = "required")
            public boolean required = false;

            /** If non-empty, restricts the value to one of the allowed choices. */
            @ConfigEntry(id = "choices")
            public Choice[] choices = new Choice[] {};

            /**
             * A command option's choice configuration schema.
             */
            @ConfigObject
            public static final class Choice
            {
                /** The choice name. */
                @ConfigEntry(id = "name")
                public String name;

                /** The allowed value, which type matches that of the option. */
                @ConfigEntry(id = "value")
                public Object value;
            }

            /**
             * Builds and returns the command option data.
             *
             * @return JDA command option data
             * @throws IllegalArgumentException if an invalid choices was provided
             */
            public OptionData getOptionData() throws IllegalArgumentException
            {
                final OptionData option = new OptionData(type, name, description, required);
                for (Choice choice : choices) {
                    switch (choice.value) {
                        case String s -> option.addChoice(choice.name, s);
                        case Integer i -> option.addChoice(choice.name, i);
                        case Double v -> option.addChoice(choice.name, v);
                        case Long l -> option.addChoice(choice.name, l);
                        case null, default -> throw new IllegalArgumentException(
                            "Could not derive type for command choice: " + choice.name
                        );
                    }
                }
                return option;
            }
        }
    }

    /**
     * Validates the configuration.
     */
    public static void validate()
    {
        // Parse message templates
        Messages.unavailableNode = parseNode(Messages.unavailable);
        Messages.failedNode = parseNode(Messages.failed);
        Messages.cooldownNode = parseNode(Messages.cooldown);
        Messages.feedbackNode = parseNode(Messages.feedback);
        BuiltinCommands.uptime.messageNode = parseNode(BuiltinCommands.uptime.message);

        // Validate custom commands
        Arrays.stream(custom).forEach(cmd -> {
            // Parse command templates
            cmd.commandNode = parseNode(cmd.command);
        });
    }
}
