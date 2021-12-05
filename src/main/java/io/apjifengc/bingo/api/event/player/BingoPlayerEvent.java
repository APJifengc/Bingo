package io.apjifengc.bingo.api.event.player;

import io.apjifengc.bingo.api.game.BingoPlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * @author Milkory
 */
public abstract class BingoPlayerEvent extends Event {

    @Getter protected BingoPlayer player;

    public BingoPlayerEvent(@NotNull BingoPlayer who) {
        this.player = who;
    }

    public BingoPlayerEvent(@NotNull BingoPlayer who, boolean async) {
        super(async);
        this.player = who;
    }

}
