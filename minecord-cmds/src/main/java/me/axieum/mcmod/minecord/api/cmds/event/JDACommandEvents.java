package me.axieum.mcmod.minecord.api.cmds.event;

import java.util.List;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A collection of callbacks for managing JDA commands.
 */
public final class JDACommandEvents
{
    private JDACommandEvents() {}

    /**
     * Called before updating the Discord command list.
     */
    public static final Event<BeforeUpdateCommandList> BEFORE_UPDATE_COMMAND_LIST =
        EventFactory.createArrayBacked(BeforeUpdateCommandList.class, callbacks -> action -> {
            for (BeforeUpdateCommandList callback : callbacks)
                action = callback.onUpdatingCommandList(action);
            return action;
        });

    /**
     * Called after updating the Discord command list.
     */
    public static final Event<AfterUpdateCommandList> AFTER_UPDATE_COMMAND_LIST =
        EventFactory.createArrayBacked(AfterUpdateCommandList.class, callbacks -> commands -> {
            for (AfterUpdateCommandList callback : callbacks)
                callback.onUpdateCommandList(commands);
        });

    @FunctionalInterface
    public interface BeforeUpdateCommandList
    {
        /**
         * Called before updating the Discord command list.
         *
         * @param action command list update action
         * @return command list update action to be queued
         */
        @NotNull CommandListUpdateAction onUpdatingCommandList(@NotNull CommandListUpdateAction action);
    }

    @FunctionalInterface
    public interface AfterUpdateCommandList
    {
        /**
         * Called after updating the Discord command list.
         *
         * @param commands list of resulting commands
         */
        void onUpdateCommandList(List<Command> commands);
    }
}
