package me.axieum.mcmod.minecord.impl.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.RequiresRestart;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.dv8tion.jda.api.OnlineStatus;

/**
 * Discord bot configuration schema.
 */
@Config(name = "bot")
public class BotConfig implements ConfigData
{
    /** Token used to authenticate against your Discord bot. */
    @Comment("Token used to authenticate against your Discord bot")
    @RequiresRestart
    public String token = "";

    /** Bot statuses relayed during the lifecycle of the server. */
    @Category("Bot Status")
    @Comment("Bot statuses relayed during the lifecycle of the server")
    public StatusSchema status = new StatusSchema();

    /**
     * Bot status configuration schema.
     */
    public static class StatusSchema
    {
        /** Status while the server is starting. */
        @Comment("Status while the server is starting")
        public OnlineStatus starting = OnlineStatus.IDLE;

        /** Status after the server has started. */
        @Comment("Status after the server has started")
        public OnlineStatus started = OnlineStatus.ONLINE;

        /** Status while the server is stopping. */
        @Comment("Status while the server is stopping")
        public OnlineStatus stopping = OnlineStatus.DO_NOT_DISTURB;

        /** Status after the server has stopped. */
        @Comment("Status after the server has stopped")
        public OnlineStatus stopped = OnlineStatus.OFFLINE;
    }

    /**
     * True if all guild members should be cached, in turn allowing {@code @mentions}.
     *
     * <p>NB: This requires the Privileged Gateway Intent 'Server Members' to be enabled on your Discord bot!
     */
    @Comment("True if all guild members should be cached, in turn allowing @mentions\n"
        + "NB: This requires the Privileged Gateway Intent 'Server Members' to be enabled on your Discord bot!")
    @RequiresRestart
    public boolean cacheMembers = false;
}
