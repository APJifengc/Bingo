package io.apjifengc.bingo.api.game.task.impl;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.api.game.task.BingoTask;
import io.apjifengc.bingo.util.Message;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a task which is impossible to finish.
 *
 * @author Milkory
 */
@SuppressWarnings("unused")
public class ImpossibleTask extends BingoTask {

    @Getter private final ItemStack shownItem;
    @Getter private final BaseComponent[] shownName = new ComponentBuilder("TMP").create();

    public ImpossibleTask() {
        ItemStack is = new ItemStack(Material.BARRIER);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(Message.get("task.impossible-task.title"));
        List<String> lore = Arrays.asList(Message.get("task.impossible-task.desc").split("\n"));
        im.setLore(lore);
        is.setItemMeta(im);
        this.shownItem = is;
    }

    @Override
    public @NotNull Listener getTaskListener() {
        return new Listener() {

        };
    }

    @Override
    public @NotNull Image getIcon(boolean isFinished) throws IOException {
        return ImageIO.read(Bingo.getInstance().getResource("icons/item/" + (isFinished ? "green" : "red") + "/barrier.png"));
    }

    public static ImpossibleTask newInstance(String[] args) {
        return new ImpossibleTask();
    }

}
