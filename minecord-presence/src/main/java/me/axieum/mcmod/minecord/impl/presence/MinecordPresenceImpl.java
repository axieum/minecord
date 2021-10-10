package me.axieum.mcmod.minecord.impl.presence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Timer;

import me.shedaniel.autoconfig.ConfigHolder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.addon.MinecordAddon;
import me.axieum.mcmod.minecord.api.presence.MinecordPresence;
import me.axieum.mcmod.minecord.api.presence.PresenceSupplier;
import me.axieum.mcmod.minecord.impl.presence.config.PresenceConfig;

public final class MinecordPresenceImpl implements MinecordPresence, MinecordAddon
{
    public static final MinecordPresence INSTANCE = new MinecordPresenceImpl();
    public static final Logger LOGGER = LogManager.getLogger("Minecord|Presence");
    private static final ConfigHolder<PresenceConfig> CONFIG = PresenceConfig.init();

    // A mapping of stage (or category) names to a list of presences
    private static final HashMap<String, List<PresenceSupplier>> PRESENCES = new HashMap<>();
    // The current stage (or category) to choose presences from
    private static @NotNull String stage = "starting";
    // The current timer that is responsible for scheduling presence updates
    private static @Nullable Timer timer = null;
    // The *current* stage (or category) presence update interval
    private static long prevInterval = 60;
    // True if the *current* stage (or category) chooses random presences, else round-robin
    private static boolean prevRandom = false;

    @Override
    public void onInitializeMinecord(JDABuilder builder)
    {
        LOGGER.info("Minecord Presence is getting ready...");

        // Update the current presence stage (or category) throughout the lifecycle of the Minecraft server
        ServerLifecycleEvents.SERVER_STARTING.register(
            s -> setStage("starting", getConfig().starting.interval, getConfig().starting.random));
        ServerLifecycleEvents.SERVER_STARTED.register(
            s -> setStage("running", getConfig().running.interval, getConfig().running.random));
        ServerLifecycleEvents.SERVER_STOPPING.register(
            s -> setStage("stopping", getConfig().stopping.interval, getConfig().stopping.random));

        // Start and stop the presence update task with the Discord client
        builder.addEventListeners(
            new ListenerAdapter()
            {
                @Override
                public void onReady(ReadyEvent event)
                {
                    // If Minecraft has already started, start the Discord bot presence task
                    if (Minecord.getInstance().getMinecraft().isPresent()) {
                        start(event.getJDA(), prevInterval, prevRandom);
                    }
                }

                @Override
                public void onShutdown(ShutdownEvent event)
                {
                    // Stop the Discord bot presence task
                    stop();
                }
            }
        );

        // Register all Minecord provided presences
        initPresences(getConfig());
    }

    /**
     * Initialises and registers all Minecord provided presences.
     *
     * @param config presence config
     */
    public static void initPresences(PresenceConfig config)
    {
        final MinecordPresence presence = MinecordPresence.getInstance();

        // A stage (or category) of presences shown while the Minecraft server is starting
        presence.clearStage("starting");
        presence.addPresences(
            "starting",
            Arrays.stream(config.starting.presences)
                  .map(PresenceConfig.Stage.PresenceEntry::getPresenceSupplier)
                  .toArray(PresenceSupplier[]::new)
        );

        // A stage (or category) of presences shown while the Minecraft server is running
        presence.clearStage("running");
        presence.addPresences(
            "running",
            Arrays.stream(config.running.presences)
                  .map(PresenceConfig.Stage.PresenceEntry::getPresenceSupplier)
                  .toArray(PresenceSupplier[]::new)
        );

        // A stage (or category) of presences shown while the Minecraft server is stopping
        presence.clearStage("stopping");
        presence.addPresences(
            "stopping",
            Arrays.stream(config.stopping.presences)
                  .map(PresenceConfig.Stage.PresenceEntry::getPresenceSupplier)
                  .toArray(PresenceSupplier[]::new)
        );
    }

    @Override
    public void start(@NotNull JDA jda, long interval, boolean random)
    {
        // Stop the timer if it already exists
        if (timer != null) stop();

        // Prepare a new timer that we can schedule tasks on
        timer = new Timer("Minecord-Presence-Timer", true);

        // Check that the interval is within reasonable bounds
        if (interval < 15) {
            LOGGER.warn(
                "A Discord presence update interval shorter than 15 seconds will lead to rate-limits! Reverting to 15s."
            );
            interval = 15;
        }

        // Schedule the presence update task
        LOGGER.info("Scheduling Discord bot presence updates in stage '{}' for every {} second(s)", stage, interval);
        timer.schedule(new PresenceUpdateTask(jda, random), 0, interval * 1000L);

        // Store the last used interval and randomness values
        prevInterval = interval;
        prevRandom = random;
    }

    @Override
    public void stop()
    {
        // Bail if the timer doesn't exist yet
        if (timer == null) return;

        // Cancel the existing timer and remove any reference to it
        LOGGER.debug("Unscheduling Discord bot presence updates");
        timer.cancel();
        timer = null;
    }

    @Override
    public @NotNull String getStage()
    {
        return stage;
    }

    @Override
    public List<String> getStages()
    {
        return PRESENCES.keySet().stream().toList();
    }

    @Override
    public MinecordPresence setStage(@NotNull String name, long interval, boolean random)
    {
        // Assert that the stage is really changing
        if (!stage.equals(name)) {
            // Set the current stage
            stage = name;
            prevInterval = interval;
            prevRandom = random;
            // Restart the presence update task
            Minecord.getInstance().getJDA().ifPresent(jda -> start(jda, interval, random));
        }
        return this;
    }

    @Override
    public MinecordPresence setStageTemporarily(@NotNull String name, long interval, boolean random, Runnable runnable)
    {
        final String oldName = stage;
        final long oldInterval = prevInterval;
        final boolean oldRandom = prevRandom;
        try {
            // Set the new stage
            setStage(name, interval, random);
            // Execute the given runnable
            runnable.run();
        } finally {
            // Revert the stage back
            setStage(oldName, oldInterval, oldRandom);
        }
        return this;
    }

    @Override
    public long getInterval()
    {
        return prevInterval;
    }

    @Override
    public boolean isRandom()
    {
        return prevRandom;
    }

    @Override
    public @Nullable List<PresenceSupplier> clearStage(@NotNull String name)
    {
        // Stop the presence update task if clearing the current stage
        if (stage.equals(name)) stop();
        // Remove the stage
        return PRESENCES.remove(name);
    }

    @Override
    public @Nullable List<PresenceSupplier> getPresences()
    {
        return PRESENCES.get(stage);
    }

    @Override
    public @Nullable List<PresenceSupplier> getPresences(@NotNull String name)
    {
        return PRESENCES.get(name);
    }

    @Override
    public MinecordPresence addPresences(@NotNull String name, @NotNull PresenceSupplier... presences)
    {
        Optional.ofNullable(PRESENCES.get(name)).ifPresentOrElse(
            // Append the presences to an existing stage
            existing -> existing.addAll(List.of(presences)),
            // Otherwise, create a new stage with the presences
            () -> PRESENCES.put(name, new ArrayList<>(List.of(presences)))
        );
        return this;
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
}
