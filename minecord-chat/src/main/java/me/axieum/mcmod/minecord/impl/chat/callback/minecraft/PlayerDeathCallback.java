package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class PlayerDeathCallback implements ServerPlayerEvents.CopyFrom
{
    @Override
    public void copyFromPlayer(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive)
    {
        // We only care if the player had died
        if (alive) return;
    }
}
