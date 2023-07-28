package me.axieum.mcmod.minecord.impl.cmds.config;

import java.util.Arrays;

import eu.pb4.placeholders.api.node.TextNode;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.cmds.MinecordCommands;
import me.axieum.mcmod.minecord.api.cmds.command.CooldownScope;
import me.axieum.mcmod.minecord.impl.cmds.MinecordCommandsImpl;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.parseNode;

/**
 * Minecord Commands configuration schema.
 */
@Config(name = "minecord/commands")
public class CommandConfig implements ConfigData
{
    /** Feedback provided to the user who triggered the command. */
    @Category("Messages")
    @Comment("Feedback provided to the user who triggered the command")
    public MessagesSchema messages = new MessagesSchema();

    /**
     * Messages configuration schema.
     */
    public static class MessagesSchema
    {
        /** The error message used when the Minecraft server is unavailable. */
        @Comment("The error message used when the Minecraft server is unavailable")
        public String unavailable = "The server is not yet ready - please wait :warning:";

        /** Pre-parsed 'unavailable' text node. */
        public transient TextNode unavailableNode;

        /**
         * The error message used when any command unexpectedly fails.
         *
         * <ul>
         *   <li>{@code ${reason}} &mdash; the reason for the command failing</li>
         * </ul>
         */
        @Comment("""
            The error message used when any command unexpectedly fails
            Usages: ${reason}""")
        public String failed = "**Oh no** - something went wrong! :warning:\n_${reason}_";

        /** Pre-parsed 'failed' text node. */
        public transient TextNode failedNode;

        /**
         * The error message used when a user must wait before executing a command.
         *
         * <ul>
         *   <li>{@code ${cooldown}} &mdash; the total cooldown before the command can be used again</li>
         *   <li>{@code ${remaining}} &mdash; the remaining time before the command can be used again</li>
         * </ul>
         */
        @Comment("""
            The error message used when a user must wait before executing a command
            Usages: ${cooldown} and ${remaining}""")
        public String cooldown = "Please wait another ${remaining} before doing that! :alarm_clock:";

        /** Pre-parsed 'cooldown' text node. */
        public transient TextNode cooldownNode;

        /** The default message used when a command does not provide any feedback of its own, e.g. {@code /say} */
        @Comment("""
            The default message used when a command does not provide any feedback of its own, e.g. '/say'""")
        public String feedback = "Consider it done! :thumbsup:";

        /** Pre-parsed 'feedback' text node. */
        public transient TextNode feedbackNode;
    }

    /** Built-in Discord commands. */
    @Category("Built-in Commands")
    @Comment("Built-in Discord commands")
    public BuiltinCommandsSchema builtin = new BuiltinCommandsSchema();

    /**
     * Built-in commands configuration schema.
     */
    public static class BuiltinCommandsSchema
    {
        /** Built-in uptime command. */
        @Category("Uptime Command")
        @Comment("Displays for how long the Minecraft server has been online")
        public UptimeCommandSchema uptime = new UptimeCommandSchema();

        /**
         * Uptime built-in command configuration schema.
         */
        public static class UptimeCommandSchema extends BaseCommandSchema
        {
            /** Constructs a new built-in uptime command config. */
            public UptimeCommandSchema()
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
            @Comment("""
                A message template that is formatted and sent for the server's uptime
                Usages: ${uptime [format]}""")
            public String message = "The server has been online for ${uptime} :hourglass_flowing_sand:";

            /** Pre-parsed 'message' text node. */
            public transient TextNode messageNode;
        }

        /** Built-in TPS command. */
        @Category("TPS Command")
        @Comment("Displays the Minecraft server's current ticks-per-second")
        public TPSCommandSchema tps = new TPSCommandSchema();

        /**
         * Ticks-per-second (TPS) built-in command configuration schema.
         */
        public static class TPSCommandSchema extends BaseCommandSchema
        {
            /** Constructs a new built-in TPS command config. */
            public TPSCommandSchema()
            {
                name = "tps";
                description = "Shows the server's current ticks-per-second";
            }
        }
    }

    /** Custom Discord commands. */
    @Category("Custom Commands")
    @Comment("Custom Discord commands")
    public CustomCommandSchema[] custom = new CustomCommandSchema[] {new CustomCommandSchema()};

    /**
     * Custom command configuration schema.
     */
    public static class CustomCommandSchema extends BaseCommandSchema
    {
        /** Constructs a new custom command config. */
        public CustomCommandSchema()
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
        @Comment("""
            A Minecraft command to execute
            Usages: ${<name>} for "<name>" option value""")
        public String command = "/whitelist ${args:-}";

