package me.axieum.mcmod.minecord.mixin.chat;

import java.util.Collection;
import java.util.Collections;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TellRawCommand;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.chat.event.minecraft.TellRawMessageCallback;

/**
 * Injects into, and broadcasts any '/tellraw' command invocations.
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
    private static void execute(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Integer> cir)
    {
        // If the message was sent to *all* players, then also include Discord in the discussion
        if (targetsAllPlayers(context)) {
            TellRawMessageCallback.EVENT.invoker().onTellRawCommandMessage(
                TextArgumentType.getTextArgument(context, "message"), context.getSource()
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
            target = "Lnet/minecraft/command/argument/EntityArgumentType;getPlayers("
                + "Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Ljava/util/Collection;"
        ),
        remap = false
    )
    private static Collection<ServerPlayerEntity> getPlayers(
        CommandContext<ServerCommandSource> context, String name
    ) throws CommandSyntaxException
    {
        try {
            // Continue with execution as normal
            return EntityArgumentType.getPlayers(context, name);
        } catch (CommandSyntaxException e) {
            // If the command targets all players, but no players are online, return an empty list instead of failing
            if (targetsAllPlayers(context) && e.getType() == EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION) {
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
    private static boolean targetsAllPlayers(CommandContext<ServerCommandSource> context)
    {
        return context.getNodes().size() > 1 && context.getNodes().get(1).getRange().get(context.getInput()).equals(
            "" + EntitySelectorReader.SELECTOR_PREFIX + 'a' // see private `EntitySelectorReader#ALL_PLAYERS` for 'a'
        );
    }
}
