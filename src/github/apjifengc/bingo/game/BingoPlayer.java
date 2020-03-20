package github.apjifengc.bingo.game;

import org.bukkit.entity.Player;

import github.apjifengc.bingo.util.BingoBroad;
import lombok.Getter;

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
	}

	/**
	 * 使玩家完成一项任务。
	 * 
	 * @param task 要完成的任务
	 */
	public void finishTask(BingoTask task) {
		taskStatus[game.getTasks().indexOf(task)] = true;
	}

	/**
	 * 检查玩家是否已完成 Bingo。
	 * 
	 * @return 若已完成 Bingo 则返回true，反之返回 false。
	 */
	public boolean checkVictory() {
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
	public boolean checkVictory(int index) {
		if (index % 6 == 0) {
			if (taskStatus[0] && taskStatus[6] && taskStatus[12] && taskStatus[18] && taskStatus[24])
				return true;
		} else if (index % 4 == 0 && index != 0) {
			if (taskStatus[4] && taskStatus[8] && taskStatus[12] && taskStatus[16] && taskStatus[20])
				return true;
		}
		int row = BingoBroad.getX(index);
		if (taskStatus[row] && taskStatus[row + 5] && taskStatus[row + 10] && taskStatus[row + 15]
				&& taskStatus[row + 20])
			return true;
		int col = BingoBroad.getYFirst(BingoBroad.getY(index));
		if (taskStatus[col] && taskStatus[col + 1] && taskStatus[col + 2] && taskStatus[col + 3] && taskStatus[col + 4])
			return true;
		return false;
	}

	/**
	 * 根据任务检查玩家是否已完成相关 Bingo。
	 * @param task 要检查的任务
	 * @return 若已完成相关 Bingo 则返回true，反之返回 false。
	 */
	public boolean checkVictory(BingoTask task) {
		int index = game.getTasks().indexOf(task);
		if (index != -1) {
			return checkVictory(index);
		}
		return false;
	}

}
