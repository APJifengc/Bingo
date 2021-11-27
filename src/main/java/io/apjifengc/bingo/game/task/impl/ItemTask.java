package io.apjifengc.bingo.game.task.impl;

import java.util.Arrays;
import java.util.List;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.util.NameUtil;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.md_5.bungee.api.chat.BaseComponent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.apjifengc.bingo.game.task.BingoTask;
import io.apjifengc.bingo.util.Message;
import lombok.Getter;
import lombok.Setter;

import static org.apache.commons.lang.Validate.*;

/**
 * 代表一个 Bingo 物品任务
 *
 * @author Milkory
 */
public class ItemTask extends BingoTask {

    @Setter
    @Getter
    private ItemStack target;

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
        item = NBTEditor.set(item, Message.getRaw("task.item-task.title", NameUtil.getItemName(target)),
                "display", "Name");
        for (String lore : Message.getWrapRaw("task.item-task.desc", NameUtil.getItemName(target))) {
            item = NBTEditor.set(item, lore, "display", "Lore", null);
        }
        this.shownItem = item;
        this.shownName = new BaseComponent[]{NameUtil.getItemName(target)};
    }

    public static ItemTask newInstance(String[] args) {
        isTrue(args.length >= 1, "The item ID not found");
        return new ItemTask(new ItemStack(Material.getMaterial(args[0].toUpperCase())));
    }

}
