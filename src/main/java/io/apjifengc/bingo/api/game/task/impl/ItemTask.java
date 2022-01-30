package io.apjifengc.bingo.api.game.task.impl;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.api.game.task.BingoTask;
import io.apjifengc.bingo.util.Message;
import io.apjifengc.bingo.util.TaskUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

import static org.apache.commons.lang.Validate.isTrue;

/**
 * Represents a task which asks players to get items.
 *
 * @author Milkory
 */
@SuppressWarnings("unused")
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
        return new ItemTask(new ItemStack(Objects.requireNonNull(Material.getMaterial(args[0].toUpperCase()))));
    }

    @Override
    public @NotNull Listener getTaskListener() {
        return new Listener() {
            @EventHandler(ignoreCancelled = true)
            void onPickupItem(EntityPickupItemEvent event) {
                if (event.getEntity() instanceof Player) {
                    getItem((Player) event.getEntity(), event.getItem().getItemStack());
                }
            }

            @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
            void onClickInventory(InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player &&
                        event.getView().getItem(event.getRawSlot()) != null) {
                    getItem((Player) event.getWhoClicked(), event.getView().getItem(event.getRawSlot()));
                }
            }

            @EventHandler(ignoreCancelled = true)
            void onUseBucket(PlayerBucketFillEvent event) {
                getItem(event.getPlayer(), event.getItemStack());
            }

            void getItem(Player player, ItemStack itemStack) {
                if (getTarget().getType() == itemStack.getType()) {
                    finishTask(player);
                }
            }
        };
    }

    @Override
    public @NotNull Image getIcon(boolean isFinished) throws IOException {
        return ImageIO.read(Bingo.getInstance().getResource("icons/item/" + (isFinished ? "green" : "red") + "/" + target.getType().getKey().getKey() + ".png"));
    }
}
