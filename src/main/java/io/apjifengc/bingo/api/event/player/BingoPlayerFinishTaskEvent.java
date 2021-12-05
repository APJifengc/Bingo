package io.apjifengc.bingo.api.event.player;

import io.apjifengc.bingo.api.game.BingoPlayer;
import io.apjifengc.bingo.api.game.task.BingoTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Calls when a player finished a task.
 *
 * @author Milkory
 */
public class BingoPlayerFinishTaskEvent extends BingoPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final BingoTask task;
    @Getter private final int index;
    @Getter @Setter private boolean isCancelled;

    public BingoPlayerFinishTaskEvent(@NotNull BingoPlayer who, @NotNull BingoTask task, int index) {
        super(who);
        this.task = task;
        this.index = index;
    }

    @NotNull @Override public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull public static HandlerList getHandlerList() {
        return handlers;
    }

}
