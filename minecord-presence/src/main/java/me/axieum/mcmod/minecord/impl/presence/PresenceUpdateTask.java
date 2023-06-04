package me.axieum.mcmod.minecord.impl.presence;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimerTask;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.presence.category.PresenceCategory;
import me.axieum.mcmod.minecord.api.presence.category.PresenceSupplier;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.duration;
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
     * Constructs a new Minecord bot presence update task.
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
             * Prepare the message placeholders.
             */

            final Optional<PlaceholderContext> ctx = Minecord.getInstance().getMinecraft().map(PlaceholderContext::of);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The total process uptime (to the nearest minute)
                "uptime", duration(() ->
                    Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()).truncatedTo(ChronoUnit.MINUTES)
                )
            );

            /*
             * Build the various presence components.
             */

            final Presence current = jda.getPresence();
            final @Nullable Activity currentActivity = current.getActivity();

            final Boolean idle = supplier.isIdle().orElse(current.isIdle());
            final @Nullable OnlineStatus status = supplier.getStatus().orElse(current.getStatus());
            final @Nullable Activity activity = supplier.getActivity(
                nameNode -> PlaceholdersExt.parseString(nameNode, ctx.orElse(null), placeholders)
            ).orElse(currentActivity);

            /*
             * Update the bot.
             */

            // Check that the presence is actually changing
            if (
                idle != current.isIdle()
                    || status != current.getStatus()
                    || activity != null && currentActivity != null
                    && (
                        activity.getType() != currentActivity.getType()
                        || !Objects.equals(activity.getName(), currentActivity.getName())
                        || !Objects.equals(activity.getUrl(), currentActivity.getUrl())
                    )
            ) {
                if (activity != null) {
                    LOGGER.debug(
                        "Updating the Discord bot presence to: [idle={}] [status={}] {}... {} ({})",
                        idle, status, activity.getType().name(), activity.getName(), activity.getUrl()
                    );
                } else {
                    LOGGER.debug("Updating the Discord bot presence to: [idle={}] [status={}]", idle, status);
                }
                current.setPresence(status, activity, idle);
            } else {
                LOGGER.debug("Skipping Discord bot presence update as no change was detected!");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to update the Discord bot presence!", e);
        }
    }
}
