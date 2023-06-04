package me.axieum.mcmod.minecord.mixin.chat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Minecraft living entity accessor mixin.
 */
@Mixin(LivingEntity.class)
public interface LivingEntityAccessor
{
    /**
     * Retrieves the private last block position of the entity.
     *
     * @return last block position
     */
    @Accessor(value = "lastBlockPos")
    BlockPos getLastBlockPos();
}
