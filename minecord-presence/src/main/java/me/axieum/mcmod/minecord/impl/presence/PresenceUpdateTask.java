package me.axieum.mcmod.minecord.impl.presence;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.TimerTask;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.presence.category.PresenceCategory;
import me.axieum.mcmod.minecord.api.presence.category.PresenceSupplier;
import me.axieum.mcmod.minecord.api.presence.event.PresencePlaceholderCallback;
import me.axieum.mcmod.minecord.api.util.StringTemplate;
import static me.axieum.mcmod.minecord.impl.presence.MinecordPresenceImpl.LOGGER;

/**
 * Timer task that rotates through Minecord presences.
 */
public class PresenceUpdateTask extends TimerTask
{
    // The JDA client
    private final JDA jda;
    // The Minecord presence category
    private final PresenceCategory category;

    /**
     * Initialises a new Minecord bot presence update task.
     *
     * @param jda      JDA client
     * @param category Minecord presence category
     */
    public PresenceUpdateTask(@NotNull JDA jda, @NotNull PresenceCategory category)
    {
        this.jda = jda;
        this.category = category;
    }

    @Override
    public void run()
    {
        try {
            final PresenceSupplier supplier = category.getPresenceSupplier();

            /*
             * Prepare a message template.
             */

            final StringTemplate st = new StringTemplate();

            // The total process uptime (to the nearest minute)
            st.add("uptime", Duration.ofMillis(
                ManagementFactory.getRuntimeMXBean().getUptime()
            ).truncatedTo(ChronoUnit.MINUTES));

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
                // The server player counts, if loaded
                if (server.getPlayerManager() != null) {
                    // The server max player count
                    st.add("max_players", server.getMaxPlayerCount());
                    // The server current player count
                    st.add("player_count", server.getCurrentPlayerCount());
                }
            }

            PresencePlaceholderCallback.EVENT.invoker().onPresencePlaceholder(st, category, jda, server);

            /*
             * Build the various presence components.
             */

            final Presence current = jda.getPresence();

            final Boolean idle = supplier.isIdle().orElse(current.isIdle());
            final OnlineStatus status = supplier.getStatus().orElse(current.getStatus());
            final Activity activity = supplier.getActivity(st).orElse(current.getActivity());

            /*
             * Update the bot.
             */

            // Check that the presence is actually changing
            if (
                idle != current.isIdle()
                    || status != current.getStatus()
                    || activity.getType() != current.getActivity().getType()
                    || !Objects.equals(activity.getName(), current.getActivity().getName())
                    || !Objects.equals(activity.getUrl(), current.getActivity().getUrl())
            ) {
                LOGGER.debug(
                    "Updating the Discord bot presence to: [idle={}] [status={}] {}... {} ({})",
                    idle, status, activity.getType().name(), activity.getName(), activity.getUrl()
                );
                current.setPresence(status, activity, idle);
            } else {
                LOGGER.debug("Skipping Discord bot presence update as no change was detected!");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to update the Discord bot presence!", e);
        }
    }
}
