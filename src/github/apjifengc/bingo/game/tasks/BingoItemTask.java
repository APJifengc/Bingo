package github.apjifengc.bingo.game.tasks;

import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import github.apjifengc.bingo.game.BingoTask;
import github.apjifengc.bingo.util.BingoUtil;
import github.apjifengc.bingo.util.Message;
import lombok.Getter;
import lombok.Setter;

/**
 * 代表一个 Bingo 物品任务
 * 
 * @author Yoooooory
 *
 */
public class BingoItemTask extends BingoTask {

	@Setter
	@Getter
	ItemStack target;

	/**
	 * Bingo 物品任务
	 * 
	 * @param target 任务目标
	 */
	public BingoItemTask(ItemStack target) {
		this.target = target;
		ItemMeta im = target.getItemMeta();
		im.setDisplayName(Message.get("task.item-task.title"));
		List<String> lore = Arrays
				.asList(Message.get("task.item-task.desc", BingoUtil.getItemName(target)).split("\n"));
		im.setLore(lore);
		target.setItemMeta(im);
		this.showItem = target;
	}

}
