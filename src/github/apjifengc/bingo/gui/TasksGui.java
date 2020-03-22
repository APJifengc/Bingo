package github.apjifengc.bingo.gui;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.util.BingoBoard;
import github.apjifengc.bingo.util.Msg;
import github.apjifengc.bingo.game.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TasksGui {
    public Inventory getTaskGUI(Player player, Bingo plugin) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new GuiHolder(),54,Msg.get("gui.title"));
        ItemStack itemStack = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§1");
        itemStack.setItemMeta(itemMeta);
        for(int i=0;i<9;i++){
            inventory.setItem(i+45,itemStack);
        }
        for(int i=0;i<5;i++){
            inventory.setItem(9*i,itemStack);
            inventory.setItem(1+9*i,itemStack);
            inventory.setItem(7+9*i,itemStack);
            inventory.setItem(8+9*i,itemStack);
        }

        itemStack.setType(Material.EMERALD);
        itemMeta.setDisplayName(Msg.get("gui.goal-title"));
        List<String> lore = new ArrayList<String>();
        Collections.addAll(lore, Msg.get("gui.goal-lore").split("\n"));
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(49,itemStack);
        ItemStack complete = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        itemMeta = complete.getItemMeta();
        itemMeta.setDisplayName(Msg.get("gui.complete"));
        itemMeta.setLore(null);
        complete.setItemMeta(itemMeta);
        for(int i = 0;i<25;i++){
            if (plugin.getCurrentGame().getPlayer(player).hasFinished(i)) {
                itemStack = complete;
            } else {
                itemStack = plugin.getCurrentGame().getTasks().get(i).getShowItem();
                if (plugin.getCurrentGame().getTasks().get(i) instanceof BingoItemTask) {
                    itemMeta.setDisplayName(Msg.get("task.item-task.title"));
                    lore = new ArrayList<String>();
                    Collections.addAll(lore, Msg.get("task.item-task.desc",itemStack.getType().name()).split("\n"));
                    itemMeta.setLore(lore);
                }
                itemStack.setItemMeta(itemMeta);
            }

            inventory.setItem(BingoBoard.getX(i)+BingoBoard.getY(i)*9+2,itemStack);
        }
        return inventory;
    }
}
