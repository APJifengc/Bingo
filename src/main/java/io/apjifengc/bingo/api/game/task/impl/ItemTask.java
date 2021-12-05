package io.apjifengc.bingo.api.game.task.impl;

import io.apjifengc.bingo.api.game.task.BingoTask;
import io.apjifengc.bingo.util.Message;
import io.apjifengc.bingo.util.TaskUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.apache.commons.lang.Validate.isTrue;

/**
 * Represents a task which asks players to get items.
 *
 * @author Milkory
 */
public class ItemTask extends BingoTask {

    @Getter private final ItemStack target;

    @Getter private final ItemStack shownItem;
    @Getter private final BaseComponent[] shownName;

    /**
     * Bingo 物品任务
     *
     * @param target 任务目标
     */
    public ItemTask(ItemStack target) {
        this.target = target;
        var item = target.clone();
        TaskUtil.setAllHideFlags(item);
        item = TaskUtil.setRawDisplay(item, Message.getRaw("task.item-task.title", TaskUtil.getItemName(target)),
                Message.getWrapRaw("task.item-task.desc", TaskUtil.getItemName(target)));
        this.shownItem = item;
        this.shownName = new BaseComponent[]{TaskUtil.getItemName(target)};
    }

    public static ItemTask newInstance(String[] args) {
        isTrue(args.length >= 1, "The item ID not found");
        return new ItemTask(new ItemStack(Material.getMaterial(args[0].toUpperCase())));
    }

}
