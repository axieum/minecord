package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import me.axieum.mcmod.minecord.api.chat.EntityDeathEvents;

public class EntityDeathCallback implements EntityDeathEvents.Entity
{
    @Override
    public void onEntityDeath(LivingEntity entity, DamageSource source)
    {
        //
    }
}
