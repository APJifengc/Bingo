package github.apjifengc.bingo.listener;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.gui.GuiHolder;
import github.apjifengc.bingo.gui.TasksGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ClickInventoryListener implements Listener {
    private final Bingo plugin;
    public ClickInventoryListener(Bingo plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack border = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta itemMeta = border.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("");
        if(!(event.getWhoClicked() instanceof Player)) { return;}
        if(event.getClickedInventory().getHolder() instanceof GuiHolder){
            event.setCancelled(true);
        }
    }
}

