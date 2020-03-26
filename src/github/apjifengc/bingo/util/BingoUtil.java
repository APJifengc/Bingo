package github.apjifengc.bingo.util;

import java.util.ArrayList;

import github.apjifengc.bingo.game.BingoPlayer;

/**
 * Bingo 游戏盘工具类
 * 
 * @author Yoooooory
 *
 */
public class BingoUtil {

	/**
	 * 获取指定格子所在的横坐标（列数）。
	 * 
	 * @param index
	 * @return 指定格子所在的行数，若在第一行则返回 0，第二行返回 1，以此类推。
	 */
	public static int getBoardX(int index) {
		while (index >= 5) {
			index -= 5;
		}
		return index;
	}

	/**
	 * 获取指定格子所在的纵坐标（行数）。
	 * 
	 * @param index
	 * @return 指定格子所在的行数，若在第一行则返回 0，第二行返回 1，以此类推。
	 */
	public static int getBoardY(int index) {
		return index / 5;
	}

	/**
	 * 获取指定纵坐标（行数）的第一格索引
	 * 
	 * @param y
	 * @return 指定纵坐标（行数）的第一格索引
	 */
	public static int getBoardYFirst(int y) {
		return 5 * y;
	}

	public static void sendMessage(ArrayList<BingoPlayer> bingoPlayer, String msg) {
		bingoPlayer.forEach((s) -> s.getPlayer().sendMessage(msg));
	}

}
