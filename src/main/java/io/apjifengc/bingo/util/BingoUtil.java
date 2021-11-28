package io.apjifengc.bingo.util;

import static org.apache.commons.lang.Validate.*;

import java.util.List;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import io.apjifengc.bingo.game.BingoPlayer;
import org.bukkit.inventory.ItemStack;

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

    public static ItemStack setRawDisplay(ItemStack item, String rawName, List<String> rawLore) {
        item = NBTEditor.set(item, rawName,"display", "Name");
        for (String lore :rawLore) {
            item = NBTEditor.set(item, lore, "display", "Lore", null);
        }
        return item;
    }

}
