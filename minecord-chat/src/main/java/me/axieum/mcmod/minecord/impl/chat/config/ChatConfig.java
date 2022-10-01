package me.axieum.mcmod.minecord.impl.chat.config;

import java.util.Arrays;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Minecord Chat configuration schema.
 */
@Config(name = "minecord/chat")
public class ChatConfig implements ConfigData
{
    /** Chat configuration entries. */
    @Comment("Chat Configurations")
    public ChatEntrySchema[] entries = {new ChatEntrySchema()};

    /**
     * Chat entry configuration schema.
     */
    public static class ChatEntrySchema
    {
        /** A channel identifier in Discord to observe. */
        @Comment("A channel identifier in Discord to observe")
        public long id;

        /**
         * If non-empty, reduces the scope of all events to the listed Minecraft dimension IDs,
         * e.g. {@code minecraft:the_nether}.
         */
        @Comment("If non-empty, reduces the scope of all events to the listed Minecraft dimension IDs, "
            + "e.g. 'minecraft:the_nether'")
        public String[] dimensions = {};

        /** Minecraft events relayed to Discord. */
        @Category("Discord")
        @Comment("Minecraft events relayed to Discord")
        public DiscordSchema discord = new DiscordSchema();

        /**
         * Discord events configuration schema.
         */
        @SuppressWarnings("checkstyle:linelength")
        public static class DiscordSchema
        {
            /**
             * A player sent an in-game chat message.
             *
             * <p>Usages: {@code ${username}}, {@code ${player}}, {@code ${message}} and {@code ${world}}.
             */
            @Comment("""
                A player sent an in-game chat message
                Usages: ${username}, ${player}, ${message} and ${world}""")
            public String chat = "`${world}` **${player}** > ${message}";

            /**
             * A player sent an in-game message via the {@code /me} command.
             *
             * <p>Note: there is no player or world if sent from a command block or console!
             *
             * <p>Usages: {@code ${username}}, {@code ${player}}, {@code ${action}} and {@code ${world}}.
             */
            @Comment("""
                A player sent an in-game message via the '/me' command
                Note: there is no player or world if sent from a command block or console!
                Usages: ${username}, ${player}, ${action} and ${world}""")
            public String emote = "`${world:-∞}` **${player:-Server}** _${action}_";

            /**
             * An admin broadcast an in-game message via the {@code /say} command.
             *
             * <p>Note: there is no player or world if sent from a command block or console!
             *
             * <p>Usages: {@code ${username}}, {@code ${player}}, {@code ${message}} and {@code ${world}}.
             */
            @Comment("""
                An admin broadcast an in-game message via the '/say' command
                Note: there is no player or world if sent from a command block or console!
                Usages: ${username}, ${player}, ${message} and ${world}""")
            public String say = "**[${player:-Server}]** ${message}";

            /**
             * An admin broadcast an in-game message to all players via the {@code /tellraw @a} command.
             *
             * <p>Usages: {@code ${message}}.
             */
            @Comment("""
                An admin broadcast an in-game message to all players via the '/tellraw @a' command
                Usages: ${message}""")
            public String tellraw = "${message}";

            /**
             * A player had died.
             *
             * <p>Usages: {@code ${username}}, {@code ${player}}, {@code ${cause}}, {@code ${world}}, {@code ${x}},
             * {@code ${y}}, {@code ${z}}, {@code ${score}} and {@code ${exp}}.
             */
            @Comment("""
                A player had died
                Usages: ${username}, ${player}, ${cause}, ${world}, ${x}, ${y}, ${z}, ${score} and ${exp}""")
            public String death = "**${player}** ${cause}! :skull:\n_${world} | ${x}, ${y}, ${z}_";

            /**
             * A named animal/monster (with name tag) had died.
             *
             * <p>Usages: {@code ${name}}, {@code ${cause}}, {@code ${world}}, {@code ${x}}, {@code ${y}} and
             * {@code ${z}}.
             */
            @Comment("""
                A named animal/monster (with name tag) had died
                Usages: ${name}, ${cause}, ${world}, ${x}, ${y} and ${z}""")
            public String grief = "**${name}** ${cause}! :coffin:";

            /**
             * A player unlocked an advancement.
             *
             * <p>Usages: {@code ${username}}, {@code ${player}}, {@code ${type}}, {@code ${title}}, and
             * {@code ${description}}.
             */
            @Comment("""
                A player unlocked an advancement
                Usages: ${username}, ${player}, ${type}, ${title}, and ${description}""")
            public String advancement = "**${player}** completed the ${type} **${title}**! :clap:\n_${description}_";

