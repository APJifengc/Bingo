package github.apjifengc.bingo.game;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;

/**
 * 代表一个 Bingo 任务
 * 
 * @author Yoooooory
 */
public abstract class BingoTask {

	@Getter
	public ItemStack showItem;

}