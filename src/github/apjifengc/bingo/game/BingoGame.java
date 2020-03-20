package github.apjifengc.bingo.game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import github.apjifengc.bingo.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sun.istack.internal.Nullable;

import github.apjifengc.bingo.exception.BadTaskException;
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
	ArrayList<BingoPlayer> players = new ArrayList<BingoPlayer>();

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

	/**
	 * 生成任务列表，这个方法不会在对象初始化时被调用。
	 * @throws IOException
	 * @throws InvalidConfigurationException
	 * @throws BadTaskException
	 */
	public void generateTasks() throws IOException, InvalidConfigurationException, BadTaskException {
		YamlConfiguration taskYaml = new YamlConfiguration();
		taskYaml.load(new File(Bukkit.getPluginManager().getPlugin("Bingo").getDataFolder(), "tasks.yml"));
		List<String> items = taskYaml.getStringList("items");
		// 任务数少于25个，不足一场游戏时抛出 BadTaskException
		if (items.size() < 25) {
			throw new BadTaskException(Message.getMessage("errors.bad-task.number-less-than-25"));
		}
		Random random = new Random();
		for (int i = 0; i < 25; i++) {
			int index = random.nextInt(items.size());
			Material m = Material.getMaterial(items.get(index));
			if (m != null) {
				tasks.add(new BingoItemTask(new ItemStack(m)));
				items.remove(index);
			} else {
				throw new BadTaskException(Message.getMessage("errors.bad-task.cant-solve",items.get(index)));
			}
		}
	}

}