            /**
             * A player teleported to another dimension.
             *
             * <p>Usages: {@code ${username}}, {@code ${player}}, {@code ${world}}, {@code ${x}}, {@code ${y}},
             * {@code ${z}}, {@code ${origin}}, {@code ${origin_x}}, {@code ${origin_y}} and {@code ${origin_z}}.
             */
            @Comment("""
                A player teleported to another dimension
                Usages: ${username}, ${player}, ${world}, ${x}, ${y}, ${z}, ${origin}, ${origin_x}, ${origin_y} and ${origin_z}""")
            public String teleport = "**${player}** entered ${world}. :cyclone:";

            /**
             * A player joined the game.
             *
             * <p>Usages: {@code ${username}}, {@code ${player}} and {@code ${world}}.
             */
            @Comment("""
                A player joined the game
                Usages: ${username}, ${player} and ${world}""")
            public String join = "**${player}** joined!";

            /**
             * A player left the game.
             *
             * <p>Usages: {@code ${username}}, {@code ${player}}, {@code ${world}} and {@code ${playtime}}.
             */
            @Comment("""
                A player left the game
                Usages: ${username}, ${player}, ${world} and ${playtime}""")
            public String leave = "**${player}** left!";

            /** The server began to start. */
            @Comment("The server began to start")
            public String starting = "Server is starting... :fingers_crossed:";

            /**
             * The server started and is accepting connections.
             *
             * <p>Usages: {@code ${uptime}}.
             */
            @Comment("""
                The server started and is accepting connections
                Usages: ${uptime}""")
            public String started = "Server started (took ${uptime:s.SSS}s) :ok_hand:";

            /**
             * The server began to stop.
             *
             * <p>Usages: {@code ${uptime}}.
             */
            @Comment("""
                The server began to stop
                Usages: ${uptime}""")
            public String stopping = "Server is stopping... :raised_hand:";

            /**
             * The server stopped and is offline.
             *
             * <p>Usages: {@code ${uptime}}.
             */
            @Comment("""
                The server stopped and is offline
                Usages: ${uptime}""")
            public String stopped = "Server stopped! :no_entry:";

            /**
             * The server stopped unexpectedly and is inaccessible.
             *
             * <p>Usages: {@code ${reason}} and {@code ${uptime}}.
             */
            @Comment("""
                The server stopped unexpectedly and is inaccessible
                Usages: ${reason} and ${uptime}""")
            public String crashed = "Server crash detected! :warning:\n_${reason}_";

            /** True if a crash report should be attached to any server crash messages. */
            @Comment("True if a crash report should be attached to any server crash messages")
            public boolean uploadCrashReport = false;
        }

        /** Discord events relayed to Minecraft. */
        @Category("Minecraft")
        @Comment("Discord events relayed to Minecraft")
        public MinecraftSchema minecraft = new MinecraftSchema();

        /**
         * Minecraft events configuration schema.
         */
        @SuppressWarnings("checkstyle:linelength")
        public static class MinecraftSchema
        {
            /**
             * A user sent a message.
             *
             * <p>Usages: {@code ${author}}, {@code ${tag}}, {@code ${username}}, {@code ${discriminator}},
             * {@code ${message}} and {@code ${raw}}.
             */
            @Comment("""
                A user sent a message
                Usages: ${author}, ${tag}, ${username}, ${discriminator}, ${message} and ${raw}""")
            public String chat = "[\"\",{\"text\":\"${author}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${tag} \"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"\",{\"text\":\"Sent from Discord\",\"italic\":true}]}},{\"text\":\" > \",\"color\":\"dark_gray\"},{\"text\":\"${message}\"}]";

            /**
             * A user sent a message in reply to another.
             *
             * <p>Usages: {@code ${author}}, {@code ${tag}}, {@code ${username}}, {@code ${discriminator}},
             * {@code ${message}}, {@code ${reply_author}}, {@code ${reply_tag}}, {@code ${reply_username}},
             * {@code ${reply_discriminator}} and {@code ${reply_message}}.
             */
            @Comment("""
                A user sent a message in reply to another
                Usages: ${author}, ${tag}, ${username}, ${discriminator}, ${message}, ${reply_author}, ${reply_tag}, ${reply_username}, ${reply_discriminator} and ${reply_message}""")
            public String reply = "[\"\",{\"text\":\"${author}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${tag} \"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"Sent from Discord\",\"italic\":true}]}},\" \",{\"text\":\"(in reply to ${reply_author})\",\"color\":\"#99dcff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${reply_tag} \"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"${reply_message:%.50s}...\"]}},{\"text\":\" > \",\"color\":\"dark_gray\"},\"${message}\"]";

