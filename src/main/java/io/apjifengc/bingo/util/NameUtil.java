package io.apjifengc.bingo.util;

import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class NameUtil {

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
        return new TranslatableComponent("item." + key.getNamespace() + "." + key.getKey());
    }

}
