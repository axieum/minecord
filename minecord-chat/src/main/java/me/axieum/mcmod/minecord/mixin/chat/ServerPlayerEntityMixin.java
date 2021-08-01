package me.axieum.mcmod.minecord.mixin.chat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.chat.event.minecraft.EntityDeathEvents;

/**
 * Injects into, and broadcasts any player deaths.
 */
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin
{
    /**
     * Broadcasts any player deaths.
     *
     * @param source damage source
     * @param info   mixin callback info
     */
    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendEntityStatus("
        + "Lnet/minecraft/entity/Entity;B)V"))
    public void onDeath(DamageSource source, CallbackInfo info)
    {
        EntityDeathEvents.PLAYER.invoker().onPlayerDeath((ServerPlayerEntity) (Object) this, source);
    }
}
