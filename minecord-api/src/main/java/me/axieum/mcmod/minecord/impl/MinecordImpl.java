package me.axieum.mcmod.minecord.impl;

import java.util.Optional;

import javax.security.auth.login.LoginException;

import me.shedaniel.autoconfig.ConfigHolder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.entrypoint.minecraft.hooks.EntrypointUtils;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.addon.MinecordAddon;
import me.axieum.mcmod.minecord.api.event.JDAEvents;
import me.axieum.mcmod.minecord.api.event.ServerShutdownCallback;
import me.axieum.mcmod.minecord.impl.callback.DiscordLifecycleListener;
import me.axieum.mcmod.minecord.impl.callback.ServerLifecycleCallback;
import me.axieum.mcmod.minecord.impl.config.MinecordConfig;

public final class MinecordImpl implements Minecord, PreLaunchEntrypoint, DedicatedServerModInitializer
{
    public static final Minecord INSTANCE = new MinecordImpl();
    public static final Logger LOGGER = LogManager.getLogger("Minecord");
    private static final ConfigHolder<MinecordConfig> CONFIG = MinecordConfig.init();

    private static @Nullable MinecraftServer minecraft = null;
    private static @Nullable JDA client = null;

    @Override
    public void onPreLaunch()
    {
        LOGGER.info("Minecord is getting ready...");

        try {
            // Prepare the JDA client
            final JDABuilder builder = JDABuilder.createDefault(getConfig().token)
                                                 // set initial bot status
                                                 .setStatus(getConfig().status.starting)
                                                 // add event listeners
                                                 .addEventListeners(new DiscordLifecycleListener());

            // Conditionally enable member caching
            if (getConfig().cacheMembers) {
                builder.enableIntents(GatewayIntent.GUILD_MEMBERS) // enable required intents
                       .setMemberCachePolicy(MemberCachePolicy.ALL) // cache all members
                       .setChunkingFilter(ChunkingFilter.ALL); // eager-load all members
            }

            // Register any Minecord addons
            EntrypointUtils.invoke("minecord", MinecordAddon.class, addon -> addon.onInitializeMinecord(builder));

            // Build and login to the client
            JDAEvents.BUILD_CLIENT.invoker().onBuildClient(builder);
            LOGGER.info("Logging into Discord...");
            client = builder.build();
        } catch (LoginException | IllegalArgumentException e) {
            LOGGER.error("Unable to login to Discord: {}", e.getMessage());
        }
    }

    @Override
    public void onInitializeServer()
    {
        // Capture the server instance
        ServerLifecycleEvents.SERVER_STARTING.register(server -> minecraft = server);

        // Register server lifecycle callbacks
        final ServerLifecycleCallback lifecycleCallback = new ServerLifecycleCallback();
        ServerLifecycleEvents.SERVER_STARTING.register(lifecycleCallback);
        ServerLifecycleEvents.SERVER_STARTED.register(lifecycleCallback);
        ServerLifecycleEvents.SERVER_STOPPING.register(lifecycleCallback);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> // register as late as possible
            ServerShutdownCallback.EVENT.register(lifecycleCallback));
    }

    @Override
    public Optional<MinecraftServer> getMinecraft()
    {
        return Optional.ofNullable(minecraft);
    }

    @Override
    public Optional<JDA> getJDA()
    {
        return Optional.ofNullable(client);
    }

    /**
     * Returns the Minecord config instance.
     *
     * @return config instance
     */
    public static MinecordConfig getConfig()
    {
        return CONFIG.getConfig();
    }
}
