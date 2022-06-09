package me.axieum.mcmod.minecord.api.cmds.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * To whom a cooldown applies.
 *
 * <p>Influenced by: <a href="https://github.com/JDA-Applications/JDA-Utilities">JDA-Applications/JDA-Utilities</a>.
 */
public enum CooldownScope
{
    /**
     * Applies the cooldown to the calling {@link net.dv8tion.jda.api.entities.User User}.
     */
    USER("U:%1$d"),

    /**
     * Applies the cooldown to the {@link net.dv8tion.jda.api.entities.Channel Channel} the command is called in.
     */
    CHANNEL("C:%2$d"),

    /**
     * Applies the cooldown to the calling {@link net.dv8tion.jda.api.entities.User User} local to the
     * {@link net.dv8tion.jda.api.entities.Channel Channel} the command is called in.
     */
    USER_CHANNEL("U:%1$d|C:%2$d"),

    /**
     * Applies the cooldown to the {@link net.dv8tion.jda.api.entities.Guild Guild} the command is called in.
     *
     * <p>This will automatically fall back to {@link CooldownScope#CHANNEL} when called in a private channel.
     */
    GUILD("G:%3$d"),

    /**
     * Applies the cooldown to the calling {@link net.dv8tion.jda.api.entities.User User} local to the
     * {@link net.dv8tion.jda.api.entities.Guild Guild} the command is called in.
     *
     * <p>This will automatically fall back to {@link CooldownScope#CHANNEL} when called in a private channel.
     */
    USER_GUILD("U:%1$d|G:%3$d"),

    /**
     * Applies the cooldown to the {@link net.dv8tion.jda.api.JDA.ShardInfo Shard} the command is called on.
     *
     * <p>This will automatically fall back to {@link CooldownScope#GLOBAL} when
     * {@link net.dv8tion.jda.api.JDA#getShardInfo() JDA#getShardInfo()} returns {@code null}.
     */
    SHARD("S:%4$d"),

    /**
     * Applies the cooldown to the calling {@link net.dv8tion.jda.api.entities.User User} local to the
     * {@link net.dv8tion.jda.api.JDA.ShardInfo Shard} the command is called on.
     *
     * <p>This will automatically fall back to {@link CooldownScope#USER} when
     * {@link net.dv8tion.jda.api.JDA#getShardInfo() JDA#getShardInfo()} returns {@code null}.
     */
    USER_SHARD("U:%1$d|S:%4$d"),

    /**
     * Applies the cooldown globally, affecting all users and channels of the
     * {@link net.dv8tion.jda.api.JDA JDA} instance.
     */
    GLOBAL("global");

    // The cache key format used to generate and lookup cooldown entries
    private final String format;

    /**
     * Constructs a new JDA cooldown scope.
     *
     * @param format the cache key format used to generate and lookup cooldown entries
     */
    CooldownScope(final String format)
    {
        this.format = format;
    }

    /**
     * Generates a cooldown key from a given JDA slash command event.
     *
     * @param event JDA slash command event
     * @return formatted cooldown key prefixed with the command name
     */
    public @NotNull String getKey(@NotNull SlashCommandInteractionEvent event)
    {
        // Guild-less scope fallback
        if (!event.isFromGuild() && (this == GUILD || this == USER_GUILD)) {
            return CHANNEL.getKey(event);
        }

        // Shard-less scope fallback
        if (event.getJDA().getShardInfo() == null) {
            if (this == SHARD) return GLOBAL.getKey(event);
            else if (this == USER_SHARD) return USER.getKey(event);
        }

        // Build, prefix, and return the cooldown key
        return event.getName() + "|" + String.format(
            format,
            event.getUser() != null ? event.getUser().getIdLong() : -1L,
            event.getChannel() != null ? event.getChannel().getIdLong() : -1L,
            event.getGuild() != null ? event.getGuild().getIdLong() : -1L,
            event.getJDA().getShardInfo() != null ? event.getJDA().getShardInfo().getShardId() : -1
        );
    }
}
