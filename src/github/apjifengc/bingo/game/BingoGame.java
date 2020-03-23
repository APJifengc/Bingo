package github.apjifengc.bingo.game;

import java.io.IOException;
import java.util.*;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.util.Configs;
import github.apjifengc.bingo.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sun.istack.internal.Nullable;

import github.apjifengc.bingo.exception.BadTaskException;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

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

	ArrayList<BukkitTask> eventTasks = new ArrayList<BukkitTask>();

	int startTimer;

	@Getter
	BingoGameState state;

	Map<Integer, Integer> TimeMap = new HashMap<Integer, Integer>();

	@Getter
	Scoreboard scoreboard;

	Bingo plugin;

	/**
	 * 为这个 Bingo 游戏添加一个玩家。
	 * 
	 * @param player
	 */
	public void addPlayer(Player player) {
		BingoPlayer p = new BingoPlayer(player, this);
		players.add(p);
		for (BingoPlayer bp : players) {
			bp.getPlayer().sendMessage(Message.get("chat.join", player.getName(), players.size(),
					Configs.getMainCfg().getInt("room.max-player")));
		}
		if (state == BingoGameState.WAITING) {
			updateStartTime();
		} else if (state == BingoGameState.RUNNING) {
			p.showScoreboard();
		}
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
			bp.clearScoreboard();
			players.remove(bp);
			for (BingoPlayer p : players) {
				p.getPlayer().sendMessage(Message.get("chat.leave", player.getName(), players.size(),
						Configs.getMainCfg().getInt("room.max-player")));
			}
			if (state == BingoGameState.WAITING) {
				updateStartTime();
			}
			return true;
		}
		return false;
	}

	/**
	 * 根据 Player（UUID） 获取 BingoPlayer。
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
	 * 
	 * @throws IOException
	 * @throws InvalidConfigurationException
	 * @throws BadTaskException
	 */
	public void generateTasks() throws BadTaskException {
		List<String> items = Configs.getTaskCfg().getStringList("items");
		// 任务数少于25个，不足一场游戏时抛出 BadTaskException
		if (items.size() < 25) {
			throw new BadTaskException(Message.get("errors.bad-task.number-less-than-25"));
		}
		Random random = new Random();
		for (int i = 0; i < 25; i++) {
			int index = random.nextInt(items.size());
			Material m = Material.getMaterial(items.get(index));
			if (m != null) {
				tasks.add(new BingoItemTask(new ItemStack(m)));
				items.remove(index);
			} else {
				throw new BadTaskException(Message.get("errors.bad-task.cant-solve", items.get(index)));
			}
		}
	}

	public BingoGame(Bingo plugin) {
		this.plugin = plugin;
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("bingo", "dummy",
				Message.get("scoreboard.start-timer.title"));
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		List<String> TimeList = Configs.getMainCfg().getStringList("room.start-time");
		this.state = BingoGameState.WAITING;
		startTimer = -1;
		for (String str : TimeList) {
			String[] strings;
			strings = str.split("\\|");
			try {
				TimeMap.put(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		eventTasks.add(new BukkitRunnable() {
			@Override
			public void run() {
				if (startTimer != -1) {
					startTimer--;
				}
				if (startTimer == 0) {
					this.cancel();
					start();
				}
				updateScoreboard();
			}
		}.runTaskTimer(plugin, 0L, 20L));

	}

	public void updateScoreboard() {
		int i = 10;
		List<String> stringList = new ArrayList<String>();
		// 如果游戏正在等待玩家加入
		if (this.state == BingoGameState.WAITING) {
			Objective obj = scoreboard.getObjective("bingo");
			scoreboard.getEntries().forEach((s) -> scoreboard.resetScores(s));
			String timeString;
			if (startTimer == -1) {
				timeString = Message.get("scoreboard.start-timer.wait-for-people");
			} else {
				timeString = Message.get("scoreboard.start-timer.starting-in", startTimer);
			}
			String[] strs = Message.get("scoreboard.start-timer.main", players.size(),
					Configs.getMainCfg().getInt("room.max-player"), timeString).split("\n");
			for (String str : strs) {
				i--;
				if (stringList.contains(str)) {
					stringList.add(str + "§1");
					Score score = obj.getScore(str + "§1");
					score.setScore(i);
				} else {
					stringList.add(str);
					Score score = obj.getScore(str);
					score.setScore(i);
				}
			}
			if (startTimer > 1 && startTimer < 5) {
				for (BingoPlayer bp : players) {
					Player p = bp.getPlayer();
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2048.0f, 1.0f);
					p.sendTitle("§6" + String.valueOf(startTimer), "", 0, 50, 10);
				}
			} else if (startTimer == 1) {
				for (BingoPlayer bp : players) {
					Player p = bp.getPlayer();
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2048.0f, 1.0f);
					p.sendTitle("§4" + String.valueOf(startTimer), "", 0, 50, 10);
				}
			}
		} else if (this.state == BingoGameState.STOPPED) {
			// TODO 游戏结束的计分板 显示胜利玩家
		}
	}

	public void updateStartTime() {
		for (int i = 1; i <= players.size(); i++) {
			if (TimeMap.containsKey(i)) {
				if (startTimer > TimeMap.get(i) || startTimer == -1) {
					startTimer = TimeMap.get(i);
				}
			}
		}
		if (startTimer != -1 && players.size() < Configs.getMainCfg().getInt("room.min-player")) {
			startTimer = -1;
			for (BingoPlayer bp : players) {
				Player p = bp.getPlayer();
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 2048.0f, 1.0f);
				p.sendTitle("", Message.get("chat.not-enough-player"), 0, 55, 5);
			}
		}
		updateScoreboard();
	}

	public void start() {
		for (BingoPlayer bp : players) {
			Player p = bp.getPlayer();
			p.sendTitle("§e这里应该有一句文案", "或者这里也行吧", 0, 55, 5);
			p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 2048.0f, 1.0f);
		}
		this.state = BingoGameState.RUNNING;
		scoreboard.getObjective("bingo").unregister();
		for (BingoPlayer player : players) {
			// 初始化
			player.showScoreboard();
		}
		eventTasks.add(new BukkitRunnable() {
			@Override
			public void run() {
				updateScoreboard();
				if (state == BingoGameState.STOPPED) {
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 0L, 5L));
	}

	public void completeBingo(BingoPlayer player) {
		// TODO 当一个玩家完成Bingo后 (注：Config中有设定当多少玩家完成后结束)
	}

	public void stop() {
		for (BukkitTask task : eventTasks) {
			task.cancel();
		}
		for (BingoPlayer bp : players) {
			bp.clearScoreboard();
		}
	}
}
