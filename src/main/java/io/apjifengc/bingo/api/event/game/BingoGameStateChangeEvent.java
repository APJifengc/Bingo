package io.apjifengc.bingo.api.event.game;

import io.apjifengc.bingo.api.game.BingoGame;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Calls when a Bingo game changes its state.
 *
 * @author Milkory
 */
@SuppressWarnings("unused")
public class BingoGameStateChangeEvent extends BingoGameEvent {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final BingoGame.State before;
    @Getter private final BingoGame.State after;

    public BingoGameStateChangeEvent(@NotNull BingoGame game, @NotNull BingoGame.State before, @NotNull BingoGame.State after) {
        super(game);
        this.before = before;
        this.after = after;
    }

    @NotNull @Override public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull public static HandlerList getHandlerList() {
        return handlers;
    }

}
