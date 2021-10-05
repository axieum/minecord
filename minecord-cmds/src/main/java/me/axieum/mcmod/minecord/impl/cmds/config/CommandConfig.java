package me.axieum.mcmod.minecord.impl.cmds.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import me.axieum.mcmod.minecord.api.cmds.MinecordCommands;
import me.axieum.mcmod.minecord.impl.cmds.MinecordCommandsImpl;

@Config(name = "minecord/commands")
public class CommandConfig implements ConfigData
{
    @Category("Messages")
    @Comment("Feedback provided to the user who triggered the command")
    public Messages messages = new Messages();

    public static class Messages
    {
        @Comment("The error message used when the Minecraft server is unavailable")
        public String unavailable = "The server is not yet ready - please wait :warning:";

        @Comment("""
            The error message used when a user is denied permission to a command
            Usages: ${role}""")
        public String denied = "You don't have permission to do that! :no_good:";

        @Comment("""
            The error message used when a user must wait before executing a command
            Usages: ${cooldown}""")
        public String cooldown = "Please wait another ${cooldown} before doing that! :alarm_clock:";
    }

    @Category("Built-in Commands")
    @Comment("Built-in Discord commands")
    public BuiltinCommands builtin = new BuiltinCommands();

    public static class BuiltinCommands
    {
        @Category("Uptime Command")
        @Comment("Displays for how long the Minecraft server has been online")
        public UptimeCommand uptime = new UptimeCommand();

        public static class UptimeCommand extends BaseCommand
        {
            public UptimeCommand()
            {
                name = "uptime";
                description = "Shows for how long the server has been online";
            }

            @Comment("""
                A message template that is formatted and sent for the server's uptime
                "Usages: ${uptime}""")
            public String message = "The server has been online for ${uptime} :hourglass_flowing_sand:";
        }

        @Category("TPS Command")
        @Comment("Displays the Minecraft server's current ticks per second")
        public TPSCommand tps = new TPSCommand();

        public static class TPSCommand extends BaseCommand
        {
            public TPSCommand()
            {
                name = "tps";
                description = "Shows the server's current ticks per second";
            }
        }
    }

    @Category("Custom Commands")
    @Comment("Custom Discord commands")
    public CustomCommand[] custom = new CustomCommand[] {new CustomCommand()};

    public static class CustomCommand extends BaseCommand
    {
        public CustomCommand()
        {
            name = "whitelist";
            description = "Manages the whitelist for the server";
            allowByDefault = false; // do not allow example commands by default
            enabled = false; // do not enable example commands by default
        }

        @Comment("True if the execution should not provide any feedback")
        public boolean quiet = false;

        @Comment("""
            A Minecraft command to execute
            Usages: ${<name>} for "<name>" option value""")
        public String command = "/whitelist ${args}";

        @Category("Options")
        @Comment("A list of command options")
        public Option[] options = new Option[] {new Option()};
    }

    /**
     * Base command configuration schema.
     */
    public abstract static class BaseCommand
    {
        @Comment("True if the command should be available for use")
        public boolean enabled = true;

        @Comment("Trigger name for the command")
        public String name;

        @Comment("A brief description of what the command does")
        public String description;

        @Comment("True if anyone can use the command by default")
        public boolean allowByDefault = true;

        @Category("Permissions")
        @Comment("A list of permissions that restrict access to the command")
        public Permission[] permissions = new Permission[] {new Permission()};

        /**
         * Command permission/privilege configuration schema.
         */
        public static class Permission
        {
            @Comment("""
                The type of entity this permission relates to
                Allowed values: ROLE and USER""")
            public CommandPrivilege.Type type = CommandPrivilege.Type.ROLE;

            @Comment("The identifier within Discord this permission relates to")
            public String id = "252653832427929601";

            @Comment("True if permission to use the command is granted")
            public boolean allow = true;
        }

        /**
         * Command option configuration schema.
         */
        public static class Option
        {
            @Comment("""
                The type of option
                Allowed values: BOOLEAN, CHANNEL, INTEGER, MENTIONABLE, NUMBER, ROLE, STRING and USER""")
            public OptionType type = OptionType.STRING;

            @Comment("The option name")
            public String name = "args";

            @Comment("A brief description of what the option does")
            public String description = "Any additional command arguments";

            @Comment("True if the option is required")
            public boolean required = false;

            @Category("Choices")
            @Comment("If non-empty, restricts the value to one of the allowed choices")
            public Choice[] choices = new Choice[] {};

            /**
             * A command option's choice configuration schema.
             */
            public static class Choice
            {
                @Comment("The choice name")
                public String name;

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
                for (Choice choice : choices) {
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

    /**
     * Registers and prepares a new configuration instance.
     *
     * @return registered config holder
     * @see AutoConfig#register(Class, ConfigSerializer.Factory)
     */
    public static ConfigHolder<CommandConfig> init()
    {
        // Register the config
        ConfigHolder<CommandConfig> holder = AutoConfig.register(CommandConfig.class, JanksonConfigSerializer::new);

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) ->
            AutoConfig.getConfigHolder(CommandConfig.class).load());

        // Listen for when the config gets loaded
        holder.registerLoadListener((hld, cfg) -> {
            // Re-register all Minecord provided commands
            MinecordCommandsImpl.initCommands(cfg);
            // Update the command list in Discord
            MinecordCommands.getInstance().updateCommandList();
            return ActionResult.PASS;
        });

        return holder;
    }
}
