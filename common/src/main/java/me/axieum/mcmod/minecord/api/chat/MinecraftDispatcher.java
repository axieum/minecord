package me.axieum.mcmod.minecord.api.chat;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.config.ChatConfig;
import me.axieum.mcmod.minecord.api.config.ChatConfig.ChatEntry;

/**
 * Utility methods for dispatching configured messages to Minecraft.
 *
 * @see ChatConfig#entries
 * @see ChatEntry.Minecraft
 */
public final class MinecraftDispatcher
{
    private MinecraftDispatcher() {}

    /**
     * Builds and sends messages for each configured chat entry.
     *
     * @param supplier  supplier that provides the Minecraft text component to be sent for a chat entry
     * @param predicate predicate that filters configured chat entries
     * @see #dispatch(Function, TriConsumer, Predicate)
     */
    public static void dispatch(
        Function<ChatEntry, @Nullable Component> supplier, Predicate<ChatEntry> predicate
    )
    {
        dispatch(supplier, (player, text, entry) -> player.sendSystemMessage(text, false), predicate);
    }

    /**
     * Builds and acts on messages for each configured chat entry.
     *
     * @param supplier  supplier that provides the Minecraft text component to be sent for a chat entry
     * @param action    consumer to act upon the resulting Minecraft text component for each player
     * @param predicate predicate that filters configured chat entries
     * @see ChatConfig#entries
     */
    public static void dispatch(
        Function<ChatEntry, @Nullable Component> supplier,
        TriConsumer<ServerPlayer, @NotNull Component, ChatEntry> action,
        Predicate<ChatEntry> predicate
    )
    {
        // Fetch the Minecraft server instance only if there is at least one player logged in
        Minecord.getMinecraft().filter(server ->
            server.getPlayerCount() > 0
        ).ifPresent(server ->
            // Prepare a stream of configured chat entries
            Arrays.stream(ChatConfig.entries)
                  .parallel()
                  // Filter message entries
                  .filter(predicate)
                  // Build and send each chat entry
                  .forEach(entry -> {
                      final Component text = supplier.apply(entry);
                      if (text != null) {
                          // Fetch all players
                          Stream<ServerPlayer> players = server.getPlayerList().getPlayers().stream();

                          // Conditionally filter players to those in the in-scope dimensions
                          if (entry.dimensions != null && entry.dimensions.length > 0) {
                              final List<String> dims = Arrays.asList(entry.dimensions);
                              players = players.filter(player ->
                                  dims.contains(player.level().dimension().registry().toString())
                              );
                          }

                          // Send the message to all relevant players
                          players.forEach(player -> action.accept(player, text, entry));
                      }
                  })
        );
    }
}
