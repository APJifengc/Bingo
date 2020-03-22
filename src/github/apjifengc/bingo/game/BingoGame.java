package github.apjifengc.bingo.game;

import java.io.IOException;
import java.util.*;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.Configs;
import github.apjifengc.bingo.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

	int startTimer;

	@Getter
	BingoGameState state;

	Map<Integer, Integer> TimeMap = new HashMap<Integer, Integer>();

	@Getter
	@Setter
	List<BukkitTask> taskList = new ArrayList<BukkitTask>();

	Scoreboard scoreboard;



	Bingo plugin;
	/**
	 * 为这个 Bingo 游戏添加一个玩家。
	 * 
	 * @param player
	 */
	public void addPlayer(Player player) {
		players.add(new BingoPlayer(player, this));
		for(BingoPlayer p : players){
			p.getPlayer().sendMessage(Msg.get("chat.join",
					player.getName(),
					String.valueOf(players.size()),
					String.valueOf(Configs.getMainCfg().getInt("room.max-player"))));
		}
		updateStartTime();
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
			for(BingoPlayer p : players){
				p.getPlayer().sendMessage(Msg.get("chat.leave",
						player.getName(),
						String.valueOf(players.size()),
						String.valueOf(Configs.getMainCfg().getInt("room.max-player"))));

			}
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
	public void generateTasks() throws BadTaskException {
		List<String> items = Configs.getTaskCfg().getStringList("items");
		// 任务数少于25个，不足一场游戏时抛出 BadTaskException
		if (items.size() < 25) {
			throw new BadTaskException(Msg.get("errors.bad-task.number-less-than-25"));
		}
		Random random = new Random();
		for (int i = 0; i < 25; i++) {
			int index = random.nextInt(items.size());
			Material m = Material.getMaterial(items.get(index));
			if (m != null) {
				tasks.add(new BingoItemTask(new ItemStack(m)));
				items.remove(index);
			} else {
				throw new BadTaskException(Msg.get("errors.bad-task.cant-solve",items.get(index)));
			}
		}
	}
	public BingoGame (Bingo plugin) {
		this.plugin = plugin;
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("bingoStartTimer","dummy", Msg.get("scoreboard.start-timer.title"));
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		List<String> TimeList;
		this.state = BingoGameState.WAITING;
		startTimer = -1;
		try {
			TimeList = (List<String>) Configs.getMainCfg().getList("room.start-time");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		for (String str : TimeList){
			String[] strings;
			strings = str.split("\\|");
			try {
				TimeMap.put(Integer.parseInt(strings[0]),Integer.parseInt(strings[1]));
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		//初始化
		scoreboard.registerNewObjective("bingoInfo","dummy", Msg.get("scoreboard.start-timer.title"));
		taskList.add(
				new BukkitRunnable() {
					@Override
					public void run() {
						if(startTimer!=-1){
							startTimer=startTimer-1;
						}
						if(startTimer==0){
							this.cancel();
							start();
						}

						updateScoreboard();
					}
				}.runTaskTimer(plugin,0L,20L)
		);

	}

	public void updateScoreboard() {
		String[] strings = new String[0];
		Objective objective = null;
		int i = 10;
		List<String> stringList = new ArrayList<String>();
		if (this.state == BingoGameState.WAITING) {
			scoreboard.getObjective("bingoInfo").unregister();
			String timeString;
			if (startTimer==-1) {
				timeString = Msg.get("scoreboard.start-timer.wait-for-people");
			} else {
				timeString = Msg.get("scoreboard.start-timer.starting-in",String.valueOf(startTimer));
			}

			objective = scoreboard.registerNewObjective("bingoInfo","dummy", Msg.get("scoreboard.start-timer.title"));
			strings = Msg.get("scoreboard.start-timer.main", String.valueOf(this.players.size()), String.valueOf(Configs.getMainCfg().getInt("room.max-player")),timeString).split("\n");
			for(String str : strings){
				i--;
				if(stringList.contains(str)){
					stringList.add(str+"§1");
					Score score = objective.getScore(str+"§1");
					score.setScore(i);
				} else {
					stringList.add(str);
					Score score = objective.getScore(str);
					score.setScore(i);
				}
			}
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			for(BingoPlayer player : players) {
				player.getPlayer().setScoreboard(scoreboard);
			}
		} else if (this.state == BingoGameState.RUNNING) {
			for(BingoPlayer player : players) {
				Scoreboard playerScoreboard = player.getScoreboard();
				Objects.requireNonNull(playerScoreboard.getObjective("bingoInfo")).unregister();
				objective = playerScoreboard.registerNewObjective("bingoInfo","dummy", Msg.get("scoreboard.in-game.title"));
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);
				String[] tasksString = {"","","","",""};
				for(int k=1;k<=5;k++) {
					for(int j=1;j<=5;j++) {
						tasksString[k-1]=tasksString[k-1]+(
								player.hasFinished(5*k+j-6) ?
								Msg.get("scoreboard.in-game.completed"):
								Msg.get("scoreboard.in-game.uncompleted")
						);
					}
				}
				strings = Msg.get("scoreboard.in-game.main",
						tasksString[0],
						tasksString[1],
						tasksString[2],
						tasksString[3],
						tasksString[4]
				).split("\n");
				for(String str : strings){
					i--;
					if(stringList.contains(str)){
						stringList.add(str+"§1");
						Score score = objective.getScore(str+"§1");
						score.setScore(i);
					} else {
						stringList.add(str);
						Score score = objective.getScore(str);
						score.setScore(i);
					}
				}
				player.getPlayer().setScoreboard(playerScoreboard);
			}
		} else if (this.state == BingoGameState.STOPPED) {
			//TODO 游戏结束的计分板 显示胜利玩家
		}
	}

	public void updateStartTime() {
		for(int i=1;i<=players.size();i++){
			if (TimeMap.containsKey(i)){
				if(startTimer>TimeMap.get(i) || startTimer==-1){
					startTimer=TimeMap.get(i);
				}
			}
		}
		if(players.size()<Configs.getMainCfg().getInt("room.min-player")){
			startTimer=-1;
		}
		updateScoreboard();
	}

	public void start() {
		this.state = BingoGameState.RUNNING;
		for(BingoPlayer player : players){
			//初始化
			player.getScoreboard().registerNewObjective("bingoInfo","dummy", Msg.get("scoreboard.in-game.title"));
		}
		taskList.add(new BukkitRunnable() {
			@Override
			public void run() {
				updateScoreboard();
				//TODO 检测游戏是否结束 是则将state转为STOPPED
			}
		}.runTaskTimer(plugin,0L,5L));
	}

	public void completeBingo(BingoPlayer player) {
		//TODO 当一个玩家完成Bingo后 (注：Config中有设定当多少玩家完成后结束)
	}

	public void stop() {
		//TODO 游戏结束后代码
	}
}
