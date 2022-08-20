package me.axieum.mcmod.minecord.impl.cmds.command.discord;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;

import me.axieum.mcmod.minecord.api.cmds.command.MinecordCommand;
import me.axieum.mcmod.minecord.api.cmds.event.MinecordCommandEvents;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.cmds.config.CommandConfig;
import static me.axieum.mcmod.minecord.impl.cmds.MinecordCommandsImpl.LOGGER;
import static me.axieum.mcmod.minecord.impl.cmds.MinecordCommandsImpl.getConfig;

/**
 * Custom Minecraft proxy Minecord command.
 */
public class CustomCommand extends MinecordCommand
{
    // The permission level all Minecraft commands should run at
    private static final int PERMISSION_LEVEL = 4;
    // The custom command config instance
    private final CommandConfig.CustomCommandSchema config;

    /**
     * Initialises a new custom command.
     *
     * @param config custom command config
     */
    public CustomCommand(CommandConfig.CustomCommandSchema config)
    {
        super(config.name, config.description);
        this.config = config;
        setEphemeral(config.ephemeral);
        setCooldown(config.cooldown);
        setCooldownScope(config.cooldownScope);
        data.setDefaultEnabled(config.allowByDefault);
        Arrays.stream(config.options)
            .map(CommandConfig.BaseCommandSchema.OptionSchema::getOptionData)
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
        final String origCommand;
        try {
            origCommand = prepareCommand(config.command, event.getOptions());
        } catch (ParsingException | IllegalArgumentException e) {
            throw new Exception("Unable to prepare Minecraft command!", e);
        }

        // Fire an event to allow the command execution to be mutated or cancelled
        final String mcCommand = MinecordCommandEvents.Custom.ALLOW_EXECUTE.invoker().onAllowCustomCommand(
            this, event, server, origCommand
        );
        if (mcCommand == null || mcCommand.isEmpty()) return;

        // Create a temporary command source and hence output, to relay command feedback
        final String tag = event.getUser().getAsTag();
        final String username = event.getMember() != null
            ? event.getMember().getEffectiveName()
            : event.getUser().getName();
        final DiscordCommandOutput output = new DiscordCommandOutput(event, server, mcCommand);
        final ServerCommandSource origSource = new ServerCommandSource(
            output, // command output
            Vec3d.ZERO, Vec2f.ZERO, server.getOverworld(), // location & world
            PERMISSION_LEVEL, tag, Text.literal(username), // permission & display name
            server, null // server & entity
        );

        // Fire an event to allow the command source to be mutated
        final ServerCommandSource source = MinecordCommandEvents.Custom.BEFORE_EXECUTE.invoker().onBeforeCustomCommand(
            this, event, server, mcCommand, origSource
        );

        // Attempt to proxy the Minecraft command
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicInteger result = new AtomicInteger(0);
        @Nullable CommandSyntaxException error = null;
        try {
            LOGGER.info("@{} is running '/{}'", tag, mcCommand);
            server.getCommandManager().getDispatcher().execute(mcCommand, source.withConsumer((c, s, r) -> {
                success.set(s); // if unsuccessful, it may choose to raise a command syntax exception
                result.set(r);
            }));
        } catch (CommandSyntaxException e) {
            error = e;
        } finally {
            LOGGER.info(
                "@{} ran '/{}' with result {} ({})", tag, mcCommand, result.get(), success.get() ? "success" : "fail"
            );
        }

        // Fire an event to broadcast the commands successful/failed execution
        MinecordCommandEvents.Custom.AFTER_EXECUTE.invoker().onCustomCommand(
            this, event, server, mcCommand, success.get(), result.get(), error
        );

        // Finally, if there was still no command feedback sent, let them know with a default message
        // NB: This is to prevent the "The application did not respond" error in Discord, e.g. '/say' or '/tellraw'
        if (output.prevMessage == null) {
            if (error == null) {
                source.sendFeedback(Text.literal(getConfig().messages.feedback), false);
            } else {
                source.sendError(Text.literal(error.getMessage()));
            }
        }
    }

    /**
     * Substitutes a Minecraft command template with options from a Discord command.
     *
     * @param command Minecraft command template using {n} for the nth argument, and {} for all
     * @param options Discord command options to substitute into the template
     * @return a prepared Minecraft command
     * @throws ParsingException if an invalid command option type is encountered
     */
    private static String prepareCommand(@NotNull String command, List<OptionMapping> options) throws ParsingException
    {
        if (command.length() == 0) return command;

        // Prepare a new string template
        final StringTemplate st = new StringTemplate();

        // Add all options to the template
        for (OptionMapping option : options) {
            switch (option.getType()) {
                case BOOLEAN -> st.add(option.getName(), option.getAsBoolean());
                case NUMBER -> st.add(option.getName(), option.getAsDouble());
                case INTEGER -> st.add(option.getName(), option.getAsLong());
                default -> st.add(option.getName(), option.getAsString());
            }
        }

        // Apply the template to the given command
        String result = st.transform(String::trim).format(command);

        // Strip any leading '/' if present, and return
        return result.length() > 0 && result.charAt(0) == '/' ? result.substring(1) : result;
    }

    /**
     * A virtual Minecraft command output for use via Discord.
     */
    private final class DiscordCommandOutput implements CommandOutput
    {
        private final SlashCommandInteractionEvent event;
        private final MinecraftServer server;
        private final String mcCommand;
        public boolean erroneous = false;
        public @Nullable String prevMessage = null;

        /**
         * Constructs a new virtual command output for relaying feedback to Discord.
         *
         * @param event     JDA slash command event
         * @param server    Minecraft server
         * @param mcCommand command to be executed in Minecraft (without leading '/')
         */
        private DiscordCommandOutput(SlashCommandInteractionEvent event, MinecraftServer server, String mcCommand)
        {
            this.event = event;
            this.server = server;
            this.mcCommand = mcCommand;
        }

        @Override
        public void sendMessage(Text message)
        {
            // Build an initial embed for the command feedback
            final String text = prevMessage != null ? prevMessage + '\n' + message.getString() : message.getString();
            EmbedBuilder embed = new EmbedBuilder()
                // Set the colour to green for a success, and red for a failure
                .setColor(!erroneous ? 0x00ff00 : 0xff0000)
                // Set the message
                .setDescription(text);

            // Fire an event to allow the command feedback to be mutated or cancelled
            embed = MinecordCommandEvents.Custom.FEEDBACK.invoker().onCustomCommandFeedback(
                CustomCommand.this, event, server, mcCommand, message, !erroneous, embed
            );

            // Build and reply with the resulting embed
            if (embed != null) {
                if (prevMessage == null) {
                    // This is the first reply, send a new message
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                } else {
                    // There already exists a reply, edit the original message
                    event.getHook().editOriginalEmbeds(embed.build()).queue();
                }
                prevMessage = text;
            }
        }

        @Override
        public boolean shouldReceiveFeedback()
        {
            return true;
        }

        @Override
        public boolean shouldTrackOutput()
        {
            // This method appears to only be called during 'ServerCommandSource#sendError'
            return erroneous = true;
        }

        @Override
        public boolean shouldBroadcastConsoleToOps()
        {
            return server.getGameRules().getBoolean(GameRules.COMMAND_BLOCK_OUTPUT);
        }
    }
}
