package github.apjifengc.bingo.game.tasks;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import github.apjifengc.bingo.game.BingoTask;
import github.apjifengc.bingo.util.Message;

public class BingoImpossibleTask extends BingoTask {

	public BingoImpossibleTask() {
		ItemStack is = new ItemStack(Material.BARRIER);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Message.get("task.impossible-task.title"));
		List<String> lore = Arrays
				.asList(Message.get("task.impossible-task.desc").split("\n"));
		im.setLore(lore);
		is.setItemMeta(im);
		this.showItem = is;
	}

}