        /** Pre-parsed 'command' text node. */
        public transient TextNode commandNode;

        /** A list of command options. */
        @Category("Options")
        @Comment("A list of command options")
        public OptionSchema[] options = new OptionSchema[] {new OptionSchema()};
    }

    /**
     * Base command configuration schema.
     */
    public abstract static class BaseCommandSchema
    {
        /** True if the command should be available for use. */
        @Comment("True if the command should be available for use")
        public boolean enabled = true;

        /** Trigger name for the command. */
        @Comment("Trigger name for the command")
        public String name;

        /** A brief description of what the command does. */
        @Comment("A brief description of what the command does")
        public String description;

        /** True if the command feedback is only visible to the executor. */
        @Comment("True if the command feedback is only visible to the executor")
        public boolean ephemeral = false;

        /** True if anyone can use the command by default. */
        @Comment("True if anyone can use the command by default")
        public boolean allowByDefault = true;

        /** The number of seconds a user must wait before using the command again. */
        @Comment("The number of seconds a user must wait before using the command again")
        public int cooldown = 0;

        /** To whom the cooldown applies. */
        @Comment("""
            To whom the cooldown applies
            Allowed values: USER, CHANNEL, USER_CHANNEL, GUILD, USER_GUILD, SHARD, USER_SHARD and GLOBAL""")
        public CooldownScope cooldownScope = CooldownScope.USER;

        /**
         * Command option configuration schema.
         */
        public static class OptionSchema
        {
            /** The type of option. */
            @Comment("""
                The type of option
                Allowed values: ATTACHMENT, BOOLEAN, CHANNEL, INTEGER, MENTIONABLE, NUMBER, ROLE, STRING and USER""")
            public OptionType type = OptionType.STRING;

            /** The option name. */
            @Comment("The option name")
            public String name = "args";

            /** A brief description of what the option does. */
            @Comment("A brief description of what the option does")
            public String description = "Any additional command arguments";

            /** True if the option is required. */
            @Comment("True if the option is required")
            public boolean required = false;

            /** If non-empty, restricts the value to one of the allowed choices. */
            @Category("Choices")
            @Comment("If non-empty, restricts the value to one of the allowed choices")
            public ChoiceSchema[] choices = new ChoiceSchema[] {};

            /**
             * A command option's choice configuration schema.
             */
            public static class ChoiceSchema
            {
                /** The choice name. */
                @Comment("The choice name")
                public String name;

                /** The allowed value, which type matches that of the option. */
                @Comment("The allowed value, which type matches that of the option")
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
                for (ChoiceSchema choice : choices) {
                    if (choice.value instanceof String) {
                        option.addChoice(choice.name, (String) choice.value);
                    } else if (choice.value instanceof Integer) {
                        option.addChoice(choice.name, (int) choice.value);
                    } else if (choice.value instanceof Double) {
                        option.addChoice(choice.name, (double) choice.value);
                    } else if (choice.value instanceof Long) {
                        option.addChoice(choice.name, (long) choice.value);
                    } else {
                        throw new IllegalArgumentException("Could not derive type for command choice: " + choice.name);
                    }
                }
                return option;
            }
        }
    }

    @Override
    public void validatePostLoad()
    {
        // Parse message templates
        messages.unavailableNode = parseNode(messages.unavailable);
        messages.failedNode = parseNode(messages.failed);
        messages.cooldownNode = parseNode(messages.cooldown);
        messages.feedbackNode = parseNode(messages.feedback);
        builtin.uptime.messageNode = parseNode(builtin.uptime.message);

        // Parse command templates
        Arrays.stream(custom).forEach(cmd -> cmd.commandNode = parseNode(cmd.command));

        // Register all Minecord provided commands
        MinecordCommandsImpl.initCommands(this);

        // Update the command list in Discord
        Minecord.getInstance().getJDA().ifPresent(jda -> MinecordCommands.getInstance().updateCommandList());
    }

    /**
     * Registers and loads a new configuration instance.
     *
     * @see AutoConfig#register(Class, ConfigSerializer.Factory)
     */
    public static void load()
    {
        // Register (and load) the config
        AutoConfig.register(CommandConfig.class, JanksonConfigSerializer::new);

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) ->
            AutoConfig.getConfigHolder(CommandConfig.class).load());
    }
}
