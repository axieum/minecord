package me.axieum.mcmod.minecord.impl.chat.config;

import java.util.Arrays;

import eu.pb4.placeholders.api.node.TextNode;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import me.axieum.mcmod.minecord.api.Minecord;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.parseNode;

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
             * <ul>
             *   <li>{@code ${message}} &mdash; the formatted message contents</li>
             * </ul>
             */
            @Comment("""
                A player sent an in-game chat message
                Usages: ${message}""")
            public String chat = "`${minecord:world}` **${minecord:player}** > ${message}";

            /** Pre-parsed 'chat' text node. */
            public transient TextNode chatNode;

            /** True if players are allowed to send links, possibly leading to image previews. */
            @Comment("True if players are allowed to send links, possibly leading to image previews")
            public boolean allowLinks = true;

            /**
             * A player sent an in-game message via the {@code /me} command.
             *
             * <p>Note: there is no player or world if sent from a command block or console!
             *
             * <ul>
             *   <li>{@code ${action}} &mdash; the formatted action</li>
             * </ul>
             */
            @Comment("""
                A player sent an in-game message via the '/me' command
                Usages: ${action}""")
            public String emote = "`${minecord:world}` **${minecord:player}** _${action}_";

            /** Pre-parsed 'emote' text node. */
            public transient TextNode emoteNode;

            /**
             * An admin broadcast an in-game message via the {@code /say} command.
             *
             * <p>Note: there is no player or world if sent from a command block or console!
             *
             * <ul>
             *   <li>{@code ${message}} &mdash; the formatted message contents</li>
             * </ul>
             */
            @Comment("""
                An admin broadcast an in-game message via the '/say' command
                Usages: ${message}""")
            public String say = "**[${minecord:player}]** ${message}";

            /** Pre-parsed 'say' text node. */
            public transient TextNode sayNode;

            /**
             * An admin broadcast an in-game message to all players via the {@code /tellraw @a} command.
             *
             * <ul>
             *   <li>{@code ${message}} &mdash; the formatted message contents</li>
             * </ul>
             */
            @Comment("""
                An admin broadcast an in-game message to all players via the '/tellraw @a' command
                Usages: ${message}""")
            public String tellraw = "${message}";

            /** Pre-parsed 'tellraw' text node. */
            public transient TextNode tellrawNode;

            /**
             * A player had died.
             *
             * <ul>
             *   <li>{@code ${cause}} &mdash; the reason for the player's death</li>
             *   <li>{@code ${exp}} &mdash; the player's number of experience levels before they died</li>
             *   <li>{@code ${lifespan [format]}} &mdash; the total time for which the player was alive for</li>
             *   <li>{@code ${score}} &mdash; the player's total score before they died</li>
             * </ul>
             */
            @Comment("""
                A player had died
                Usages: ${cause}, ${exp}, ${lifespan [format]} and ${score}""")
            public String death = "**${minecord:player}** ${cause}! :skull:\n_${minecord:world} at ${player:pos_x 0}, ${player:pos_y 0}, ${player:pos_z 0}_";

            /** Pre-parsed 'death' text node. */
            public transient TextNode deathNode;

            /**
             * A named animal/monster (with name tag) had died.
             *
             * <ul>
             *   <li>{@code ${name}} &mdash; the entity's display name</li>
             *   <li>{@code ${cause}} &mdash; the reason for the entity's death</li>
             *   <li>{@code ${pos_x}} &mdash; the X coordinate of where the entity died</li>
             *   <li>{@code ${pos_y}} &mdash; the Y coordinate of where the entity died</li>
             *   <li>{@code ${pos_z}} &mdash; the Z coordinate of where the entity died</li>
             * </ul>
             */
            @Comment("""
                A named animal/monster (with name tag) had died
                Usages: ${name}, ${cause}, ${pos_x}, ${pos_y} and ${pos_z}""")
            public String grief = "**${name}** ${cause}! :coffin:\n_${minecord:world} at ${pos_x}, ${pos_y}, ${pos_z}_";

            /** Pre-parsed 'grief' text node. */
            public transient TextNode griefNode;

            /**
             * A player unlocked an advancement task.
             *
             * <ul>
             *   <li>{@code ${title}} &mdash; the title of the advancement (task)</li>
             *   <li>{@code ${description}} &mdash; the description of the advancement (task)</li>
             * </ul>
             */
            @Comment("""
                A player unlocked an advancement task
                Usages: ${title} and ${description}""")
            public String advancementTask = "**${minecord:player}** has made the advancement **${title}**! :clap:\n_${description}_";

            /** Pre-parsed 'advancementTask' text node. */
            public transient TextNode advancementTaskNode;

            /**
             * A player reached an advancement goal.
             *
             * <ul>
             *   <li>{@code ${title}} &mdash; the title of the advancement</li>
             *   <li>{@code ${description}} &mdash; the description of the advancement</li>
             * </ul>
             */
            @Comment("""
                A player reached an advancement goal
                Usages: ${title} and ${description}""")
            public String advancementGoal = "**${minecord:player}** has reached the goal **${title}**! :clap:\n_${description}_";

            /** Pre-parsed 'advancementGoal' text node. */
            public transient TextNode advancementGoalNode;

            /**
             * A player completed an advancement challenge.
             *
             * <ul>
             *   <li>{@code ${title}} &mdash; the title of the advancement</li>
             *   <li>{@code ${description}} &mdash; the description of the advancement</li>
             * </ul>
             */
            @Comment("""
                A player completed an advancement challenge
                Usages: ${title} and ${description}""")
            public String advancementChallenge = "**${minecord:player}** has completed the challenge **${title}**! :trophy:\n_${description}_";

            /** Pre-parsed 'advancementChallenge' text node. */
            public transient TextNode advancementChallengeNode;

            /**
             * A player teleported to another dimension.
             *
             * <ul>
             *   <li>{@code ${world}} &mdash; the name of the world the player entered</li>
             *   <li>{@code ${pos_x}} &mdash; the X coordinate of where the player entered</li>
             *   <li>{@code ${pos_y}} &mdash; the Y coordinate of where the player entered</li>
             *   <li>{@code ${pos_z}} &mdash; the Z coordinate of where the player entered</li>
             *   <li>{@code ${origin}} &mdash; the name of the world the player left</li>
             *   <li>{@code ${origin_pos_x}} &mdash; the X coordinate of where the player left</li>
             *   <li>{@code ${origin_pos_y}} &mdash; the Y coordinate of where the player left</li>
             *   <li>{@code ${origin_pos_z}} &mdash; the Z coordinate of where the player left</li>
             * </ul>
             */
            @Comment("""
                A player teleported to another dimension
                Usages: ${world}, ${pos_x}, ${pos_y}, ${pos_z}, ${origin}, ${origin_pos_x}, ${origin_pos_y} and ${origin_pos_z}""")
            public String teleport = "**${minecord:player}** entered ${world}. :cyclone:";

            /** Pre-parsed 'teleport' text node. */
            public transient TextNode teleportNode;

            /** A player joined the game. */
            @Comment("A player joined the game")
            public String join = "**${minecord:player}** joined!";

            /** Pre-parsed 'join' text node. */
            public transient TextNode joinNode;

            /** A player left the game. */
            @Comment("A player left the game")
            public String leave = "**${minecord:player}** left!";

            /** Pre-parsed 'leave' text node. */
            public transient TextNode leaveNode;

            /** The server began to start. */
            @Comment("The server began to start")
            public String starting = "Server is starting... :fingers_crossed:";

            /** Pre-parsed 'starting' text node. */
            public transient TextNode startingNode;

            /**
             * The server started and is accepting connections.
             *
             * <ul>
             *   <li>{@code ${uptime [format]}} &mdash; the time taken for the server to start</li>
             * </ul>
             */
            @Comment("""
                The server started and is accepting connections
                Usages: ${uptime [format]}""")
            public String started = "Server started (took ${uptime s.SSS}s) :ok_hand:";

            /** Pre-parsed 'started' text node. */
            public transient TextNode startedNode;

            /**
             * The server began to stop.
             *
             * <ul>
             *   <li>{@code ${uptime [format]}} &mdash; the total time for which the server has been online for</li>
             * </ul>
             */
            @Comment("""
                The server began to stop
                Usages: ${uptime [format]}""")
            public String stopping = "Server is stopping... :raised_hand:";

            /** Pre-parsed 'stopping' text node. */
            public transient TextNode stoppingNode;

            /**
             * The server stopped and is offline.
             *
             * <ul>
             *   <li>{@code ${uptime [format]}} &mdash; the total time for which the server has been online for</li>
             * </ul>
             */
            @Comment("""
                The server stopped and is offline
                Usages: ${uptime [format]}""")
            public String stopped = "Server stopped! :no_entry:";

            /** Pre-parsed 'stopped' text node. */
            public transient TextNode stoppedNode;

            /**
             * The server stopped unexpectedly and is inaccessible.
             *
             * <ul>
             *   <li>{@code ${reason}} &mdash; the reason for the server stopping, if crashed</li>
             *   <li>{@code ${uptime [format]}} &mdash; the total time for which the server has been online for</li>
             * </ul>
             */
            @Comment("""
                The server stopped unexpectedly and is inaccessible
                Usages: ${reason} and ${uptime [format]}""")
            public String crashed = "Server crash detected! :warning:\n_${reason}_";

            /** Pre-parsed 'crashed' text node. */
            public transient TextNode crashedNode;

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
             * <ul>
             *   <li>{@code ${author}} &mdash; the author's nickname or username</li>
             *   <li>{@code ${tag}} &mdash; the author's tag (i.e. username#discriminator), e.g. Axieum#1001</li>
             *   <li>{@code ${username}} &mdash; the author's username, e.g. Axieum</li>
             *   <li>{@code ${discriminator}} &mdash; the author's username discriminator, e.g. 1001</li>
             *   <li>{@code ${message}} &mdash; the formatted message contents</li>
             *   <li>{@code ${raw}} &mdash; the raw message contents</li>
             * </ul>
             */
            @Comment("""
                A user sent a message
                Usages: ${author}, ${tag}, ${username}, ${discriminator}, ${message} and ${raw}""")
            public String chat = "<<cmd:'@${tag} '><hover:show_text:'<i>Sent from Discord</i>'><color:#00aaff>${author}</color></hover></cmd>> ${message}";

            /** Pre-parsed 'chat' text node. */
            public transient TextNode chatNode;

            /**
             * A user sent a message in reply to another.
             *
             * <ul>
             *   <li>{@code ${author}} &mdash; the author's nickname or username</li>
             *   <li>{@code ${tag}} &mdash; the author's tag (i.e. username#discriminator), e.g. Axieum#1001</li>
             *   <li>{@code ${username}} &mdash; the author's username, e.g. Axieum</li>
             *   <li>{@code ${discriminator}} &mdash; the author's username discriminator, e.g. 1001</li>
             *   <li>{@code ${message}} &mdash; the formatted message contents</li>
             *   <li>{@code ${raw}} &mdash; the raw message contents</li>
             *   <li>{@code ${reply_author}} &mdash; the replied message author's nickname or username</li>
             *   <li>{@code ${reply_tag}} &mdash; the replied message author's tag (i.e. username#discriminator), e.g. Axieum#1001</li>
             *   <li>{@code ${reply_username}} &mdash; the replied message author's username, e.g. Axieum</li>
             *   <li>{@code ${reply_discriminator}} &mdash; the replied message author's username discriminator, e.g. 1001</li>
             *   <li>{@code ${reply_message}} &mdash; the replied message formatted message contents</li>
             *   <li>{@code ${reply_raw}} &mdash; the replied message raw message contents</li>
             * </ul>
             */
            @Comment("""
                A user sent a message in reply to another
                Usages: ${author}, ${tag}, ${username}, ${discriminator}, ${message}, ${raw}, ${reply_author}, ${reply_tag}, ${reply_username}, ${reply_discriminator}, ${reply_message} and ${reply_raw}""")
            public String reply = "<<cmd:'@${tag} '><hover:show_text:'<i>Sent from Discord</i>'><color:#00aaff>${author}</color></hover></cmd>> (in reply to <cmd:'@${reply_tag} '><hover:show_text:'${reply_message}'><color:#00aaff>${reply_author}</color></hover></cmd>) ${message}";

            /** Pre-parsed 'crashed' text node. */
            public transient TextNode replyNode;

            /**
             * A user edited their recently sent message.
             *
             * <ul>
             *   <li>{@code ${author}} &mdash; the author's nickname or username</li>
             *   <li>{@code ${tag}} &mdash; the author's tag (i.e. username#discriminator), e.g. Axieum#1001</li>
             *   <li>{@code ${username}} &mdash; the author's username, e.g. Axieum</li>
             *   <li>{@code ${discriminator}} &mdash; the author's username discriminator, e.g. 1001</li>
             *   <li>{@code ${diff}} &mdash; the difference between the original and new message</li>
             *   <li>{@code ${message}} &mdash; the new formatted message contents</li>
             *   <li>{@code ${raw}} &mdash; the new raw message contents</li>
             *   <li>{@code ${original}} &mdash; the old formatted message contents</li>
             *   <li>{@code ${original_raw}} &mdash; the old raw message contents</li>
             * </ul>
             */
            @Comment("""
                A user edited their recently sent message
                Usages: ${author}, ${tag}, ${username}, ${discriminator}, ${diff}, ${message}, ${raw}, ${original} and ${original_raw}""")
            public String edit = "<<cmd:'@${tag} '><hover:show_text:'<i>Sent from Discord</i>'><color:#00aaff>${author}</color></hover></cmd>> ${diff}";

            /** Pre-parsed 'edit' text node. */
            public transient TextNode editNode;

            /**
             * A user reacted to a recent message.
             *
             * <ul>
             *   <li>{@code ${issuer}} &mdash; the issuer's nickname or username</li>
             *   <li>{@code ${issuer_tag}} &mdash; the issuer's tag (i.e. username#discriminator), e.g. Axieum#1001</li>
             *   <li>{@code ${issuer_username}} &mdash; the issuer's username, e.g. Axieum</li>
             *   <li>{@code ${issuer_discriminator}} &mdash; the issuer's username discriminator, e.g. 1001</li>
             *   <li>{@code ${author}} &mdash; the author's nickname or username</li>
             *   <li>{@code ${author_tag}} &mdash; the author's tag (i.e. username#discriminator), e.g. Axieum#1001</li>
             *   <li>{@code ${author_username}} &mdash; the author's username, e.g. Axieum</li>
             *   <li>{@code ${author_discriminator}} &mdash; the author's username discriminator, e.g. 1001</li>
             *   <li>{@code ${emote}} &mdash; the emote used to react</li>
             * </ul>
             */
            @Comment("""
                A user reacted to a recent message
                Usages: ${issuer}, ${issuer_tag}, ${issuer_username}, ${issuer_discriminator}, ${author}, ${author_tag}, ${author_username}, ${author_discriminator} and ${emote}""")
            public String react = "<cmd:'@${issuer_tag} '><hover:show_text:'<i>Sent from Discord</i>'><color:#00aaff>${issuer}</color></hover></cmd> reacted with <green>${emote}</green> to <cmd:'@${author_tag} '><color:#00aaff>${author}</color></cmd>'s message";

            /** Pre-parsed 'react' text node. */
            public transient TextNode reactNode;

            /**
             * A user removed their reaction from a recent message.
             *
             * <ul>
             *   <li>{@code ${issuer}} &mdash; the issuer's nickname or username</li>
             *   <li>{@code ${issuer_tag}} &mdash; the issuer's tag (i.e. username#discriminator), e.g. Axieum#1001</li>
             *   <li>{@code ${issuer_username}} &mdash; the issuer's username, e.g. Axieum</li>
             *   <li>{@code ${issuer_discriminator}} &mdash; the issuer's username discriminator, e.g. 1001</li>
             *   <li>{@code ${author}} &mdash; the author's nickname or username</li>
             *   <li>{@code ${author_tag}} &mdash; the author's tag (i.e. username#discriminator), e.g. Axieum#1001</li>
             *   <li>{@code ${author_username}} &mdash; the author's username, e.g. Axieum</li>
             *   <li>{@code ${author_discriminator}} &mdash; the author's username discriminator, e.g. 1001</li>
             *   <li>{@code ${emote}} &mdash; the emote used to react</li>
             * </ul>
             */
            @Comment("""
                A user removed their reaction from a recent message
                Usages: ${issuer}, ${issuer_tag}, ${issuer_username}, ${issuer_discriminator}, ${author}, ${author_tag}, ${author_username}, ${author_discriminator} and ${emote}""")
            public String unreact = "<cmd:'@${issuer_tag} '><hover:show_text:'<i>Sent from Discord</i>'><color:#00aaff>${issuer}</color></hover></cmd> removed their reaction of <red>${emote}</red> from <cmd:'@${author_tag} '><color:#00aaff>${author}</color></cmd>'s message";

            /** Pre-parsed 'unreact' text node. */
            public transient TextNode unreactNode;

            /**
             * A user sent a message that contained stickers.
             *
             * <ul>
             *   <li>{@code ${author}} &mdash; the author's nickname or username</li>
             *   <li>{@code ${tag}} &mdash; the author's tag (i.e. username#discriminator), e.g. Axieum#1001</li>
             *   <li>{@code ${username}} &mdash; the author's username, e.g. Axieum</li>
             *   <li>{@code ${discriminator}} &mdash; the author's username discriminator, e.g. 1001</li>
             *   <li>{@code ${url}} &mdash; the link to the sticker image</li>
             *   <li>{@code ${name}} &mdash; the name of the sticker</li>
             * </ul>
             */
            @Comment("""
                A user sent a message that contained stickers
                Usages: ${author}, ${tag}, ${username}, ${discriminator}, ${url} and ${name}""")
            public String sticker = "<<cmd:'@${tag} '><hover:show_text:'<i>Sent from Discord</i>'><color:#00aaff>${author}</color></hover></cmd>> <url:'${url}'><hover:show_text:'Sticker'><underline><blue>${name}</blue></underline></hover></url>";

            /** Pre-parsed 'sticker' text node. */
            public transient TextNode stickerNode;

            /**
             * A user sent a message that contained attachments.
             *
             * <ul>
             *   <li>{@code ${author}} &mdash; the author's nickname or username</li>
             *   <li>{@code ${tag}} &mdash; the author's tag (i.e. username#discriminator), e.g. Axieum#1001</li>
             *   <li>{@code ${username}} &mdash; the author's username, e.g. Axieum</li>
             *   <li>{@code ${discriminator}} &mdash; the author's username discriminator, e.g. 1001</li>
             *   <li>{@code ${url}} &mdash; the link to the file to download</li>
             *   <li>{@code ${name}} &mdash; the file name that was uploaded</li>
             *   <li>{@code ${ext}} &mdash; the file extension/type</li>
             *   <li>{@code ${size}} &mdash; the file size for humans</li>
             * </ul>
             */
            @Comment("""
                A user sent a message that contained attachments
                Usages: ${author}, ${tag}, ${username}, ${discriminator}, ${url}, ${name}, ${ext} and ${size}""")
            public String attachment = "<<cmd:'@${tag} '><hover:show_text:'<i>Sent from Discord</i>'><color:#00aaff>${author}</color></hover></cmd>> <url:'${url}'><hover:show_text:'${ext} (${size})'><underline><blue>${name}</blue></underline></hover></url>";

            /** Pre-parsed 'attachment' text node. */
            public transient TextNode attachmentNode;
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

    @Override
    public void validatePostLoad()
    {
        // Parse Discord event templates
        parseDiscordTemplates();
        // Parse Minecraft event templates
        if (Minecord.getInstance().getMinecraft().isPresent()) {
            parseMinecraftTemplates();
        }
    }

    /**
     * Parses Discord event templates into text nodes.
     *
     * @see me.axieum.mcmod.minecord.api.util.PlaceholdersExt#parseNode(String)
     * @see me.axieum.mcmod.minecord.impl.chat.config.ChatConfig.ChatEntrySchema.DiscordSchema
     */
    public void parseDiscordTemplates()
    {
        Arrays.stream(entries).forEach(entry -> {
            entry.discord.chatNode = parseNode(entry.discord.chat);
            entry.discord.emoteNode = parseNode(entry.discord.emote);
            entry.discord.sayNode = parseNode(entry.discord.say);
            entry.discord.tellrawNode = parseNode(entry.discord.tellraw);
            entry.discord.deathNode = parseNode(entry.discord.death);
            entry.discord.griefNode = parseNode(entry.discord.grief);
            entry.discord.advancementTaskNode = parseNode(entry.discord.advancementTask);
            entry.discord.advancementGoalNode = parseNode(entry.discord.advancementGoal);
            entry.discord.advancementChallengeNode = parseNode(entry.discord.advancementChallenge);
            entry.discord.teleportNode = parseNode(entry.discord.teleport);
            entry.discord.joinNode = parseNode(entry.discord.join);
            entry.discord.leaveNode = parseNode(entry.discord.leave);
            entry.discord.startingNode = parseNode(entry.discord.starting);
            entry.discord.startedNode = parseNode(entry.discord.started);
            entry.discord.stoppingNode = parseNode(entry.discord.stopping);
            entry.discord.stoppedNode = parseNode(entry.discord.stopped);
            entry.discord.crashedNode = parseNode(entry.discord.crashed);
        });
    }

    /**
     * Parses Minecraft event templates into text nodes.
     *
     * <p>The Minecraft server MUST be available with its registry bootstrapped!
     *
     * @see me.axieum.mcmod.minecord.api.util.PlaceholdersExt#parseNode(String)
     * @see me.axieum.mcmod.minecord.impl.chat.config.ChatConfig.ChatEntrySchema.DiscordSchema
     */
    public void parseMinecraftTemplates()
    {
        Arrays.stream(entries).forEach(entry -> {
            entry.minecraft.chatNode = parseNode(entry.minecraft.chat);
            entry.minecraft.replyNode = parseNode(entry.minecraft.reply);
            entry.minecraft.editNode = parseNode(entry.minecraft.edit);
            entry.minecraft.reactNode = parseNode(entry.minecraft.react);
            entry.minecraft.unreactNode = parseNode(entry.minecraft.unreact);
            entry.minecraft.stickerNode = parseNode(entry.minecraft.sticker);
            entry.minecraft.attachmentNode = parseNode(entry.minecraft.attachment);
        });
    }

    /**
     * Registers and loads a new configuration instance.
     *
     * @see AutoConfig#register(Class, ConfigSerializer.Factory)
     */
    public static void load()
    {
        // Register (and load) the config
        AutoConfig.register(ChatConfig.class, JanksonConfigSerializer::new);

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) ->
            AutoConfig.getConfigHolder(ChatConfig.class).load());
    }
}
