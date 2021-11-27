package io.apjifengc.bingo.game;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.exception.BadTaskException;
import io.apjifengc.bingo.game.task.BingoTask;
import io.apjifengc.bingo.util.BingoUtil;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Message;
import io.apjifengc.bingo.util.TeleportUtil;
import io.apjifengc.bingo.world.SchematicManager;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a Bingo game.
 *
 * @author Milkory
 */

public class BingoGame {

    @Getter List<BingoTask> board = new ArrayList<>(25);
    @Getter ArrayList<BingoPlayer> players = new ArrayList<>();
    @Getter ArrayList<BingoPlayer> winners = new ArrayList<>();

    /*
     * 通过数字 id 区分各个 BukkitTask 0.世界生成器 1. 开始游戏倒计时 2. PVP 保护 3. 游戏结束倒计时 4.游戏结束延迟 5.
     * Bossbar更新器
     */
    Map<Integer, BukkitTask> eventTasks = new HashMap<>();

    int timer, pvpTimer, endTimer;

    @Getter State state;

    Map<Integer, Integer> TimeMap = new HashMap<>();

    @Getter Scoreboard scoreboard;

    Bingo plugin;

    @Getter BossBar bossbar = Bukkit.createBossBar(Message.get("bossbar.normal"), BarColor.YELLOW, BarStyle.SEGMENTED_10);

