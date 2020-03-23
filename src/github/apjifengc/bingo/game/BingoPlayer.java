package github.apjifengc.bingo.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import github.apjifengc.bingo.inventory.BingoGuiInventory;
import github.apjifengc.bingo.util.BingoBoard;
import github.apjifengc.bingo.util.Message;
import lombok.Getter;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * 代表一个在 Bingo 游戏中的玩家
 * 
 * @author Yoooooory
 *
 */
public class BingoPlayer {

	@Getter
	Player player;

	@Getter
	BingoGame game;

	@Getter
	Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

	boolean[] taskStatus = new boolean[25];

	public BingoPlayer(Player player, BingoGame game) {
		this.player = player;
		this.game = game;
	}

	/**
	 * 根据任务索引检查该玩家是否已完成某任务。
	 * 
	 * @param index 要检查的任务的索引
	 * @return 若玩家已完成该任务则返回 true，反之返回 false。
	 */
	public boolean hasFinished(int index) {
		return taskStatus[index];
	}

	/**
	 * 根据任务检查该玩家是否已完成某任务。
	 * 
	 * @param task 要检查的任务
	 * @return 若玩家已完成该任务则返回 true，反之返回 false。
	 */
	public boolean hasFinished(BingoTask task) {
		return taskStatus[game.getTasks().indexOf(task)];
	}

	/**
	 * 使玩家完成一项任务。
	 * 
	 * @param index 要完成的任务的索引
	 */
	public void finishTask(int index) {
		taskStatus[index] = true;
		if (checkBingo(index)) {
			game.completeBingo(this);
		}
	}

	/**
	 * 使玩家完成一项任务。
	 * 
	 * @param task 要完成的任务
	 */
	public void finishTask(BingoTask task) {
		taskStatus[game.getTasks().indexOf(task)] = true;
		if (checkBingo(task)) {
			game.completeBingo(this);
		}
	}

	/**
	 * 检查玩家是否已完成 Bingo。
	 * 
	 * @return 若已完成 Bingo 则返回true，反之返回 false。
	 */
	public boolean checkBingo() {
		if (taskStatus[0] && taskStatus[6] && taskStatus[12] && taskStatus[18] && taskStatus[24])
			return true;
		if (taskStatus[4] && taskStatus[8] && taskStatus[12] && taskStatus[16] && taskStatus[20])
			return true;
		for (int i = 0, j = 0; i < 5; i++, j += 5) {
			if (taskStatus[i] && taskStatus[i + 5] && taskStatus[i + 10] && taskStatus[i + 15] && taskStatus[i + 20])
				return true;
			if (taskStatus[j] && taskStatus[j + 1] && taskStatus[j + 2] && taskStatus[j + 3] && taskStatus[j + 4]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据任务索引检查玩家是否已完成相关 Bingo。
	 * 
	 * @param index 任务索引
	 * @return 若已完成 Bingo 则返回true，反之返回 false。
	 */
	public boolean checkBingo(int index) {
		if (index % 6 == 0) {
			if (taskStatus[0] && taskStatus[6] && taskStatus[12] && taskStatus[18] && taskStatus[24])
				return true;
		} else if (index % 4 == 0 && index != 0) {
			if (taskStatus[4] && taskStatus[8] && taskStatus[12] && taskStatus[16] && taskStatus[20])
				return true;
		}
		int row = BingoBoard.getX(index);
		if (taskStatus[row] && taskStatus[row + 5] && taskStatus[row + 10] && taskStatus[row + 15]
				&& taskStatus[row + 20])
			return true;
		int col = BingoBoard.getYFirst(BingoBoard.getY(index));
		if (taskStatus[col] && taskStatus[col + 1] && taskStatus[col + 2] && taskStatus[col + 3] && taskStatus[col + 4])
			return true;
		return false;
	}

	/**
	 * 根据任务检查玩家是否已完成相关 Bingo。
	 * 
	 * @param task 要检查的任务
	 * @return 若已完成相关 Bingo 则返回true，反之返回 false。
	 */
	public boolean checkBingo(BingoTask task) {
		int index = game.getTasks().indexOf(task);
		if (index != -1) {
			return checkBingo(index);
		}
		return false;
	}

	/**
	 * 获取玩家的任务清单 GUI。
	 * 
	 * @return GUI
	 */
	public Inventory getInventory() {
		Inventory inv = Bukkit.createInventory((InventoryHolder) new BingoGuiInventory(), 54, Message.get("gui.title"));
		ItemStack is = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("§1");
		is.setItemMeta(im);
		for (int i = 0; i <= 36; i += 9) {
			inv.setItem(i, is);
			inv.setItem(i + 8, is);
		}
		for (int i = 45; i <= 53; i++) {
			inv.setItem(i, is);
		}
		is.setType(Material.EMERALD);
		im.setDisplayName(Message.get("gui.goal-title"));
		List<String> lore = Arrays.asList(Message.get("gui.goal-lore").split("\n"));
		im.setLore(lore);
		is.setItemMeta(im);
		inv.setItem(49, is);
		ItemStack complete = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		im = complete.getItemMeta();
		im.setDisplayName(Message.get("gui.complete"));
		im.setLore(null);
		complete.setItemMeta(im);
		for (int i = 0; i < 25; i++) {
			if (hasFinished(i)) {
				is = complete;
			} else {
				is = game.getTasks().get(i).getShowItem();
				if (game.getTasks().get(i) instanceof BingoItemTask) {
					im.setDisplayName(Message.get("task.item-task.title"));
					lore = Arrays.asList(Message.get("task.item-task.desc", is.getI18NDisplayName()).split("\n"));
					im.setLore(lore);
				}
				is.setItemMeta(im);
			}

			inv.setItem(BingoBoard.getX(i) + BingoBoard.getY(i) * 9 + 2, is);
		}
		return inv;
	}

	/**
	 * 给玩家显示计分板。
	 */
	public void showScoreboard() {
		Objective obj;
		try {
			obj = scoreboard.registerNewObjective("bingoInfo", "dummy",
					Message.get("scoreboard.in-game.title"));
		} catch (Exception ignored) {
			obj=scoreboard.getObjective("bingoInfo");
		}
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.setScoreboard(scoreboard);
		updateScoreboard();
	}

	public void clearScoreboard() {
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	/**
	 * 更新玩家的记分板。
	 */
	public void updateScoreboard() {
		int i = 10;
		List<String> stringList = new ArrayList<String>();
		scoreboard.getEntries().forEach((s) -> scoreboard.resetScores(s));
		Objective obj = scoreboard.getObjective("bingoInfo");
		String[] tasksString = { "", "", "", "", "" };
		for (int k = 1; k <= 5; k++) {
			for (int j = 1; j <= 5; j++) {
				tasksString[k - 1] = tasksString[k - 1]
						+ (hasFinished(5 * k + j - 6) ? Message.get("scoreboard.in-game.completed")
								: Message.get("scoreboard.in-game.uncompleted"));
			}
		}
		String[] strs = Message.get("scoreboard.in-game.main", tasksString[0], tasksString[1], tasksString[2],
				tasksString[3], tasksString[4]).split("\n");
		for (String str : strs) {
			i--;
			if (stringList.contains(str)) {
				stringList.add(str + "§1");
				obj.getScore(str + "§1").setScore(i);
			} else {
				stringList.add(str);
				obj.getScore(str).setScore(i);
			}
		}
	}

}
