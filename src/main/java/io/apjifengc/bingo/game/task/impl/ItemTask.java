package io.apjifengc.bingo.game.task.impl;

import java.util.Arrays;
import java.util.List;

import io.apjifengc.bingo.util.NameUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.apjifengc.bingo.game.task.BingoTask;
import io.apjifengc.bingo.util.Message;
import lombok.Getter;
import lombok.Setter;

/**
 * 代表一个 Bingo 物品任务
 *
 * @author Milkory
 */
public class ItemTask implements BingoTask {

    @Setter @Getter ItemStack target;

    /**
     * Bingo 物品任务
     *
     * @param target 任务目标
     */
    public ItemTask(ItemStack target) {
        this.target = target;
        ItemMeta im = target.getItemMeta();
        im.setDisplayName(Message.get("task.item-task.title", NameUtil.getItemName(target)));
        List<String> lore = Arrays.asList(Message.get("task.item-task.desc", NameUtil.getItemName(target)).split("\\n"));
        im.setLore(lore);
        target.setItemMeta(im);
        this.showItem = target;
        this.name = NameUtil.getItemName(target);
    }

}
