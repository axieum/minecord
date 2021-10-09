package me.axieum.mcmod.minecord.impl.presence;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import me.shedaniel.autoconfig.ConfigHolder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import net.minecraft.server.MinecraftServer;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.addon.MinecordAddon;
import me.axieum.mcmod.minecord.api.presence.MinecordPresence;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import me.axieum.mcmod.minecord.impl.presence.config.PresenceConfig;

public final class MinecordPresenceImpl extends ListenerAdapter implements MinecordPresence, MinecordAddon
{
    public static final MinecordPresence INSTANCE = new MinecordPresenceImpl();
    public static final Logger LOGGER = LogManager.getLogger("Minecord|Presence");
    private static final ConfigHolder<PresenceConfig> CONFIG = PresenceConfig.init();

    // An instance of random to aid in choosing presences
    private static final Random RANDOM = new Random();
    // Timer that is responsible for scheduling presence updates
    private static Timer timer;

    @Override
    public void onInitializeMinecord(JDABuilder builder)
    {
        LOGGER.info("Minecord Presence is getting ready...");
        builder.addEventListeners(this);
    }

    @Override
    public void onReady(ReadyEvent event)
    {
        start(event.getJDA());
    }

    @Override
    public void onShutdown(ShutdownEvent event)
    {
        stop();
    }

    @Override
    public void start(@NotNull JDA jda)
    {
        // Skip presence scheduling if there are none to choose from
        if (getConfig().running.length == 0) return;

        // Bail if the timer already exists
        if (timer != null) return;

        // Prepare a new timer that we can schedule tasks on
        timer = new Timer("Minecord-Presence-Timer", true);

        // Check how long the update interval should be, and apply reasonable bounds
        final long interval = Math.max(getConfig().interval, 15);

        // Schedule the presence update task
        LOGGER.info("Scheduling Discord bot presence updates for every {} second(s)", interval);
        timer.schedule(new PresenceUpdateTask(jda), 0, interval * 1000L);
    }

    @Override
    public void stop()
    {
        if (timer == null) return;
        timer.cancel();
        timer = null;
    }

    /**
     * Returns the Minecord Presence config instance.
     *
     * @return config instance
     */
    public static PresenceConfig getConfig()
    {
        return CONFIG.getConfig();
    }

    /**
     * Timer task that rotates through Discord bot presences.
     */
    private static final class PresenceUpdateTask extends TimerTask
    {
        private final @NotNull JDA jda;
        private int next = 0;

        /**
         * Initialises a new Discord bot presence update task.
         *
         * @param jda JDA client
         */
        private PresenceUpdateTask(@NotNull JDA jda)
        {
            this.jda = jda;
        }

        @Override
        public void run()
        {
            try {
                // Fetch the Minecraft server instance, if present
                final Optional<MinecraftServer> server = Minecord.getInstance().getMinecraft();

                // Prepare a string template
                final StringTemplate st = new StringTemplate()
                    .add("version", server.map(MinecraftServer::getVersion).orElse(null))
                    .add("ip", server.map(MinecraftServer::getServerIp).orElse(null))
                    .add("port", server.map(MinecraftServer::getServerPort).orElse(null))
                    .add("motd", server.map(MinecraftServer::getServerMotd).orElse(null))
                    .add("difficulty", server.map(s -> s.getSaveProperties().getDifficulty().getName()).orElse(null))
                    .add("max_players", server.map(MinecraftServer::getMaxPlayerCount).orElse(null))
                    .add("player_count", server.map(MinecraftServer::getCurrentPlayerCount).orElse(null))
                    .add("uptime", Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()));

                // Attempt to create a new activity from the configured presence entry and set it
                final PresenceConfig.PresenceEntry cfg = getConfig().running[next];
                jda.getPresence().setActivity(Activity.of(cfg.type, st.format(cfg.value), cfg.url));
            } catch (Exception e) {
                // The configured presence entry is invalid!
                LOGGER.error("Unable to update the Discord bot presence to entry #{}!", next, e);
            } finally {
                // Choose the next presence index
                if (getConfig().random) {
                    next = RANDOM.nextInt(getConfig().running.length);
                } else {
                    next = (next + 1) % getConfig().running.length;
                }
            }
        }

        @Override
        public boolean cancel()
        {
            jda.getPresence().setActivity(null); // reset the bot activity
            return super.cancel();
        }
    }
}
