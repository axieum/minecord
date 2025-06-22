package me.axieum.mcmod.minecord.mixin;

import java.util.Collection;
import java.util.Collections;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.server.commands.TellRawCommand;
import net.minecraft.server.level.ServerPlayer;

import me.axieum.mcmod.minecord.api.event.MinecraftEvents;

/**
 * Injects into and broadcasts any '/tellraw' command invocations.
 */
@Mixin(TellRawCommand.class)
public abstract class TellRawCommandMixin
{
    /**
     * Broadcasts any {@code /tellraw @a} command invocations that target
     * all players.
     *
     * @param context command context
     * @param cir     mixin callback info
     */
    @Inject(method = "method_13777", at = @At(value = "TAIL"), remap = false)
    private static void execute(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir)
    {
        // If the message was sent to *all* players, then also include Discord in the discussion
        if (minecord$targetsAllPlayers(context)) {
            MinecraftEvents.TELL_RAW.invoker().tellRaw(
                ComponentArgument.getRawComponent(context, "message"), context.getSource()
            );
        }
    }

    /**
     * Intercepts the player argument resolution during the {@code /tellraw}
     * command to prevent the "No player was found" error when no players are
     * online. This allows the message to still propagate through to Discord.
     *
     * @param context command context
     * @param name    player argument name, i.e. {@code targets}
     * @return collection of target players to send the message to
     */
    @Redirect(
        method = "method_13777",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/arguments/EntityArgument;getPlayers("
                + "Lcom/mojang/brigadier/context/CommandContext;"
                + "Ljava/lang/String;"
                + ")Ljava/util/Collection;"
        )
    )
    private static Collection<ServerPlayer> getPlayers(CommandContext<CommandSourceStack> context, String name)
        throws CommandSyntaxException
    {
        try {
            // Continue with execution as normal
            return EntityArgument.getPlayers(context, name);
        } catch (CommandSyntaxException e) {
            // If the command targets all players, but no players are online, return an empty list instead of failing
            if (minecord$targetsAllPlayers(context) && e.getType() == EntityArgument.NO_PLAYERS_FOUND) {
                return Collections.emptyList();
            }
            // Else, continue by throwing the error as normal
            throw e;
        }
    }

    /**
     * Returns true if the {@code /tellraw} command targets all players.
     *
     * @param context command context
     * @return true if the {@code /tellraw @a} command was executed
     */
    @Unique
    @SuppressWarnings({"checkstyle:illegalidentifiername", "checkstyle:methodname"})
    private static boolean minecord$targetsAllPlayers(CommandContext<CommandSourceStack> context)
    {
        return context.getNodes().size() > 1 && context.getNodes().get(1).getRange().get(context.getInput()).equals(
            "" + EntitySelectorParser.SYNTAX_SELECTOR_START + 'a'
        );
    }
}
