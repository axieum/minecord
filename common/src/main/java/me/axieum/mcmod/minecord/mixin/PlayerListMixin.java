package me.axieum.mcmod.minecord.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.players.PlayerList;

import me.axieum.mcmod.minecord.api.event.MinecraftEvents;

/**
 * Injects into and captures any server crash reports.
 */
@Mixin(PlayerList.class)
public abstract class PlayerListMixin
{
    /**
     * Broadcasts any {@code /me} or {@code /say} command invocations.
     *
     * @param message broadcast message with message decorators applied
     * @param source  command source that sent the message
     * @param type    chat type
     * @param ci      mixin callback info
     */
    @Inject(
        method = "broadcastChatMessage("
            + "Lnet/minecraft/network/chat/PlayerChatMessage;"
            + "Lnet/minecraft/commands/CommandSourceStack;"
            + "Lnet/minecraft/network/chat/ChatType$Bound;)V",
        at = @At("HEAD")
    )
    private void onBroadcastCommandMessage(
        PlayerChatMessage message, CommandSourceStack source, ChatType.Bound type, CallbackInfo ci
    )
    {
        final String chatType = type.chatType().value().chat().translationKey();
        if ("chat.type.emote".equals(chatType)) {
            // '/me <action>'
            MinecraftEvents.EMOTE.invoker().emote(message.decoratedContent(), source);
        } else if ("chat.type.announcement".equals(chatType)) {
            // '/say <message>'
            MinecraftEvents.SAY.invoker().say(message.decoratedContent(), source);
        }
    }
}
