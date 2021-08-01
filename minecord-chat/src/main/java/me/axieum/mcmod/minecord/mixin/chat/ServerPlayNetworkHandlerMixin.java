package me.axieum.mcmod.minecord.mixin.chat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.chat.event.ReceiveChatCallback;

/**
 * Injects into, and broadcasts any player chat messages.
 */
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin
{
    @Shadow
    public ServerPlayerEntity player;

    /**
     * Broadcasts any player chat messages.
     *
     * @param message received message contents
     * @param info    mixin callback info
     */
    @Inject(method = "handleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;"
        + "broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;"
        + "Ljava/util/UUID;)V"))
    private void onChatMessage(TextStream.Message message, CallbackInfo info)
    {
        ReceiveChatCallback.EVENT.invoker().onReceiveChat(player, message);
    }
}
