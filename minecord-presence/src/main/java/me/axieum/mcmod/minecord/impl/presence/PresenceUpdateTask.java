package me.axieum.mcmod.minecord.impl.presence;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Optional;
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
import me.axieum.mcmod.minecord.api.presence.event.PlaceholderCallback;
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
        final PresenceSupplier supplier = category.getPresenceSupplier();
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

            PlaceholderCallback.EVENT.invoker().onPlaceholderPresence(st, category, jda, server);

            /*
             * Build the various presence components.
             */

            final Presence presence = jda.getPresence();

            final Optional<OnlineStatus> status = supplier.getStatus();
            final Optional<Boolean> idle = supplier.isIdle();
            final Optional<Activity> activity = supplier.getActivity(st);

            LOGGER.debug(
                "Updating the Discord bot presence to: [status={}] [idle={}] {}... {} ({})",
                status.orElse(presence.getStatus()),
                idle.orElse(presence.isIdle()),
                activity.orElse(presence.getActivity()).getType().name(),
                activity.orElse(presence.getActivity()).getName(),
                activity.orElse(presence.getActivity()).getUrl()
            );

            /*
             * Update the bot.
             */

            status.ifPresent(presence::setStatus);
            idle.ifPresent(presence::setIdle);
            activity.ifPresent(presence::setActivity);
        } catch (Exception e) {
            LOGGER.error("Unable to update the Discord bot presence!", e);
        }
    }

    @Override
    public boolean cancel()
    {
        jda.getPresence().setActivity(null); // reset the bot activity
        return super.cancel();
    }
}
