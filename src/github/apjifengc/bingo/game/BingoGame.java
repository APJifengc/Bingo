package github.apjifengc.bingo.game;

import java.io.IOException;
import java.util.*;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.destination.DestinationFactory;
import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.util.BingoUtil;
import github.apjifengc.bingo.util.Configs;
import github.apjifengc.bingo.util.Message;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sun.istack.internal.Nullable;

import github.apjifengc.bingo.exception.BadTaskException;
import github.apjifengc.bingo.game.tasks.BingoImpossibleTask;
import github.apjifengc.bingo.game.tasks.BingoItemTask;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_14_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_14_R1.PacketPlayInClientCommand.EnumClientCommand;

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

	@Getter
	ArrayList<BingoPlayer> winners = new ArrayList<BingoPlayer>();

	/*
	 * 通过数字 id 区分各个 BukkitTask 0.世界生成器 1. 开始游戏倒计时 2. PVP 保护 3. 游戏结束倒计时 4.游戏结束延迟 5.
	 * Bossbar更新器
	 */
	Map<Integer, BukkitTask> eventTasks = new HashMap<Integer, BukkitTask>();

	int timer, pvpTimer, endTimer;
	@Getter
	BingoGameState state;

	Map<Integer, Integer> TimeMap = new HashMap<Integer, Integer>();

	@Getter
	Scoreboard scoreboard;

	Bingo plugin;

	@Getter
	BossBar bossbar = Bukkit.createBossBar(Message.get("bossbar.normal"), BarColor.YELLOW, BarStyle.SEGMENTED_10);

	MultiverseCore mvCore;
	MVWorldManager mvWM;
	MultiverseWorld world;

	DestinationFactory df;
	MVDestination d;

	/**
	 * 为这个 Bingo 游戏添加一个玩家。
	 * 
	 * @param player
	 */
	public void addPlayer(Player player) {
		BingoPlayer p = new BingoPlayer(player, this);
		players.add(p);
		BingoUtil.sendMessage(players, Message.get("chat.join", player.getName(), players.size(),
				Configs.getMainCfg().getInt("room.max-player")));
		if (state == BingoGameState.WAITING) {
			updateStartTime();
		} else if (state == BingoGameState.RUNNING) {
			p.showScoreboard();
			p.giveGuiItem();
			bossbar.addPlayer(player);
			mvCore.getSafeTTeleporter().safelyTeleport(plugin.getServer().getConsoleSender(), player, d);
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
			bossbar.removePlayer(player);
			players.remove(bp);
			BingoUtil.sendMessage(players, Message.get("chat.leave", player.getName(), players.size(),
					Configs.getMainCfg().getInt("room.max-player")));
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
			String task = items.get(index);
			if (task.equalsIgnoreCase("IMPOSSIBLE")) {
				tasks.add(new BingoImpossibleTask());
			} else {
				Material m = Material.getMaterial(items.get(index));
				if (m != null) {
					tasks.add(new BingoItemTask(new ItemStack(m)));
				} else {
					throw new BadTaskException(Message.get("errors.bad-task.cant-solve", items.get(index)));
				}
			}
			items.remove(index);
		}
	}

	public BingoGame(Bingo plugin) {
		this.plugin = plugin;
		mvCore = plugin.getMultiverseCore();
		mvWM = mvCore.getMVWorldManager();
		df = mvCore.getDestFactory();
		// Start the timer.
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("bingo", "dummy",
				Message.get("scoreboard.start-timer.title"));
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		List<String> TimeList = Configs.getMainCfg().getStringList("room.start-time");
		this.state = BingoGameState.WAITING;
		timer = -1;
		for (String str : TimeList) {
			String[] strings;
			strings = str.split("\\|");
			try {
				TimeMap.put(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		eventTasks.put(1, new BukkitRunnable() {
			@Override
			public void run() {
				if (timer != -1) {
					timer--;
				}
				updateScoreboard();
				if (timer == 0) {
					this.cancel();
					start();
				}
			}
		}.runTaskTimer(plugin, 0L, 20L));

	}

	public void updateScoreboard() {
		int i = 10;
		List<String> stringList = new ArrayList<String>();
		// 如果游戏正在等待玩家加入
		if (this.state == BingoGameState.WAITING || state == BingoGameState.LOADING) {
			Objective obj = scoreboard.getObjective("bingo");
			scoreboard.getEntries().forEach((s) -> scoreboard.resetScores(s));
			String timeString;
			if (timer == -1) {
				timeString = Message.get("scoreboard.start-timer.wait-for-people");
			} else if (state == BingoGameState.LOADING) {
				timeString = Message.get("chat.wait-for-world");
			} else {
				timeString = Message.get("scoreboard.start-timer.starting-in", timer);
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
			if (timer > 1 && timer < 5) {
				for (BingoPlayer bp : players) {
					Player p = bp.getPlayer();
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2048.0f, 1.0f);
					p.sendTitle("§6" + String.valueOf(timer), "", 0, 50, 10);
				}
			} else if (timer == 1) {
				for (BingoPlayer bp : players) {
					Player p = bp.getPlayer();
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2048.0f, 1.0f);
					p.sendTitle("§4" + String.valueOf(timer), "", 0, 50, 10);
				}
			}
		} else if (this.state == BingoGameState.STOPPED) {
			// TODO 游戏结束的计分板 显示胜利玩家
		}
	}

	public void updateStartTime() {
		for (int i = 1; i <= players.size(); i++) {
			if (TimeMap.containsKey(i)) {
				if (timer > TimeMap.get(i) || timer == -1) {
					timer = TimeMap.get(i);
				}
			}
		}
		if (timer != -1 && players.size() < Configs.getMainCfg().getInt("room.min-player")) {
			timer = -1;
			for (BingoPlayer bp : players) {
				Player p = bp.getPlayer();
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 2048.0f, 1.0f);
				p.sendTitle("", Message.get("chat.not-enough-player"), 0, 55, 5);
			}
		}
		updateScoreboard();
	}

	public void start() {
		// Gener world.
		for (BingoPlayer player : players) {
			player.getPlayer().sendTitle("", Message.get("chat.wait-for-world"), 0, 400, 5);
		}
		state = BingoGameState.LOADING;
		updateScoreboard();
		Random random = new Random();
		String worldName = Configs.getMainCfg().getString("room.world-name");
		if (mvWM.getMVWorld(worldName) != null) {
			mvWM.deleteWorld(worldName, true);
		}
		mvWM.addWorld(worldName, World.Environment.NORMAL, String.valueOf(random.nextLong()), WorldType.NORMAL, true,
				null);
		world = mvWM.getMVWorld(worldName);
		d = df.getDestination(Configs.getMainCfg().getString("room.world-name"));
		for (BingoPlayer player : players) {
			Player p = player.getPlayer();
			p.resetTitle();
			if (p.isDead()) {
				((CraftPlayer) p).getHandle().playerConnection
						.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
			}
			p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			p.setFoodLevel(20);
			p.sendMessage(Message.get("chat.world-gened"));
			mvCore.getSafeTTeleporter().safelyTeleport(plugin.getServer().getConsoleSender(), p, d);
		}
		players.forEach((s) -> bossbar.addPlayer(s.getPlayer()));
		if (Configs.getMainCfg().getInt("game.world-border") > 0) {
			world.getCBWorld().getWorldBorder().setSize(Configs.getMainCfg().getInt("game.world-border"));
		}
		int pvpTime = Configs.getMainCfg().getInt("game.no-pvp");
		if (pvpTime > 0) {
			timer = 0;
			bossbar.setTitle(Message.get("bossbar.pvp-timer", timer));
			bossbar.setProgress(0);
			world.setPVPMode(false);
			BingoUtil.sendMessage(players, Message.get("chat.pvp-timer", pvpTime));
			pvpTimer = pvpTime;
			eventTasks.put(2, new BukkitRunnable() {

				@Override
				public void run() {
					resetBossbar();
					world.setPVPMode(true);
					BingoUtil.sendMessage(players, Message.get("chat.pvp-enabled"));
					for (BingoPlayer bp : players) {
						Player p = bp.getPlayer();
						p.sendTitle(Message.get("title.pvp-enabled-title"), Message.get("title.pvp-enabled-subtitle"),
								0, 55, 5);
						p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2048.0f, 1.0f);
					}
					this.cancel();
				}

			}.runTaskLater(plugin, pvpTime * 20));
			eventTasks.put(5, new BukkitRunnable() {

				@Override
				public void run() {
					if (state == BingoGameState.STOPPED) {
						endBossbar();
					} else if (pvpTimer > 0 || endTimer > 0) {
						if (pvpTimer > endTimer) {
							bossbar.setProgress(1 - (double) pvpTimer / (double) pvpTime);
							bossbar.setTitle(Message.get("bossbar.pvp-timer", pvpTimer));
						} else {
							bossbar.setProgress(
									1 - (double) endTimer / (double) Configs.getMainCfg().getInt("room.end-time"));
							bossbar.setTitle(Message.get("bossbar.end-timer", endTimer));
						}
						if (pvpTimer > 0) {
							pvpTimer--;
						}
						if (endTimer > 0) {
							endTimer--;
						}
					}
				}

			}.runTaskTimer(plugin, 0, 20));
		}
		// ..
		for (BingoPlayer bp : players) {
			Player p = bp.getPlayer();
			bp.giveGuiItem();
			p.sendTitle(Message.get("title.game-start-title"), Message.get("title.game-start-subtitle"), 0, 55, 5);
			p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 2048.0f, 1.0f);
		}
		this.state = BingoGameState.RUNNING;
		scoreboard.getObjective("bingo").unregister();
		for (BingoPlayer player : players) {
			// 初始化
			player.showScoreboard();
		}
	}

	public void resetBossbar() {
		bossbar.setProgress(1);
		bossbar.setTitle(Message.get("bossbar.normal"));
		bossbar.setColor(BarColor.RED);
		bossbar.setStyle(BarStyle.SOLID);
	}

	public void endBossbar() {
		bossbar.setProgress(1);
		bossbar.setTitle(Message.get("bossbar.gameover"));
		bossbar.setColor(BarColor.PINK);
		bossbar.setStyle(BarStyle.SOLID);
	}

	public void completeBingo(BingoPlayer player) {
		winners.add(player);
		Player p = player.getPlayer();
		BingoUtil.sendMessage(players, Message.get("chat.win", winners.size(), p.getName(), player.getFinishedCount()));
		p.sendTitle(Message.get("title.win-bingo-title"), Message.get("title.win-bingo-subtitle"), 0, 85, 5);
		p.sendMessage(Message.get("chat.win-tellraw"));
		p.setGameMode(GameMode.SPECTATOR);
		if (winners.size() >= Configs.getMainCfg().getInt("room.winner-count") || winners.size() >= players.size()) {
			endGame();
		} else if (winners.size() == 1 && Configs.getMainCfg().getInt("room.end-time") > 0) {
			// 启动游戏结束倒计时
			endTimer = Configs.getMainCfg().getInt("room.end-time");
			eventTasks.put(3, new BukkitRunnable() {

				@Override
				public void run() {
					endGame();
				}

			}.runTaskLater(plugin, Configs.getMainCfg().getInt("room.end-time") * 20));
		}
	}

	public void endGame() {
		state = BingoGameState.STOPPED;
		updateScoreboard();
		if (winners.size() >= 1) {
			players.forEach((p) -> p.getPlayer().sendTitle(Message.get("title.gameover-title"),
					Message.get("title.gameover-subtitle", winners.get(0).getPlayer().getName()), 10, 190, 5));
		} else {
			players.forEach((p) -> p.getPlayer().sendTitle(Message.get("title.gameover-title"),
					Message.get("title.gameover-nowinner-subtitle"), 10, 190, 5));
		}
		players.forEach((p) -> p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT,
				2048.0f, 1.0f));
		eventTasks.put(4, new BukkitRunnable() {

			@Override
			public void run() {
				stop();
				plugin.setCurrentGame(null);
				this.cancel();
			}

		}.runTaskLater(plugin, 200));
	}

	public void stop() {
		eventTasks.forEach((i, s) -> s.cancel());
		for (BingoPlayer bp : players) {
			bp.getPlayer().getInventory().clear();
			bp.clearScoreboard();
			bossbar.removePlayer(bp.getPlayer());
		}
		if (mvWM.getMVWorld(Configs.getMainCfg().getString("room.world-name")) != null) {
			mvWM.deleteWorld(Configs.getMainCfg().getString("room.world-name"), true);
		}
	}
}
