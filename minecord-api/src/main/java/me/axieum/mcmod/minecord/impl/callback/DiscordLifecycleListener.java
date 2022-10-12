package me.axieum.mcmod.minecord.impl.callback;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static me.axieum.mcmod.minecord.impl.MinecordImpl.LOGGER;

/**
 * A listener for Discord lifecycle events.
 */
public class DiscordLifecycleListener extends ListenerAdapter
{
    @Override
    public void onReady(@NotNull ReadyEvent event)
    {
        LOGGER.info("Logged into Discord as @{}", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event)
    {
        LOGGER.info("Logged out of Discord!");
    }
}
