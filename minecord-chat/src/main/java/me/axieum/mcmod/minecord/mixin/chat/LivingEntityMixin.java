package me.axieum.mcmod.minecord.mixin.chat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import me.axieum.mcmod.minecord.api.chat.event.minecraft.EntityDeathEvents;

/**
 * Injects into, and broadcasts any animal or monster deaths.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /**
     * Broadcasts any animal or monster deaths.
     *
     * @param source damage source
     * @param info   mixin callback info
     */
    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendEntityStatus("
        + "Lnet/minecraft/entity/Entity;B)V"))
    public void onDeath(DamageSource source, CallbackInfo info)
    {
        EntityDeathEvents.ANIMAL_MONSTER.invoker().onEntityDeath((LivingEntity) (Object) this, source);
    }
}
