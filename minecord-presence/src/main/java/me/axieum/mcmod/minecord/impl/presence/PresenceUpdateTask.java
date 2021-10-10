package me.axieum.mcmod.minecord.impl.presence;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.TimerTask;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.presence.MinecordPresence;
import me.axieum.mcmod.minecord.api.presence.PresenceSupplier;
import me.axieum.mcmod.minecord.api.presence.event.PlaceholderCallback;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import static me.axieum.mcmod.minecord.impl.presence.MinecordPresenceImpl.LOGGER;

/**
 * Timer task that rotates through Minecord presences.
 */
public class PresenceUpdateTask extends TimerTask
{
    // An instance of random to aid in choosing presences
    private static final Random RANDOM = new Random();

    // The Minecord Presence instance
    private final MinecordPresence instance = MinecordPresence.getInstance();
    // The JDA client
    private final JDA jda;
    // True if presences are chosen randomly, else round-robin
    private final boolean random;
    // The index of the last presence entry used
    private int last = -1;

    /**
     * Initialises a new Minecord bot presence update task.
     *
     * @param jda    JDA client
     * @param random true if presences are chosen randomly, else round-robin
     */
    public PresenceUpdateTask(@NotNull JDA jda, boolean random)
    {
        this.jda = jda;
        this.random = random;
    }

    @Override
    public void run()
    {
        // Fetch a list presences for the current stage
        final String stage = instance.getStage();
        final List<PresenceSupplier> suppliers = instance.getPresences();
        if (suppliers == null || suppliers.size() == 0) return;

        // Choose a presence index to use
        if (random) {
            last = RANDOM.nextInt(suppliers.size());
        } else {
            last = (last + 1) % suppliers.size();
        }

        // Attempt to build and update the bot presence
        final PresenceSupplier supplier = suppliers.get(last);
        try {
            /*
             * Prepare a message template.
             */

            final StringTemplate st = new StringTemplate();

            // The total process uptime
            st.add("uptime", Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()));

            // The Minecraft server, if present
            final @Nullable MinecraftServer server = Minecord.getInstance().getMinecraft().orElse(null);
            if (server != null) {
                // The server version
                st.add("version", server.getVersion());
                // The server IP address
                st.add("ip", server.getServerIp());
                // The server port
                st.add("port", server.getServerPort());
                // The server message-of-the-day (MOTD)
                st.add("motd", server.getServerMotd());
                // The world difficulty
                st.add("difficulty", server.getSaveProperties().getDifficulty().getName());
                // The server max player count
                st.add("max_players", server.getMaxPlayerCount());
                // The server current player count
                st.add("player_count", server.getCurrentPlayerCount());
            }

            PlaceholderCallback.EVENT.invoker().onPlaceholderPresence(st, stage, jda, server);

            /*
             * Build the various presence components.
             */

            final Optional<OnlineStatus> status = supplier.getStatus();
            final Optional<Boolean> idle = supplier.isIdle();
            final Activity activity = supplier.getActivity(st);

            LOGGER.debug(
                "Updating the Discord bot presence in stage '{}' to entry {}: [{}] {}... {} ({})",
                stage, last + 1, status.orElse(jda.getPresence().getStatus()), activity.getType().name(),
                activity.getName(), activity.getUrl()
            );

            /*
             * Update the bot.
             */

            // Depending on what is changing, make one update call instead of multiple
            if (status.isPresent() && idle.isPresent()) {
                // Set all status, activity and idle
                jda.getPresence().setPresence(status.get(), activity, idle.get());
            } else if (status.isPresent()) {
                // Set status and activity only, leaving idle untouched
                jda.getPresence().setPresence(status.get(), activity);
            } else if (idle.isPresent()) {
                // Set idle and activity only, leaving status untouched
                jda.getPresence().setPresence(activity, idle.get());
            } else {
                // Set the activity only, leaving both status and idle untouched
                jda.getPresence().setActivity(activity);
            }
        } catch (Exception e) {
            // The configured presence entry is invalid!
            LOGGER.error("Could not set the Discord bot presence in stage '{}' to entry {}!", stage, last + 1, e);
        }
    }

    @Override
    public boolean cancel()
    {
        jda.getPresence().setActivity(null); // reset the bot activity
        return super.cancel();
    }
}
