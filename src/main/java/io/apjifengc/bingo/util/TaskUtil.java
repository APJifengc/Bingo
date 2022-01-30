package io.apjifengc.bingo.util;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.apjifengc.bingo.api.game.task.BingoTask;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TaskUtil {

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
        var nbtItem = NBTItem.convertItemtoNBT(item);
        var event = new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                new Item(item.getType().getKey().toString(),
                        item.getAmount(),
                        ItemTag.ofNbt(nbtItem.getCompound("tag").toString())));

        var component = task.getShownName().clone();
        for (BaseComponent it : component) {
            it.setHoverEvent(event);
        }
        return component;
    }

    public static ItemStack setRawDisplay(ItemStack item, String rawName, List<String> rawLore) {
        var nbtItem = NBTItem.convertItemtoNBT(item);
        if (!nbtItem.hasKey("tag")) nbtItem.addCompound("tag");
        var tag = nbtItem.getCompound("tag");
        if (!tag.hasKey("display")) tag.addCompound("display");
        var display = tag.getCompound("display");

        display.setString("Name", rawName);

        var lore = display.getStringList("Lore");
        lore.clear();
        lore.addAll(rawLore);

        return NBTItem.convertNBTtoItem(nbtItem);
    }

    public static void setAllHideFlags(ItemStack item) {
        var meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
    }

}
