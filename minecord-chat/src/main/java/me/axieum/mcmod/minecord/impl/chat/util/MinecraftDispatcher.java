package me.axieum.mcmod.minecord.impl.chat.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.impl.chat.config.ChatConfig;
import me.axieum.mcmod.minecord.impl.chat.config.ChatConfig.ChatEntrySchema;
import static me.axieum.mcmod.minecord.impl.chat.MinecordChat.getConfig;

/**
 * Utility methods for dispatching configured messages to Minecraft.
 *
 * @see ChatConfig#entries
 * @see ChatEntrySchema.MinecraftSchema
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
        Function<ChatEntrySchema, @Nullable Text> supplier, Predicate<ChatEntrySchema> predicate
    )
    {
        dispatch(supplier, (player, text, entry) -> player.sendMessage(text, false), predicate);
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
        Function<ChatEntrySchema, @Nullable Text> supplier,
        TriConsumer<ServerPlayerEntity, @NotNull Text, ChatEntrySchema> action,
        Predicate<ChatEntrySchema> predicate
    )
    {
        // Fetch the Minecraft server instance, only if there is at least one player logged in
        Minecord.getInstance().getMinecraft().filter(server ->
            server.getPlayerManager() != null && server.getCurrentPlayerCount() > 0
        ).ifPresent(server ->
            // Prepare a stream of configured chat entries
            Arrays.stream(getConfig().entries)
                  .parallel()
                  // Filter message entries
                  .filter(predicate)
                  // Build and send each chat entry
                  .forEach(entry -> {
                      final Text text = supplier.apply(entry);
                      if (text != null) {
                          // Fetch all players
                          Stream<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList().stream();

                          // Conditionally filter players to those in the in-scope dimensions
                          if (entry.dimensions != null && entry.dimensions.length > 0) {
                              final List<String> dims = Arrays.asList(entry.dimensions);
                              players = players.filter(player ->
                                  dims.contains(player.getWorld().getRegistryKey().getValue().toString())
                              );
                          }

                          // Send the message to all relevant players
                          players.forEach(player -> action.accept(player, text, entry));
                      }
                  })
        );
    }
}
