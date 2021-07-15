package io.apjifengc.bingo.util;

import static org.apache.commons.lang.Validate.*;

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

}
