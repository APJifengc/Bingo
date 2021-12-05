package io.apjifengc.bingo.api.util;

import io.apjifengc.bingo.api.game.BingoPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.List;

import static org.apache.commons.lang.Validate.isTrue;

/**
 * Bingo general utilities.
 *
 * @author Milkory
 */
public class BingoUtil {

    /** Get the column which the index is in. */
    public static int getBoardX(int index) {
        isTrue(index >= 0 && index <= 24, "Invalid index");
        while (index >= 5) {
            index -= 5;
        }
        return index;
    }

    /** Get the row which the index is in. */
    public static int getBoardY(int index) {
        isTrue(index >= 0 && index <= 24, "Invalid index");
        return index / 5;
    }

    /** Get index of the row's first slot. */
    public static int getBoardYFirst(int row) {
        isTrue(row >= 0 && row <= 5, "Invalid row");
        return 5 * row;
    }

    /** Send formatted message to players. */
    public static void sendMessage(List<BingoPlayer> players, String message) {
        players.forEach(p -> p.getPlayer().sendMessage(message));
    }

    public static boolean callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
        return !(event instanceof Cancellable) || !((Cancellable) event).isCancelled();
    }

}
