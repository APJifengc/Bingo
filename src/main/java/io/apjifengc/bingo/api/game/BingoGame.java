package io.apjifengc.bingo.api.game;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.api.event.game.BingoGameGenerateTaskEvent;
import io.apjifengc.bingo.api.event.game.BingoGameStateChangeEvent;
import io.apjifengc.bingo.api.event.player.BingoPlayerFinishBingoEvent;
import io.apjifengc.bingo.api.event.player.BingoPlayerLeaveEvent;
import io.apjifengc.bingo.api.event.player.BingoPlayerPreJoinEvent;
import io.apjifengc.bingo.api.exception.BadTaskException;
import io.apjifengc.bingo.api.game.task.BingoTask;
import io.apjifengc.bingo.api.timer.BingoTimer;
import io.apjifengc.bingo.api.timer.BingoTimerManager;
import io.apjifengc.bingo.api.timer.BingoTimerTask;
import io.apjifengc.bingo.api.util.BingoUtil;
import io.apjifengc.bingo.map.TaskMapRenderer;
import io.apjifengc.bingo.util.BungeecordUtil;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Message;
import io.apjifengc.bingo.util.TeleportUtil;
import io.apjifengc.bingo.world.SchematicManager;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
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
@SuppressWarnings("UnusedReturnValue")
public class BingoGame {

    private static final Bingo plugin = Bingo.getInstance();

    @Getter private State state = State.LOADING;
    @Getter private List<BingoTask> board = new ArrayList<>(25);

    @Getter private final ArrayList<BingoPlayer> players = new ArrayList<>();
    @Getter private final ArrayList<BingoPlayer> winners = new ArrayList<>();

    private final TreeMap<Integer, Integer> timeMap = new TreeMap<>();
    private final List<Listener> taskListeners = new ArrayList<>();

    @Getter private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    @Getter private final BossBar bossbar = Bukkit.createBossBar(Message.get("bossbar.normal"), BarColor.YELLOW, BarStyle.SEGMENTED_10);
    @Getter private final World mainWorld;
    @Getter private final World bingoWorld;

    /** The task item for one user. */
    @Getter private ItemStack taskItem;

    @Getter final boolean allowOpenOthersGui;
    @Getter final int randomTeleportRange;

