package me.axieum.mcmod.minecord.api.cmds.builtin;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.node.EmptyNode;
import eu.pb4.placeholders.api.node.TextNode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import dev.architectury.event.EventResult;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.cmds.MinecordCommand;
import me.axieum.mcmod.minecord.api.config.CommandConfig;
import me.axieum.mcmod.minecord.api.event.MinecraftEvents;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import static me.axieum.mcmod.minecord.api.Minecord.LOGGER;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * Custom Minecraft proxy Minecord command.
 */
public class CustomCommand extends MinecordCommand
{
    /** The permission level all Minecraft commands should run at. */
    private static final int PERMISSION_LEVEL = 4;

    /** The custom command config instance. */
    private final CommandConfig.CustomCommand config;

    /**
     * Constructs a new custom command.
     *
     * @param config custom command config
     */
    public CustomCommand(CommandConfig.CustomCommand config)
    {
        super(config.name, config.description);
        this.config = config;
        setEphemeral(config.ephemeral);
        setCooldown(config.cooldown);
        setCooldownScope(config.cooldownScope);
        data.setDefaultPermissions(
            config.allowByDefault ? DefaultMemberPermissions.ENABLED : DefaultMemberPermissions.DISABLED
        );
        Arrays.stream(config.options)
            .map(CommandConfig.BaseCommand.Option::getOptionData)
            .forEach(data::addOptions);
    }

    @Override
    public boolean isEphemeral()
    {
        return config.ephemeral;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @Nullable MinecraftServer server) throws Exception
    {
        assert server != null; // this.requiresMinecraft = true

        // Prepare the Minecraft command
        final String mcCommand;
        try {
            mcCommand = prepareCommand(config.commandNode, event.getOptions(), server);
        } catch (ParsingException | IllegalArgumentException e) {
            throw new Exception("Unable to prepare Minecraft command!", e);
        }

        // Fire an event to allow the command execution to be mutated or cancelled
        EventResult allowCommand = MinecraftEvents.ALLOW_COMMAND.invoker().shouldAllowCommand(
            this, event, server, mcCommand
        );
        if (allowCommand.isFalse()) return;

        // Create a temporary command source and hence output, to relay command feedback
        final String tag = event.getUser().getName();
        final String username = event.getMember() != null
            ? event.getMember().getEffectiveName()
            : event.getUser().getName();
        final DiscordCommandOutput output = new DiscordCommandOutput(event, server);
        final CommandSourceStack source = new CommandSourceStack(
            output, // command output
            Vec3.ZERO, Vec2.ZERO, server.overworld(), // location & world
            PERMISSION_LEVEL, tag, Component.literal(username), // permission & display name
            server, null // server & entity
        );

        // Attempt to proxy the Minecraft command
        final AtomicBoolean success = new AtomicBoolean(false);
        final AtomicInteger result = new AtomicInteger(0);
        @Nullable CommandSyntaxException error = null;
        try {
            LOGGER.info("@{} is running '/{}'", tag, mcCommand);

            // Parse the command and build its context
            final ParseResults<CommandSourceStack> parseResults = server.getCommands().getDispatcher().parse(
                mcCommand,
                source.withCallback((bl, i) -> {
                    success.set(bl); // if unsuccessful, it may choose to raise a command syntax exception
                    result.set(i);
                })
            );
            final CommandContext<CommandSourceStack> context = parseResults.getContext().build(mcCommand);

            // Analyse the command context for a player's UUID to show their avatar on any command feedback
            findPlayerUuids(context.getLastChild()).findFirst()
                .flatMap(uuid -> Minecord.getAvatarUrl(uuid, 16))
                .ifPresent(url -> output.thumbnailUrl = url);

            // Execute the command
            server.getCommands().getDispatcher().execute(parseResults);
        } catch (CommandSyntaxException e) {
            error = e;
        } finally {
            LOGGER.info(
                "@{} ran '/{}' with result {} ({})", tag, mcCommand, result.get(), success.get() ? "success" : "fail"
            );
        }

        // Finally, if there was still no command feedback sent, let them know with a default message
        // NB: This is to prevent the "The application did not respond" error in Discord, e.g. '/say' or '/tellraw'
        if (output.prevMessage == null) {
            if (error == null) {
                output.thumbnailUrl = null;
                source.sendSuccess(
                    () -> PlaceholdersExt.parseText(
                        CommandConfig.Messages.feedbackNode, PlaceholderContext.of(source), Collections.emptyMap()
                    ),
                    false
                );
            } else {
                source.sendFailure(Component.literal(error.getMessage()));
            }
        }
    }

