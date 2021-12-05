package io.apjifengc.bingo.api.event.game;

import io.apjifengc.bingo.api.game.BingoGame;
import io.apjifengc.bingo.api.game.task.BingoTask;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Calls when a game is generating tasks which can be replaced by modifying this event.
 *
 * @author Milkory
 */
public class BingoGameGenerateTaskEvent extends BingoGameEvent {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final List<BingoTask> tasks;

    public BingoGameGenerateTaskEvent(@NotNull BingoGame game, @NotNull List<BingoTask> tasks) {
        super(game);
        this.tasks = tasks;
    }

    @NotNull @Override public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull public static HandlerList getHandlerList() {
        return handlers;
    }

}
