package io.apjifengc.bingo.api.event.game;

import io.apjifengc.bingo.api.game.BingoGame;
import lombok.Getter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * @author Milkory
 */
@SuppressWarnings("unused")
public abstract class BingoGameEvent extends Event {

    @Getter protected final BingoGame game;

    public BingoGameEvent(@NotNull BingoGame game) {
        this.game = game;
    }

    public BingoGameEvent(@NotNull BingoGame game, boolean async) {
        super(async);
        this.game = game;
    }

}
