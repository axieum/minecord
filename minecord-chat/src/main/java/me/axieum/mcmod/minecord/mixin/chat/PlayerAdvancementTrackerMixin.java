package me.axieum.mcmod.minecord.mixin.chat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.chat.event.minecraft.GrantCriterionCallback;

/**
 * Injects into, and broadcasts any granted advancement criterion.
 */
@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin
{
    @Shadow
    private ServerPlayerEntity owner;

    /**
     * Broadcasts any granted advancement criterion.
     *
     * @param advancement parent advancement
     * @param criterion   name of the criterion granted
     * @param info        mixin callback info
     */
    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/"
        + "AdvancementRewards;apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void grantCriterion(Advancement advancement, String criterion, CallbackInfoReturnable<Boolean> info)
    {
        GrantCriterionCallback.EVENT.invoker().onGrantCriterion(owner, advancement, criterion);
    }
}
