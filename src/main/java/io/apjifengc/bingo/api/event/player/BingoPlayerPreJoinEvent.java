package io.apjifengc.bingo.api.event.player;

import io.apjifengc.bingo.api.game.BingoGame;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Calls before player join a Bingo game.
 *
 * @author Milkory
 */
@SuppressWarnings("unused")
public class BingoPlayerPreJoinEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final BingoGame game;
    @Getter @Setter private boolean isCancelled;

    public BingoPlayerPreJoinEvent(@NotNull Player who, @NotNull BingoGame game) {
        super(who);
        this.game = game;
    }

    @NotNull @Override public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull public static HandlerList getHandlerList() {
        return handlers;
    }

}