    /**
     * 为这个 Bingo 游戏添加一个玩家。
     *
     * @param player
     */
    public void addPlayer(Player player) {
        BingoPlayer p = new BingoPlayer(player, this);
        players.add(p);
        Message.sendTo(players, Message.get("chat.join", player.getName(), players.size(),
                Config.getMain().getInt("room.max-player")));
        if (state == State.WAITING) {
            updateStartTime();
            player.setScoreboard(scoreboard);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(new Location(Bukkit.getWorld(Config.getMain().getString("room.world-name")), 0, 200, 0));
        } else if (state == State.RUNNING) {
            p.showScoreboard();
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            p.giveGuiItem();
            bossbar.addPlayer(player);
            TeleportUtil.safeTeleport(player, Bukkit.getWorld(Config.getMain().getString("room.world-name")), 0, 0);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            player.getActivePotionEffects().forEach((s) -> player.removePotionEffect(s.getType()));
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
            bp.getInventory().clear();
            bp.clearScoreboard();
            bossbar.removePlayer(player);
            players.remove(bp);
            Message.sendTo(players, Message.get("chat.leave", player.getName(), players.size(),
                    Config.getMain().getInt("room.max-player")));
            if (state == State.WAITING) {
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
        if (player == null) return null;
        for (BingoPlayer bp : players) {
            if (bp.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return bp;
            }
        }
        return null;
    }

    /** Generate tasks of the Bingo game board. */
    public void generateTasks() throws BadTaskException {
        this.board = BingoTaskManager.getInstance().generateTasks();
    }

    public BingoGame(Bingo plugin) {
        this.plugin = plugin;
        // Start the timer.
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreboard.registerNewObjective("bingo", "dummy", Message.get("scoreboard.start-timer.title"))
                .setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboard.registerNewObjective("bingo-end", "dummy", Message.get("scoreboard.end.title"));
        List<String> TimeList = Config.getMain().getStringList("room.start-time");
        this.state = State.WAITING;
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
        if (this.state == State.WAITING || state == State.LOADING) {
            Objective obj = scoreboard.getObjective("bingo");
            scoreboard.getEntries().forEach((s) -> scoreboard.resetScores(s));
            String timeString;
            if (timer == -1) {
                timeString = Message.get("scoreboard.start-timer.wait-for-people");
            } else if (state == State.LOADING) {
                timeString = Message.get("chat.wait-for-world");
            } else {
                timeString = Message.get("scoreboard.start-timer.starting-in", timer);
            }
            String[] strs = Message.get("scoreboard.start-timer.main", players.size(),
                    Config.getMain().getInt("room.max-player"), timeString).split("\n");
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
                    p.sendTitle("§6" + timer, "", 0, 50, 10);
                }
            } else if (timer == 1) {
                for (BingoPlayer bp : players) {
                    Player p = bp.getPlayer();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2048.0f, 1.0f);
                    p.sendTitle("§4" + timer, "", 0, 50, 10);
                }
            }
        } else if (this.state == State.STOPPED) {
            players.forEach((p) -> p.getPlayer().setScoreboard(scoreboard));
            Objective endObjective = scoreboard.getObjective("bingo-end");
            int order = 0;
            i = 10;
            endObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            List<String> winnerText = new ArrayList<String>();
            for (BingoPlayer winner : winners) {
                order++;
                winnerText.add(Message.get("scoreboard.end.player", order, winner.getPlayer().getName()));
            }
            String[] strs = Message.get("scoreboard.end.main", String.join("\n", winnerText)).split("\n");
            for (String str : strs) {
                i--;
                order++;
                Score score = endObjective.getScore(str);
                score.setScore(i);
            }
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
        if (timer != -1 && players.size() < Config.getMain().getInt("room.min-player")) {
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
        updateScoreboard();
        SchematicManager.undo();
        for (BingoPlayer bingoPlayer : players) {
            Player player = bingoPlayer.getPlayer();
            if (Config.getMain().getInt("game.random-teleport-range") > 0) {
                TeleportUtil.randomTeleport(player, player.getWorld(), 0, 0,
                        Config.getMain().getInt("game.random-teleport-range"));
            }
            player.setGameMode(GameMode.SURVIVAL);
            player.setBedSpawnLocation(player.getLocation());
            player.resetTitle();
            player.spigot().respawn();
            player.sendMessage(Message.get("chat.world-gened"));
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            player.getActivePotionEffects().forEach((s) -> player.removePotionEffect(s.getType()));
            player.getInventory().clear();
        }
        players.forEach((s) -> bossbar.addPlayer(s.getPlayer()));
        World world = Bukkit.getWorld(Config.getMain().getString("room.world-name"));
        if (Config.getMain().getInt("game.world-border") > 0) {
            world.getWorldBorder().setCenter(0, 0);
            world.getWorldBorder().setSize(Config.getMain().getInt("game.world-border"));
        }
        int pvpTime = Config.getMain().getInt("game.no-pvp");
        if (pvpTime > 0) {
            timer = 0;
            bossbar.setTitle(Message.get("bossbar.pvp-timer", timer));
            bossbar.setProgress(0);
            world.setPVP(false);
            BingoUtil.sendMessage(players, Message.get("chat.pvp-timer", pvpTime));
            pvpTimer = pvpTime;
            eventTasks.put(2, new BukkitRunnable() {

                @Override
                public void run() {
                    resetBossbar();
                    world.setPVP(true);
                    BingoUtil.sendMessage(players, Message.get("chat.pvp-enabled"));
                    for (BingoPlayer bingoPlayer : players) {
                        Player player = bingoPlayer.getPlayer();
                        player.sendTitle(Message.get("title.pvp-enabled-title"), Message.get("title.pvp-enabled-subtitle"),
                                0, 55, 5);
                        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2048.0f, 1.0f);
                    }
                    this.cancel();
                }

            }.runTaskLater(plugin, pvpTime * 20));
            eventTasks.put(5, new BukkitRunnable() {

                @Override
                public void run() {
                    if (state == State.STOPPED) {
                        endBossbar();
                    } else if (pvpTimer > 0 || endTimer > 0) {
                        if (pvpTimer > endTimer) {
                            bossbar.setProgress(1 - (double) pvpTimer / (double) pvpTime);
                            bossbar.setTitle(Message.get("bossbar.pvp-timer", pvpTimer));
                        } else {
                            bossbar.setProgress(
                                    1 - (double) endTimer / (double) Config.getMain().getInt("room.end-time"));
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
        for (BingoPlayer bingoPlayer : players) {
            Player player = bingoPlayer.getPlayer();
            bingoPlayer.giveGuiItem();
            player.sendTitle(Message.get("title.game-start-title"), Message.get("title.game-start-subtitle"), 0, 55, 5);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 2048.0f, 1.0f);
        }
        this.state = State.RUNNING;
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
        if (!winners.contains(player)) {
            winners.add(player);
            Player p = player.getPlayer();
            if (Config.getMain().getBoolean("chat.complete-task-show")) {
                BingoUtil.sendMessage(players,
                        Message.get("chat.win", winners.size(), p.getName(), player.getFinishedCount()));
            }
            p.sendTitle(Message.get("title.win-bingo-title"), Message.get("title.win-bingo-subtitle"), 0, 85, 5);
            p.sendMessage(Message.get("chat.win-tellraw"));
            p.setGameMode(GameMode.SPECTATOR);
            if (winners.size() >= Config.getMain().getInt("room.winner-count") || winners.size() >= players.size()) {
                endGame();
            } else if (winners.size() == 1 && Config.getMain().getInt("room.end-time") > 0) {
                // 启动游戏结束倒计时
                endTimer = Config.getMain().getInt("room.end-time");
                eventTasks.put(3, new BukkitRunnable() {

                    @Override
                    public void run() {
                        endGame();
                    }

                }.runTaskLater(plugin, Config.getMain().getInt("room.end-time") * 20));
            }
        }
    }

    public void endGame() {
        state = State.STOPPED;
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
        for (BingoPlayer bingoPlayer : players) {
            TeleportUtil.safeTeleport(bingoPlayer.getPlayer(), Bukkit.getWorld(Config.getMain().getString("room.main-world")), 0, 0);
            bingoPlayer.getPlayer().getInventory().clear();
            bingoPlayer.clearScoreboard();
            bossbar.removePlayer(bingoPlayer.getPlayer());
        }
        //if (mvWM.getMVWorld(Config.getMain().getString("room.world-name")) != null) {
        //    mvWM.deleteWorld(Config.getMain().getString("room.world-name"), true);
        //}
    }

    public enum State {
        WAITING, LOADING, RUNNING, STOPPED
    }

}
