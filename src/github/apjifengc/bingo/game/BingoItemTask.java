package github.apjifengc.bingo.game;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

/**
 * 代表一个 Bingo 物品任务
 * @author Yoooooory
 *
 */
public class BingoItemTask extends BingoTask {

	@Setter
	@Getter
	ItemStack target;

	/**
	 * Bingo 物品任务
	 * @param target 任务目标
	 */
	public BingoItemTask(ItemStack target) {
		this.target = target;
		this.showItem = target;
	}

}
