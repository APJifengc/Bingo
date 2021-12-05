package io.apjifengc.bingo.api.game.task;

import io.apjifengc.bingo.api.game.task.impl.ImpossibleTask;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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

    @Override public String toString() {
        return TextComponent.toPlainText(getShownName());
    }
}
