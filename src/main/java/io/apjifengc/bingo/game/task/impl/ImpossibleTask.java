package io.apjifengc.bingo.game.task.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.apjifengc.bingo.game.task.BingoTask;
import io.apjifengc.bingo.util.Message;
import org.jetbrains.annotations.NotNull;

public class ImpossibleTask implements BingoTask {

    @Getter private static final ItemStack shownItem;
    @Getter private static final List<BaseComponent> shownName = Collections.singletonList(new TextComponent("TMP"));

    static {
        ItemStack is = new ItemStack(Material.BARRIER);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(Message.get("task.impossible-task.title"));
        List<String> lore = Arrays.asList(Message.get("task.impossible-task.desc").split("\n"));
        im.setLore(lore);
        is.setItemMeta(im);
        shownItem = is;
    }

    public static ImpossibleTask newInstance(String[] args) {
        return new ImpossibleTask();
    }

}
