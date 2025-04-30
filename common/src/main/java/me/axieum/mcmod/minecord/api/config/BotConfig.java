package me.axieum.mcmod.minecord.api.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import net.dv8tion.jda.api.OnlineStatus;

/**
 * Discord bot configuration options.
 */
@Category(value = "bot")
@ConfigInfo(title = "Bot Config", description = "Discord bot configuration options.")
public final class BotConfig
{
    private BotConfig() {}

    /** Token used to authenticate against your Discord bot. */
    @ConfigEntry(id = "token")
    public static String token = "";

    /** The bot statuses relayed during the lifecycle of the server. */
    @Category(value = "status")
    @ConfigInfo(title = "Bot Statuses", description = "The bot statuses relayed during the lifecycle of the server.")
    public static class Status
    {
        /** Status while the server is starting. */
        @ConfigEntry(id = "starting")
        public static OnlineStatus starting = OnlineStatus.IDLE;

        /** Status after the server has started. */
        @ConfigEntry(id = "started")
        public static OnlineStatus started = OnlineStatus.ONLINE;

        /** Status while the server is stopping. */
        @ConfigEntry(id = "stopping")
        public static OnlineStatus stopping = OnlineStatus.DO_NOT_DISTURB;

        /** Status after the server has stopped. */
        @ConfigEntry(id = "stopped")
        public static OnlineStatus stopped = OnlineStatus.OFFLINE;
    }

    /**
     * True if all guild members should be cached, in turn allowing {@code @mentions}.
     *
     * <p>NB: This requires the Privileged Gateway Intent 'Server Members' to be enabled on your Discord bot!
     */
    @ConfigEntry(id = "cacheMembers")
    public static boolean cacheMembers = false;
}
