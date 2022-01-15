package io.apjifengc.bingo.api.game.task.impl;

import io.apjifengc.bingo.api.game.BingoGame;
import io.apjifengc.bingo.api.game.BingoPlayer;
import io.apjifengc.bingo.api.game.task.BingoTask;
import io.apjifengc.bingo.util.Message;
import io.apjifengc.bingo.util.TaskUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
                if (event.getClickedInventory() != null && event.getResult() == Event.Result.ALLOW) {
                    if (event.getRawSlot() == event.getSlot()) {
                        if (event.getWhoClicked() instanceof Player &&
                                event.getInventory().getItem(event.getRawSlot()) != null) {
                            getItem((Player) event.getWhoClicked(), event.getInventory().getItem(event.getRawSlot()));
                        }
                    } else if (event.getClickedInventory().getType() == InventoryType.PLAYER
                            && (event.getSlot() == 8 || event.getHotbarButton() == 8)) {
                        event.setCancelled(true);
                    }
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
}
