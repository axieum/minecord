package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import net.minecraft.advancement.Advancement;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.chat.GrantCriterionCallback;

public class PlayerAdvancementCallback implements GrantCriterionCallback
{
    @Override
    public void onGrantCriterion(ServerPlayerEntity player, Advancement advancement, String criterion)
    {
        //
    }
}
