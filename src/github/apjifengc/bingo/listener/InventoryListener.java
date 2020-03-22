package github.apjifengc.bingo.listener;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.gui.GuiHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class InventoryListener implements Listener {
    private final Bingo plugin;
    public InventoryListener(Bingo plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
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

