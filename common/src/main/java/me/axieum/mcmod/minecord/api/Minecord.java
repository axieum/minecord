package me.axieum.mcmod.minecord.api;

import java.util.Map;
import java.util.Optional;

import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.server.MinecraftServer;

import dev.architectury.event.events.common.LifecycleEvent;

import me.axieum.mcmod.minecord.api.config.BotConfig;
import me.axieum.mcmod.minecord.api.config.MiscConfig;
import me.axieum.mcmod.minecord.api.event.JDAEvents;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * The multi-platform Minecord mod.
 */
public final class Minecord extends ListenerAdapter
{
    private Minecord() {}

    /**
     * The mod identifier.
     */
    public static final String MOD_ID = "minecord";

    /**
     * The mod display name.
     */
    public static final String MOD_NAME = "Minecord";

    /**
     * The mod logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    /**
     * The mod configuration.
     */
    public static final Configurator CONFIG = new Configurator(MOD_ID);

    /**
     * The Minecraft server instance, if starting.
     */
    public static @Nullable MinecraftServer minecraft = null;

    /**
     * The JDA client, if built.
     */
    public static JDA client = null;

    /**
     * Returns the underlying Minecraft server.
     *
     * @return the Minecraft server if present
     */
    public static Optional<MinecraftServer> getMinecraft()
    {
        return Optional.ofNullable(minecraft);
    }

    /**
     * Returns the underlying JDA client.
     *
     * @return the JDA client if built
     */
    public static Optional<JDA> getJDA()
    {
        return Optional.ofNullable(client);
    }

    /**
     * Initialises the multi-platform mod.
     */
    public static void init()
    {
        LOGGER.info("Minecord is getting ready...");

        // Register the configuration
        CONFIG.register(Config.class);
        Config.validate();

        // Register global placeholders
        Placeholders.register();

        // Log into Discord
        try {
            // Prepare the JDA client
            final JDABuilder builder = JDABuilder.createDefault(BotConfig.token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT) // allow the bot to read message contents
                .setStatus(BotConfig.Status.starting) // set initial bot status
                .addEventListeners(new Minecord()); // add event listeners

            // Conditionally enable member caching
            if (BotConfig.cacheMembers) {
                builder.enableIntents(GatewayIntent.GUILD_MEMBERS) // enable required intents
                    .setMemberCachePolicy(MemberCachePolicy.ALL) // cache all members
                    .setChunkingFilter(ChunkingFilter.ALL); // eager-load all members
            }

            JDAEvents.BUILD_CLIENT.invoker().onBuildClient(builder);

            // Build and log into the client
            LOGGER.info("Logging into Discord...");
            client = builder.build();
        } catch (InvalidTokenException | IllegalArgumentException e) {
            LOGGER.error("Unable to login to Discord: {}", e.getMessage());
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event)
    {
        final JDA jda = event.getJDA();
        LOGGER.info("Logged into Discord as @{}", jda.getSelfUser().getName());

        // Cascade initialisation to modules
        MinecordChat.init(jda);
        MinecordCommands.init(jda);
        MinecordPresence.init();

        // Register server lifecycle callbacks
        LifecycleEvent.SERVER_STARTING.register(s -> jda.getPresence().setStatus(BotConfig.Status.starting));
        LifecycleEvent.SERVER_STARTED.register(s -> jda.getPresence().setStatus(BotConfig.Status.started));
        LifecycleEvent.SERVER_STOPPING.register(s -> jda.getPresence().setStatus(BotConfig.Status.stopping));
        LifecycleEvent.SERVER_STOPPED.register(s -> {
            LOGGER.info("Minecord is wrapping up...");
            jda.getPresence().setStatus(BotConfig.Status.stopped);
            jda.shutdown();
        });
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event)
    {
        LOGGER.info("Logged out of Discord!");

        // Cascade teardown to modules
        MinecordPresence.stop();
    }

    /**
     * Builds and returns a URL for retrieving a Minecraft player's avatar.
     *
     * @param uuid   the UUID of the Minecraft player
     * @param height the desired height of the avatar in pixels
     * @return the URL for the Minecraft player's avatar if enabled
     * @see me.axieum.mcmod.minecord.api.config.MiscConfig#enableAvatars
     * @see me.axieum.mcmod.minecord.api.config.MiscConfig#avatarUrl
     */
    public static Optional<String> getAvatarUrl(@Nullable String uuid, int height)
    {
        // Only return an avatar URL if they are enabled and the provided UUID is valid
        if (MiscConfig.enableAvatars && uuid != null && !uuid.isBlank()) {
            return getMinecraft().map(server -> {
                // Format the avatar URL template and return
                return PlaceholdersExt.parseString(
                    MiscConfig.avatarUrlNode,
                    PlaceholderContext.of(server),
                    Map.of(
                        "uuid", string(uuid),
                        "size", string(String.valueOf(height))
                    )
                );
            });
        }
        return Optional.empty();
    }
}
