package io.apjifengc.bingo.listener;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.api.inventory.BingoGuiInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.CraftingInventory;

public final class InventoryListener implements Listener {

    private final Bingo plugin;

    public InventoryListener(Bingo plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            if (event.getClickedInventory().getHolder() != null
                    && event.getClickedInventory().getHolder() instanceof BingoGuiInventory) {
                event.setCancelled(true);
            }
            if (!(event.getView().getTopInventory() instanceof CraftingInventory)) {
                if (plugin.getCurrentGame() != null) {
                    if (plugin.getCurrentGame().getTaskItem().isSimilar(event.getCurrentItem())) {
                        if (event.isShiftClick()) {
                            event.setCancelled(true);
                        }
                    }
                    if (plugin.getCurrentGame().getTaskItem().isSimilar(event.getCursor())) {
                        if (event.getClickedInventory() != event.getWhoClicked().getInventory()) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() != null
                && event.getInventory().getHolder() instanceof BingoGuiInventory) {
            event.setCancelled(true);
        }
        if (plugin.getCurrentGame() != null) {
            if (plugin.getCurrentGame().getTaskItem().isSimilar(event.getOldCursor())) {
                if (!(event.getView().getTopInventory() instanceof CraftingInventory)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