            /**
             * A user edited their recently sent message.
             *
             * <p>Usages: {@code ${author}}, {@code ${tag}}, {@code ${username}}, {@code ${discriminator}},
             * {@code ${diff}}, {@code ${message}}, {@code ${raw}}, {@code ${original}} and {@code ${original_raw}}.
             */
            @Comment("""
                A user edited their recently sent message
                Usages: ${author}, ${tag}, ${username}, ${discriminator}, ${diff}, ${message}, ${raw}, ${original} and ${original_raw}""")
            public String edit = "[\"\",{\"text\":\"${author}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${tag} \"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"\",{\"text\":\"Sent from Discord\",\"italic\":true}]}},{\"text\":\" > \",\"color\":\"dark_gray\"},{\"text\":\"${diff}\"}]";

            /**
             * A user reacted to a recent message.
             *
             * <p>Usages: {@code ${issuer}}, {@code ${issuer_tag}}, {@code ${issuer_username}},
             * {@code ${issuer_discriminator}}, {@code ${author}}, {@code ${author_tag}}, {@code ${author_username}},
             * {@code ${author_discriminator}} and {@code ${emote}}.
             */
            @Comment("""
                A user reacted to a recent message
                Usages: ${issuer}, ${issuer_tag}, ${issuer_username}, ${issuer_discriminator}, ${author}, ${author_tag}, ${author_username}, ${author_discriminator} and ${emote}""")
            public String react = "[\"\",{\"text\":\"${issuer}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${issuer_tag} \"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"\",{\"text\":\"Sent from Discord\",\"italic\":true}]}},{\"text\":\" reacted with \"},{\"text\":\"${emote}\",\"color\":\"green\"},{\"text\": \" to \"},{\"text\":\"${author}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${author_tag} \"}},{\"text\":\"'s message\"}]";

            /**
             * A user removed their reaction from a recent message.
             *
             * <p>Usages: {@code ${issuer}}, {@code ${issuer_tag}}, {@code ${issuer_username}},
             * {@code ${issuer_discriminator}}, {@code ${author}}, {@code ${author_tag}},
             * {@code ${author_username}}, {@code ${author_discriminator}} and {@code ${emote}}.
             */
            @Comment("""
                A user removed their reaction from a recent message
                Usages: ${issuer}, ${issuer_tag}, ${issuer_username}, ${issuer_discriminator}, ${author}, ${author_tag}, ${author_username}, ${author_discriminator} and ${emote}""")
            public String unreact = "[\"\",{\"text\":\"${issuer}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${issuer_tag} \"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"\",{\"text\":\"Sent from Discord\",\"italic\":true}]}},{\"text\":\" removed their reaction of \"},{\"text\":\"${emote}\",\"color\":\"red\"},{\"text\": \" from \"},{\"text\":\"${author}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${author_tag} \"}},{\"text\":\"'s message\"}]";

            /**
             * A user sent a message that contained attachments.
             *
             * <p>Usages: {@code ${author}}, {@code ${tag}}, {@code ${username}},
             * {@code ${discriminator}}, {@code ${url}}, {@code ${name}}, {@code ${ext}} and
             * {@code ${size}}.
             */
            @Comment("""
                A user sent a message that contained attachments
                Usages: ${author}, ${tag}, ${username}, ${discriminator}, ${url}, ${name}, ${ext} and ${size}""")
            public String attachment = "[\"\",{\"text\":\"${author}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${tag} \"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"\",{\"text\":\"Sent from Discord\",\"italic\":true}]}},{\"text\":\" > \",\"color\":\"dark_gray\"},{\"text\":\"${name}\",\"color\":\"blue\",\"underlined\":true,\"clickEvent\":{\"action\":\"open_url\",\"value\":\"${url}\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":{\"text\":\"${ext} (${size})\"}}}]";
        }

        /**
         * Returns whether a given Minecraft world is within scope.
         *
         * @param world Minecraft world
         * @return true if the Minecraft world is in-scope
         */
        public boolean hasWorld(World world)
        {
            return world == null
                || dimensions == null
                || dimensions.length == 0
                || Arrays.asList(dimensions).contains(world.getRegistryKey().getValue().toString());
        }
    }

    /**
     * Determines whether the given channel identifier relates to any chat entries.
     *
     * @param identifier Discord channel identifier
     * @return true if the channel identifier is in use by any chat entries
     */
    public boolean hasChannel(final long identifier)
    {
        return Arrays.stream(this.entries).anyMatch(entry -> entry.id == identifier);
    }

    /**
     * Registers and prepares a new configuration instance.
     *
     * @return registered config holder
     * @see AutoConfig#register(Class, ConfigSerializer.Factory)
     */
    public static ConfigHolder<ChatConfig> init()
    {
        // Register the config
        ConfigHolder<ChatConfig> holder = AutoConfig.register(ChatConfig.class, JanksonConfigSerializer::new);

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) ->
            AutoConfig.getConfigHolder(ChatConfig.class).load());

        return holder;
    }
}
