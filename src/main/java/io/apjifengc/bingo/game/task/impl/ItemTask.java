package io.apjifengc.bingo.game.task.impl;

import java.util.Arrays;
import java.util.List;

import io.apjifengc.bingo.util.NameUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

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
        ItemMeta im = target.getItemMeta();
        im.setDisplayName(Message.get("task.item-task.title", NameUtil.getItemName(target)));
        List<String> lore = Arrays
                .asList(Message.get("task.item-task.desc", NameUtil.getItemName(target)).split("\\n"));
        im.setLore(lore);
        target.setItemMeta(im);
        this.shownItem = target;
        this.shownName = NameUtil.getItemName(target).toArray(new BaseComponent[0]);
    }

    public static ItemTask newInstance(String[] args) {
        isTrue(args.length >= 1, "The item ID not found");
        return new ItemTask(new ItemStack(Material.getMaterial(args[0].toUpperCase())));
    }

}