    public BingoGame() {
        this.allowOpenOthersGui = Config.getMain().getBoolean("game.access-to-others-gui");
        this.randomTeleportRange = Config.getMain().getInt("game.random-teleport-range");
        this.mainWorld = Bukkit.getWorld(Objects.requireNonNull(Config.getMain().getString("room.main-world")));
        this.bingoWorld = Bukkit.getWorld(Objects.requireNonNull(Config.getMain().getString("room.world-name")));
        // Start the timer.
        scoreboard.registerNewObjective("bingo", "dummy", Message.get("scoreboard.start-timer.title"))
                .setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboard.registerNewObjective("bingo-end", "dummy", Message.get("scoreboard.end.title"));
        List<String> TimeList = Config.getMain().getStringList("room.start-time");
        changeState(State.WAITING);
        for (String str : TimeList) {
            String[] strings;
            strings = str.split("\\|");
            try {
                timeMap.put(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        BingoTimerManager.startTimer();
        new BingoTimerTask() {
            @Override
            public void run() {
                startGame();
            }

            @Override
            public void tick() {
                updateScoreboard();
            }
        }.id("start-timer").tickInterval(20L).add();
        new BingoTimerTask() {
            @Override public void run() {
                // TODO: I just want to tick it!
            }

            @Override
            public void tick() {
                for (BingoPlayer player : players) {
                    if (state == State.RUNNING) {
                        player.keepScoreboard(player.getScoreboard());
                    } else {
                        player.keepScoreboard(scoreboard);
                    }
                }
            }
        }.id("scoreboard-keeper").tickInterval(2L).time(Long.MAX_VALUE).add();
        BingoTimerManager.startTask("scoreboard-keeper");
    }

    /**
     * Add a player to the game.
     *
     * @param player The player to be joined.
     * @return A {@link BingoPlayer} instance generated by the player, or null if it failed.
     */
    public BingoPlayer addPlayer(Player player) {
        if (BingoUtil.callEvent(new BingoPlayerPreJoinEvent(player, this))) {
            BingoPlayer bingoPlayer = new BingoPlayer(player, this);
            players.add(bingoPlayer);
            Message.sendTo(players, "chat.join", player.getName(), players.size(),
                    Config.getMain().getInt("room.max-player"));
            if (state == State.WAITING || state == State.STARTING) {
                updateStartTime();
                player.setScoreboard(scoreboard);
                player.setGameMode(GameMode.ADVENTURE);
                bingoPlayer.clearPlayer();
                player.teleport(new Location(bingoWorld, 0, 200, 0));
            } else if (state == State.RUNNING) {
                initPlayer(bingoPlayer);
            }
            return bingoPlayer;
        } else return null;
    }

    /**
     * Remove a player from the game.
     *
     * @param player The player to be removed
     * @return Succeed or fail.
     */
    public boolean removePlayer(Player player) {
        BingoPlayer bingoPlayer = getPlayer(player);
        if (bingoPlayer != null) {
            if (BingoUtil.callEvent(new BingoPlayerLeaveEvent(bingoPlayer))) {
                bingoPlayer.getGui().clear();
                bingoPlayer.clearScoreboard();
                bingoPlayer.clearPlayer();
                bingoPlayer.getPlayer().setGameMode(Bukkit.getServer().getDefaultGameMode());
                bossbar.removePlayer(player);
                players.remove(bingoPlayer);
                Message.sendTo(players, "chat.leave", player.getName(), players.size(),
                        Config.getMain().getInt("room.max-player"));
                bingoPlayer.sendBackToLobby();
                if (state == State.WAITING || state == State.STARTING) {
                    updateStartTime();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Get a {@link BingoPlayer} instance according to a player's UUID.
     *
     * @param player The player to be got.
     * @return The specified player or null for not found.
     */
    @Nullable
    public BingoPlayer getPlayer(Player player) {
        if (player == null) return null;
        for (BingoPlayer bingoPlayer : players) {
            if (bingoPlayer.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return bingoPlayer;
            }
        }
        return null;
    }

    /**
     * Get a {@link BingoPlayer} instance according to a player's name.
     *
     * @param player The player to be got.
     * @return The specified player or null for not found.
     */
    @Nullable
    public BingoPlayer getPlayer(String player) {
        return getPlayer(Bukkit.getPlayer(player));
    }

    /**
     * Generate tasks as the game board.
     *
     * @see BingoTaskManager#generateTasks()
     */
    public void generateTasks() throws BadTaskException {
        var result = BingoTaskManager.getInstance().generateTasks();
        Bukkit.getPluginManager().callEvent(new BingoGameGenerateTaskEvent(this, result));
        this.board = result;
        result.forEach(t -> {
            Listener listener = t.getTaskListener();
            taskListeners.add(listener);
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        });
        Material material = Material.valueOf(Config.getMain().getString("display.item-list-material", "FILLED_MAP"));
        taskItem = new ItemStack(material);
        if (material == Material.FILLED_MAP) {
            MapMeta mapMeta = (MapMeta) taskItem.getItemMeta();
            MapView mapView = Bukkit.createMap(mainWorld);
            mapView.getRenderers().forEach(mapView::removeRenderer);
            mapView.addRenderer(new TaskMapRenderer(this));
            mapMeta.setMapView(mapView);
            taskItem.setItemMeta(mapMeta);
        }
        ItemMeta itemMeta = taskItem.getItemMeta();
        itemMeta.setDisplayName(Message.get("item.goal.name"));
        itemMeta.setLore(Arrays.asList(Message.get("item.goal.lore").split("\n")));
        // 消失诅咒
        itemMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        taskItem.setItemMeta(itemMeta);
    }

    /** Update the scoreboard. */
    public void updateScoreboard() {
        int i = 10;
        List<String> stringList = new ArrayList<>();
        // 如果游戏正在等待玩家加入
        if (this.state == State.WAITING || state == State.LOADING || state == State.STARTING) {
            Objective obj = scoreboard.getObjective("bingo");
            scoreboard.getEntries().forEach(scoreboard::resetScores);
            String timeString;
            long timer = BingoTimerManager.getTime("start-timer", true);
            if (!BingoTimerManager.getTask("start-timer").isRunning()) {
                timeString = Message.get("scoreboard.start-timer.wait-for-people");
            } else if (state == State.LOADING) {
                timeString = Message.get("chat.wait-for-world");
            } else {
                timeString = Message.get("scoreboard.start-timer.starting-in", timer / 20L);
            }
            String[] strings = Message.get("scoreboard.start-timer.main", players.size(),
                    Config.getMain().getInt("room.max-player"), timeString).split("\n");
            for (String str : strings) {
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
            switch ((int) timer) {
                case 40:
                case 60:
                case 80:
                case 100:
                    for (BingoPlayer bp : players) {
                        Player p = bp.getPlayer();
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2048.0f, 1.0f);
                        p.sendTitle("§6" + timer / 20L, "", 0, 50, 10);
                    }
                    break;
                case 20:
                    for (BingoPlayer bp : players) {
                        Player p = bp.getPlayer();
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2048.0f, 1.0f);
                        p.sendTitle("§4" + timer / 20L, "", 0, 50, 10);
                    }
                    break;
            }
        } else if (this.state == State.STOPPED) {
            players.forEach((p) -> p.getPlayer().setScoreboard(scoreboard));
            Objective endObjective = scoreboard.getObjective("bingo-end");
            int order = 0;
            endObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            List<String> winnerText = new ArrayList<>();
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

    private Map.Entry<Integer, Integer> lastEntry;

    /** Update the time last to start the game. */
    public void updateStartTime() {
        var entry = timeMap.floorEntry(players.size());
        if (entry == null || players.size() < Config.getMain().getInt("room.min-player")) {
            if (state == State.STARTING) {
                BingoTimerManager.stopTask("start-timer");
                for (BingoPlayer bingoPlayer : players) {
                    Player player = bingoPlayer.getPlayer();
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2048.0f, 1.0f);
                    player.sendTitle("", Message.get("chat.not-enough-player"), 0, 55, 5);
                }
                changeState(State.WAITING);
            }
        } else if (state == State.STARTING) {
            if (entry.getValue() > lastEntry.getValue()) {
                BingoTimerManager.setTime("start-timer", entry.getValue() * 20L, true);
            } else if (entry.getValue() * 20L < BingoTimerManager.getTime("start-timer", true)) {
                BingoTimerManager.setTime("start-timer", entry.getValue() * 20L, true);
            }
        } else if (state == State.WAITING) {
            BingoTimerManager.setTime("start-timer", entry.getValue() * 20L, true);
            BingoTimerManager.startTask("start-timer");
            changeState(State.STARTING);
        }
        lastEntry = entry;
        updateScoreboard();
    }

    /**
     * Init the new player.
     *
     * @param bingoPlayer The new player.
     */
    public void initPlayer(BingoPlayer bingoPlayer) {
        Player player = bingoPlayer.getPlayer();
        teleportToBingoWorld(player);
        player.setGameMode(GameMode.SURVIVAL);
        player.setBedSpawnLocation(player.getLocation(), true);
        player.resetTitle();
        player.spigot().respawn();
        bingoPlayer.clearPlayer();
        Config.getStartkits().forEach(item -> player.getInventory().addItem(item));
        TaskMapRenderer.makeDirty(player);
        bossbar.addPlayer(player);
        bingoPlayer.giveGuiItem();
        bingoPlayer.showScoreboard();
    }

    /**
     * Start the game.
     *
     * @see #endGame()
     * @see #stop()
     */
    public void startGame() {
        updateScoreboard();
        SchematicManager.undo();
        for (BingoPlayer bingoPlayer : players) {
            initPlayer(bingoPlayer);
            Player player = bingoPlayer.getPlayer();
            player.sendMessage(Message.get("chat.world-gened"));
            player.sendTitle(Message.get("title.game-start-title"), Message.get("title.game-start-subtitle"), 0, 55, 5);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 2048.0f, 1.0f);
        }
        if (Config.getMain().getInt("game.world-border") > 0) {
            bingoWorld.getWorldBorder().setCenter(0, 0);
            bingoWorld.getWorldBorder().setSize(Config.getMain().getInt("game.world-border"));
        }
        int pvpTime = Config.getMain().getInt("game.no-pvp");
        BingoTimerManager.resetTimer();
        bingoWorld.setPVP(true);
        bingoWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        bingoWorld.setTime(Config.getMain().getInt("game.start-time"));
        if (pvpTime > 0) {
            bossbar.setTitle(Message.get("bossbar.pvp-timer", 0));
            bossbar.setProgress(0);
            bingoWorld.setPVP(false);
            BingoUtil.sendMessage(players, Message.get("chat.pvp-timer", pvpTime));
            new BingoTimerTask() {
                @Override
                public void run() {
                    resetBossbar();
                    bingoWorld.setPVP(true);
                    BingoUtil.sendMessage(players, Message.get("chat.pvp-enabled"));
                    for (BingoPlayer bingoPlayer : players) {
                        Player player = bingoPlayer.getPlayer();
                        player.sendTitle(Message.get("title.pvp-enabled-title"), Message.get("title.pvp-enabled-subtitle"),
                                0, 55, 5);
                        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2048.0f, 1.0f);
                    }
                }

                @Override
                public void tick() {
                    bossbar.setProgress(1 - (double) this.getRelativeTime() / 20.0d / (double) pvpTime);
                    bossbar.setTitle(Message.get("bossbar.pvp-timer", this.getRelativeTime() / 20L));
                }
            }.id("pvp-timer").time(pvpTime * 20L).tickInterval(20L).start();
        }
        changeState(State.RUNNING);
        scoreboard.getObjective("bingo").unregister();
    }

    /** Reset the bossbar. */
    public void resetBossbar() {
        bossbar.setProgress(1);
        bossbar.setTitle(Message.get("bossbar.normal"));
        bossbar.setColor(BarColor.RED);
        bossbar.setStyle(BarStyle.SOLID);
    }

    /** Turn the bossbar to the end state. */
    public void endBossbar() {
        bossbar.setProgress(1);
        bossbar.setTitle(Message.get("bossbar.gameover"));
        bossbar.setColor(BarColor.PINK);
        bossbar.setStyle(BarStyle.SOLID);
    }

    /** Make a player complete bingo. */
    public void completeBingo(BingoPlayer player, BingoTask lastTask) {
        if (BingoUtil.callEvent(new BingoPlayerFinishBingoEvent(player, lastTask, board.indexOf(lastTask)))) {
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
                    new BingoTimerTask() {
                        @Override
                        public void run() {
                            endGame();
                        }

                        @Override
                        public void tick() {
                            bossbar.setProgress(
                                    1 - (double) this.getRelativeTime() / 20.0d / (double) Config.getMain().getInt("room.end-time"));
                            bossbar.setTitle(Message.get("bossbar.end-timer", this.getRelativeTime() / 20L));
                        }
                    }.id("end-game-timer").time(Config.getMain().getInt("room.end-time") * 20L)
                            .tickInterval(20L).startFromNow();
                }
            }
        }
    }

    /**
     * End the game.
     *
     * @see #startGame()
     * @see #stop()
     */
    public void endGame() {
        endBossbar();
        changeState(State.STOPPED);
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
        new BingoTimerTask() {
            @Override
            public void run() {
                stop();
            }

            @Override public void tick() {
            }
        }.id("stop-game-timer").time(200L).startFromNow();
    }

    public void forceStop() {
        taskListeners.forEach(HandlerList::unregisterAll);
        for (BingoPlayer bingoPlayer : players) {
            bingoPlayer.clearPlayer();
            bingoPlayer.clearScoreboard();
            bingoPlayer.getPlayer().setGameMode(Bukkit.getServer().getDefaultGameMode());
            bossbar.removePlayer(bingoPlayer.getPlayer());
            bingoPlayer.sendBackToLobby();
        }
        plugin.setCurrentGame(null);
        BingoTimerManager.stopTimer();
        BingoTimerManager.clearTasks();
        BingoTimerManager.resetTimer();
    }

    /**
     * Fully stop the game.
     *
     * @see #startGame()
     * @see #endGame()
     */
    public void stop() {
        forceStop();
        if (Config.getMain().getBoolean("server.auto-start-end", false)) {
            if (Config.getMain().getBoolean("server.bungee", false)) {
                Bukkit.getOnlinePlayers().forEach(player ->
                        BungeecordUtil.sendPlayer(player, Config.getMain().getString("server.lobby-server")));
            } else {
                Bukkit.getOnlinePlayers().forEach(player ->
                        player.kickPlayer(Message.get("game-restart")));
            }
            try {
                plugin.startGame();
            } catch (BadTaskException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeState(State after) {
        var before = state;
        state = after;
        BingoUtil.callEvent(new BingoGameStateChangeEvent(this, before, after));
    }

    public void teleportToBingoWorld(Player player) {
        if (randomTeleportRange > 0) {
            TeleportUtil.randomTeleport(player, bingoWorld, 0, 0, randomTeleportRange);
        } else {
            TeleportUtil.safeTeleport(player, bingoWorld, 0, 0);
        }
    }

    /** The state of a game. */
    public enum State {
        WAITING, LOADING, RUNNING, STOPPED, STARTING
    }

}
