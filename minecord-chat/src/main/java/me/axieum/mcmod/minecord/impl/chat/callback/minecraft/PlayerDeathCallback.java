package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.chat.EntityDeathEvents;

public class PlayerDeathCallback implements EntityDeathEvents.Player
{
    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source)
    {
        //
    }
}
