package me.axieum.mcmod.minecord.impl.cmds.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import me.axieum.mcmod.minecord.api.cmds.MinecordCommands;

@Config(name = "minecord/commands")
public class CommandConfig implements ConfigData
{
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
            Usages: {n} for the nth argument, and {} for all""")
        public String command = "/whitelist {}";
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
        holder.registerLoadListener((h, c) -> {
            MinecordCommands.getInstance().updateCommandList();
            return ActionResult.PASS;
        });

        return holder;
    }
}