    /**
     * Substitutes a Minecraft command template with options from a Discord command.
     *
     * @param command Minecraft command template using {@code ${<name>}} for the 'name' argument
     * @param options Discord command options to substitute into the template
     * @param server  optional Minecraft server for placeholder context
     * @return a prepared Minecraft command
     * @throws ParsingException if an invalid command option type is encountered
     */
    private static String prepareCommand(
        @NotNull TextNode command,
        List<OptionMapping> options,
        @Nullable MinecraftServer server
    ) throws ParsingException
    {
        if (command == EmptyNode.INSTANCE) return "";

        // Prepare new command placeholders
        final @Nullable PlaceholderContext ctx = server != null ? PlaceholderContext.of(server) : null;
        final HashMap<String, PlaceholderHandler> placeholders = new HashMap<>(options.size());
        options.forEach(option -> placeholders.put(option.getName(), string(option.getAsString())));

        // Parse the placeholders in the given command
        String result = PlaceholdersExt.parseString(command, ctx, placeholders).trim();

        // Strip any leading '/' if present, and return
        return !result.isEmpty() && result.charAt(0) == '/' ? result.substring(1) : result;
    }

    /**
     * Traverses the nodes of a Minecraft command context for a player-related
     * argument and returns their UUID if present.
     *
     * @param context Minecraft command context
     * @return a stream of Minecraft player UUIDs if present
     */
    private static Stream<String> findPlayerUuids(CommandContext<CommandSourceStack> context)
    {
        return context
            .getNodes()
            .stream()
            .map(ParsedCommandNode::getNode)
            .filter(node -> node instanceof ArgumentCommandNode)
            .map(node -> (ArgumentCommandNode<?, ?>) node)
            .map(node -> {
                try {
                    if (node.getType() instanceof EntityArgument) {
                        // Entity
                        return EntityArgument.getPlayer(context, node.getName()).getStringUUID();
                    } else if (node.getType() instanceof GameProfileArgument) {
                        // Game Profile
                        Collection<GameProfile> c = GameProfileArgument.getGameProfiles(context, node.getName());
                        return c.size() == 1 ? c.iterator().next().getId().toString() : null;
                    }
                } catch (CommandSyntaxException | IllegalArgumentException ignored) { /* ignored */ }
                return null;
            })
            .filter(Objects::nonNull);
    }

    /**
     * A virtual Minecraft command output for use via Discord.
     */
    private static final class DiscordCommandOutput implements CommandSource
    {
        private final SlashCommandInteractionEvent event;
        private final MinecraftServer server;
        public boolean erroneous = false;
        public @Nullable String prevMessage = null;
        public @Nullable String thumbnailUrl = null;

        /**
         * Constructs a new virtual command output for relaying feedback to Discord.
         *
         * @param event     JDA slash command event
         * @param server    Minecraft server
         */
        private DiscordCommandOutput(SlashCommandInteractionEvent event, MinecraftServer server)
        {
            this.event = event;
            this.server = server;
        }

        @Override
        public void sendSystemMessage(@NotNull Component component)
        {
            // Build an initial embed for the command feedback
            String text = prevMessage != null ? prevMessage + '\n' + component.getString() : component.getString();
            EmbedBuilder embed = new EmbedBuilder()
                // Set the colour to green for a success, and red for a failure
                .setColor(!erroneous ? 0x00ff00 : 0xff0000)
                // Set the thumbnail
                .setThumbnail(thumbnailUrl)
                // Set the message
                .setDescription(text);

            // Build and reply with the resulting embed
            if (prevMessage == null) {
                // This is the first reply, send a new message
                event.getHook().sendMessageEmbeds(embed.build()).queue();
            } else {
                // There already exists a reply, edit the original message
                event.getHook().editOriginalEmbeds(embed.build()).queue();
            }
            prevMessage = text;
        }

        @Override
        public boolean acceptsSuccess()
        {
            return true;
        }

        @Override
        public boolean acceptsFailure()
        {
            return erroneous = true;
        }

        @Override
        public boolean shouldInformAdmins()
        {
            return server.getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
        }
    }
}
