package io.apjifengc.bingo.util;

import java.util.Collections;
import java.util.List;

import io.apjifengc.bingo.game.task.BingoTask;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class TaskUtil {

    /** Format a string like "{@code iron_golem}" to "{@code Iron Golem}". */
    public static String formatName(String ori) {
        StringBuilder sb = new StringBuilder();
        boolean isUpperCase = true;
        for (int i = 0; i < ori.length(); i++) {
            if (ori.charAt(i) == '_') {
                sb.append(" ");
                isUpperCase = true;
            } else if (isUpperCase) {
                sb.append(ori.toUpperCase().charAt(i));
                isUpperCase = false;
            } else {
                sb.append(ori.toLowerCase().charAt(i));
            }
        }
        return sb.toString();
    }

    public static BaseComponent getItemName(ItemStack is) {
        if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            return new TextComponent(is.getItemMeta().getDisplayName());
        }
        return getItemName(is.getType());
    }

    public static BaseComponent getItemName(Material mat) {
        var key = mat.getKey();
        return new TranslatableComponent((mat.isBlock() ? "block." : "item.") + key.getNamespace() + "." + key.getKey());
    }

    public static BaseComponent getEntityName(EntityType type) {
        var key = type.getKey();
        return new TranslatableComponent("entity." + key.getNamespace() + "." + key.getKey());
    }

    public static BaseComponent[] getTaskComponent(BingoTask task) {
        var item = task.getShownItem();
        var event = new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                new Item(item.getType().getKey().toString(),
                        item.getAmount(),
                        ItemTag.ofNbt(NBTEditor.getNBTCompound(item, "tag").toString())));

        var component = task.getShownName().clone();
        for (BaseComponent it : component) {
            it.setHoverEvent(event);
        }
        return component;
    }

    public static ItemStack setRawDisplay(ItemStack item, String rawName, List<String> rawLore) {
        item = NBTEditor.set(item, rawName,"display", "Name");
        for (String lore :rawLore) {
            item = NBTEditor.set(item, lore, "display", "Lore", null);
        }
        return item;
    }

    public static ItemStack setAllHideFlags(ItemStack item) {
        var meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        return item;
    }

}
