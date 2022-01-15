package io.apjifengc.bingo.api.event.player;

import io.apjifengc.bingo.api.game.BingoPlayer;
import io.apjifengc.bingo.api.game.task.BingoTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Calls when a player finished Bingo.
 *
 * @author Milkory
 */
@SuppressWarnings("unused")
public class BingoPlayerFinishBingoEvent extends BingoPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final BingoTask lastTask;
    @Getter private final int lastIndex;
    @Getter @Setter private boolean isCancelled;

    public BingoPlayerFinishBingoEvent(@NotNull BingoPlayer who, @NotNull BingoTask lastTask, int lastIndex) {
        super(who);
        this.lastTask = lastTask;
        this.lastIndex = lastIndex;
    }

    @NotNull @Override public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull public static HandlerList getHandlerList() {
        return handlers;
    }

}
