package me.axieum.mcmod.minecord.impl.cmds.command.discord;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;

import me.axieum.mcmod.minecord.api.cmds.command.MinecordCommand;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.cmds.config.CommandConfig;
import static me.axieum.mcmod.minecord.impl.cmds.MinecordCommandsImpl.LOGGER;

/**
 * Custom Minecraft proxy Minecord command.
 */
public class CustomCommand extends MinecordCommand
{
    // The permission level all Minecraft commands should run at
    private static final int PERMISSION_LEVEL = 4;
    // The custom command config instance
    private final CommandConfig.CustomCommand config;

    /**
     * Initialises a new custom command.
     *
     * @param config custom command config
     */
    public CustomCommand(CommandConfig.CustomCommand config)
    {
        super(config.name, config.description);
        this.config = config;
        data.setDefaultEnabled(config.allowByDefault);
        Arrays.stream(config.options).map(CommandConfig.BaseCommand.Option::getOptionData).forEach(data::addOptions);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event, @Nullable MinecraftServer server) throws Exception
    {
        assert server != null;

        // Let them know that their request is in-progress
        event.deferReply().queue();

        // Prepare the Minecraft command
        final String mcCommand;
        try {
            mcCommand = prepareCommand(config.command, event.getOptions());
        } catch (ParsingException | IllegalArgumentException e) {
            throw new Exception("Unable to prepare Minecraft command!", e);
        }

        // todo: Fire an event to allow the command execution to be cancelled

        // Create a temporary command source and hence output, to relay command feedback
        final String username = event.getMember().getEffectiveName();
        final ServerCommandSource source = new ServerCommandSource(
            new DiscordCommandOutput(event, server, mcCommand),
            Vec3d.ZERO, Vec2f.ZERO, server.getOverworld(),
            PERMISSION_LEVEL, username, new LiteralText(username),
            server, null
        );

        // Attempt to proxy the Minecraft command
        try {
            server.getCommandManager().getDispatcher().execute(mcCommand, source.withConsumer((c, s, r) -> {
                // The command was proxied, however the actual feedback was sent to the source's command output
                LOGGER.info("@{} ran '/{}' with result {}", event.getUser().getAsTag(), c.getInput(), r);
            }));
        } catch (CommandSyntaxException e) {
            // The command was indeed proxied, but the result was not a success
            // e.g. trying to whitelist a player who is already whitelisted
            reply(event, server, mcCommand, e.getMessage(), false);
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
        String result = st.format(command);

        // Strip any leading '/' if present, and return
        return result.length() > 0 && result.charAt(0) == '/' ? result.substring(1) : result;
    }

    /**
     * Replies with an embed containing the command execution result.
     *
     * @param event   JDA slash command event to reply to
     * @param server  Minecraft server
     * @param command Minecraft command that was executed
     * @param result  Minecraft command execution feedback
     * @param success true if the command was a success
     */
    public void reply(SlashCommandEvent event, MinecraftServer server, String command, String result, boolean success)
    {
        // Build an initial embed for the result, green for success and red for failure
        // NB: We should still fire an event (with no embed) regardless of whether the command is 'quiet'
        @Nullable EmbedBuilder embed = !config.quiet ? new EmbedBuilder().setColor(success ? 0x00ff00 : 0xff0000)
                                                                         .setDescription(result) : null;

        // todo: Fire an event to allow the command feedback to be mutated or cancelled

        // Build and reply with the resulting embed
        if (embed != null && !embed.isEmpty()) {
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }

    /**
     * A virtual Minecraft command output for use via Discord.
     */
    private final class DiscordCommandOutput implements CommandOutput
    {
        private final SlashCommandEvent event;
        private final MinecraftServer server;
        private final String mcCommand;

        /**
         * Constructs a new virtual command output for relaying feedback to Discord.
         *
         * @param event     JDA slash command event
         * @param server    Minecraft server
         * @param mcCommand command to be executed in Minecraft (without leading '/')
         */
        private DiscordCommandOutput(SlashCommandEvent event, MinecraftServer server, String mcCommand)
        {
            this.event = event;
            this.server = server;
            this.mcCommand = mcCommand;
        }

        @Override
        public void sendSystemMessage(Text message, UUID senderUuid)
        {
            reply(event, server, mcCommand, message.getString(), true);
        }

        @Override
        public boolean shouldReceiveFeedback()
        {
            return !config.quiet;
        }

        @Override
        public boolean shouldTrackOutput()
        {
            return false;
        }

        @Override
        public boolean shouldBroadcastConsoleToOps()
        {
            return server.getGameRules().getBoolean(GameRules.COMMAND_BLOCK_OUTPUT);
        }
    }
}
