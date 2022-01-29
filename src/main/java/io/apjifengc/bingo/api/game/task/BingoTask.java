package io.apjifengc.bingo.api.game.task;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.api.game.BingoPlayer;
import io.apjifengc.bingo.api.game.task.impl.ImpossibleTask;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

/**
 * Represents a bingo task. <p>
 * <p>
 * Implementing this class, you should also add a public static method {@code newInstance(String[] args)}.
 * For a specific example, see {@link ImpossibleTask#newInstance(String[])}
 *
 * @author Milkory
 */
public abstract class BingoTask {
    /** Get an item which will be shown in the gui to represent this task. */
    @NotNull public abstract ItemStack getShownItem();

    /** Get the shown name of the task. */
    @NotNull public abstract BaseComponent[] getShownName();

    /** Get the task listener for the task. */
    @NotNull public abstract Listener getTaskListener();

    /** Get the icon shown on the map. (18px) */
    @NotNull public abstract Image getIcon(boolean isFinished) throws IOException;

    /**
     * Make the player finish the task. <br/>
     * If the player doesn't exist in the game, it will do nothing.
     *
     * @param player The player to finish the task.
     */
    public void finishTask(Player player) {
        BingoPlayer bingoPlayer = Bingo.getInstance().getPlayer(player);
        if (bingoPlayer != null) {
            bingoPlayer.finishTask(this);
        }
    }

    @Override public String toString() {
        return TextComponent.toPlainText(getShownName());
    }
}
