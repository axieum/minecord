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

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

@Config(name = "minecord/chat")
public class ChatConfig implements ConfigData
{
    @Comment("True if emojis should be treated as unicode - useful if your players' client supports emojis")
    public boolean useUnicodeEmojis = false;

    @Comment("Chat Configurations")
    public ChatEntry[] entries = {new ChatEntry()};

    public static class ChatEntry
    {
        @Comment("A channel identifier in Discord to observe")
        public long id;

        @Comment("If non-empty, reduces the scope of all events to the listed Minecraft dimension IDs, "
            + "e.g. 'minecraft:the_nether'")
        public String[] dimensions = {};

        @Category("Discord")
        @Comment("Minecraft events relayed to Discord")
        public Discord discord = new Discord();

        public static class Discord
        {
            @Comment("""
                A player sent an in-game chat message
                Usages: ${username}, ${player}, ${message} and ${world}""")
            public String chat = "`${world}` **${player}** > ${message}";

            @Comment("""
                A player had died
                Usages: ${username}, ${player}, ${cause}, ${world}, ${x}, ${y}, ${z}, ${score} and ${exp}""")
            public String death = "**${player}** ${cause}! :skull:\n_${world} | ${x}, ${y}, ${z}_";

            @Comment("""
                A named animal/monster (with name tag) had died
                Usages: ${name}, ${cause}, ${world}, ${x}, ${y} and ${z}""")
            public String grief = "**${name}** ${cause}! :coffin:";

            @Comment("""
                A player unlocked an advancement
                Usages: ${username}, ${player}, ${type}, ${title}, and ${description}""")
            public String advancement = "**${player}** completed the ${type} **${title}**! :clap:\n_${description}_";

            @Comment("""
                A player teleported to another dimension
                Usages: ${username}, ${player}, ${origin} and ${destination}""")
            public String teleport = "**${player}** entered ${destination}. :cyclone:";

            @Comment("""
                A player joined the game
                Usages: ${username}, ${player} and ${world}""")
            public String join = "**${player}** joined!";

            @Comment("""
                A player left the game
                Usages: ${username}, ${player}, ${world} and ${playtime}""")
            public String leave = "**${player}** left!";

            @Comment("The server began to start")
            public String starting = "Server is starting... :fingers_crossed:";

            @Comment("""
                The server started and is accepting connections
                Usages: ${uptime}""")
            public String started = "Server started (took ${uptime:s.SSS}s) :ok_hand:";

            @Comment("""
                The server began to stop
                Usages: ${uptime}""")
            public String stopping = "Server is stopping... :raised_hand:";

            @Comment("""
                The server stopped and is offline
                Usages: ${uptime}""")
            public String stopped = "Server stopped! :no_entry:";

            @Comment("""
                The server stopped unexpectedly and is inaccessible
                Usages: ${reason} and ${uptime}""")
            public String crashed = "Server crash detected! :warning:\n_${reason}_";

            @Comment("True if a crash report should be attached to any server crash messages")
            public boolean uploadCrashReport = false;
        }

        @Category("Minecraft")
        @Comment("Discord events relayed to Minecraft")
        public Minecraft minecraft = new Minecraft();

        @SuppressWarnings("checkstyle:linelength")
        public static class Minecraft
        {
            @Comment("""
                A user sent a message
                Usages: ${author}, ${tag}, ${username}, ${discriminator} and ${message}""")
            public String chat = "[\"\",{\"text\":\"${author}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${tag} \"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"\",{\"text\":\"Sent from Discord\",\"italic\":true}]}},{\"text\":\" > \",\"color\":\"dark_gray\"},{\"text\":\"${message}\"}]";

            @Comment("""
                A user edited their recently sent message
                Usages: ${author}, ${tag}, ${username}, ${discriminator}, ${diff}, ${original} and ${message}""")
            public String edit = "[\"\",{\"text\":\"${author}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${tag} \"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"\",{\"text\":\"Sent from Discord\",\"italic\":true}]}},{\"text\":\" > \",\"color\":\"dark_gray\"},{\"text\":\"${diff}\"}]";

            @Comment("""
                A user reacted to a recent message
                Usages: ${reactor}, ${reactor_tag}, ${reactor_username}, ${reactor_discriminator}, ${author}, ${author_tag}, ${author_username}, ${author_discriminator} and ${emote}""")
            public String react = "[\"\",{\"text\":\"${issuer}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${issuer_tag} \"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"\",{\"text\":\"Sent from Discord\",\"italic\":true}]}},{\"text\":\" reacted with \"},{\"text\":\"${emote}\",\"color\":\"green\"},{\"text\": \" to \"},{\"text\":\"${author}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${author_tag} \"}},{\"text\":\"'s message\"}]";

            @Comment("""
                A user removed their reaction from a recent message
                Usages: ${reactor}, ${reactor_tag}, ${reactor_username}, ${reactor_discriminator}, ${author}, ${author_tag}, ${author_username}, ${author_discriminator} and ${emote}""")
            public String unreact = "[\"\",{\"text\":\"${issuer}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${issuer_tag} \"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"\",{\"text\":\"Sent from Discord\",\"italic\":true}]}},{\"text\":\" removed their reaction of \"},{\"text\":\"${emote}\",\"color\":\"red\"},{\"text\": \" from \"},{\"text\":\"${author}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${author_tag} \"}},{\"text\":\"'s message\"}]";

            @Comment("""
                A user sent a message that contained attachments
                Usages: ${author}, ${tag}, ${username}, ${discriminator}, ${url}, ${name}, ${ext} and ${size}""")
            public String attachment = "[\"\",{\"text\":\"${author}\",\"color\":\"#00aaff\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@${tag} \"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"\",{\"text\":\"Sent from Discord\",\"italic\":true}]}},{\"text\":\" > \",\"color\":\"dark_gray\"},{\"text\":\"${name}\",\"color\":\"blue\",\"underlined\":true,\"clickEvent\":{\"action\":\"open_url\",\"value\":\"${url}\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":{\"text\":\"${ext} (${size})\"}}}]";
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
