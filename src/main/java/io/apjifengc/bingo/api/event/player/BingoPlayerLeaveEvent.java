package io.apjifengc.bingo.api.event.player;

import io.apjifengc.bingo.api.game.BingoPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Calls when a player leave a Bingo game.
 *
 * @author Milkory
 */
@SuppressWarnings("unused")
public class BingoPlayerLeaveEvent extends BingoPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter @Setter private boolean isCancelled;

    public BingoPlayerLeaveEvent(@NotNull BingoPlayer who) {
        super(who);
    }

    @NotNull @Override public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull public static HandlerList getHandlerList() {
        return handlers;
    }

}
