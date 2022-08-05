package me.axieum.mcmod.minecord.impl.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.RequiresRestart;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.dv8tion.jda.api.OnlineStatus;

@Config(name = "bot")
public class BotConfig implements ConfigData
{
    @Comment("Token used to authenticate against your Discord bot")
    @RequiresRestart
    public String token = "";

    @Category("Bot Status")
    @Comment("Bot statuses relayed during the lifecycle of the server")
    public StatusSchema status = new StatusSchema();

    /**
     * Bot status configuration schema.
     */
    public static class StatusSchema
    {
        @Comment("Status while the server is starting")
        public OnlineStatus starting = OnlineStatus.IDLE;

        @Comment("Status after the server has started")
        public OnlineStatus started = OnlineStatus.ONLINE;

        @Comment("Status while the server is stopping")
        public OnlineStatus stopping = OnlineStatus.DO_NOT_DISTURB;

        @Comment("Status after the server has stopped")
        public OnlineStatus stopped = OnlineStatus.OFFLINE;
    }

    @Comment("True if all guild members should be cached, in turn allowing @mentions\n"
        + "NB: This requires the Privileged Gateway Intent 'Server Members' to be enabled on your Discord bot!")
    @RequiresRestart
    public boolean cacheMembers = false;
}
