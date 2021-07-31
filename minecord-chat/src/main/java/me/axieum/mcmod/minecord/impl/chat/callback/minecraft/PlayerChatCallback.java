package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayerEntity;

import me.axieum.mcmod.minecord.api.chat.ReceiveChatCallback;

public class PlayerChatCallback implements ReceiveChatCallback
{
    @Override
    public void onReceiveChat(ServerPlayerEntity player, TextStream.Message message)
    {
        //
    }
}
