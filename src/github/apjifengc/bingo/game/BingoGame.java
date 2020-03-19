package github.apjifengc.bingo.game;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.sun.istack.internal.Nullable;

import lombok.Getter;
import lombok.Setter;

/**
 * 代表一个 Bingo 游戏
 * 
 * @author Yoooooory
 */
public class BingoGame {

	@Setter
	@Getter
	ArrayList<BingoTask> tasks = new ArrayList<BingoTask>(25);

	@Getter
	ArrayList<BingoPlayer> players;

	/**
	 * 为这个 Bingo 游戏添加一个玩家。
	 * 
	 * @param player
	 */
	public void addPlayer(Player player) {
		players.add(new BingoPlayer(player, this));
	}

	/**
	 * 为这个 Bingo 游戏移除一个玩家。
	 * 
	 * @param player 要移除的玩家
	 * @return 若移除成功则返回 true，移除失败（玩家不存在）则返回 false。
	 */
	public boolean removePlayer(Player player) {
		BingoPlayer bp = getPlayer(player);
		if (bp != null) {
			 players.remove(bp);
			 return true;
		}
		return false;
	}

	/**
	 * 根据 Player 获取 BingoPlayer。
	 * 
	 * @param player Player
	 * @return 返回指定的 BingoPlayer，如果不存在则返回 null。
	 */
	@Nullable
	public BingoPlayer getPlayer(Player player) {
		for (BingoPlayer bp : players) {
			if (bp.getPlayer().getUniqueId().equals(player.getUniqueId())) {
				return bp;
			}
		}
		return null;
	}

}
