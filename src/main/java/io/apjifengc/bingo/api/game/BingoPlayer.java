package io.apjifengc.bingo.api.game;

import io.apjifengc.bingo.api.event.player.BingoPlayerFinishTaskEvent;
import io.apjifengc.bingo.api.game.task.BingoTask;
import io.apjifengc.bingo.inventory.BingoGuiInventory;
import io.apjifengc.bingo.api.util.BingoUtil;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Message;
import io.apjifengc.bingo.util.TaskUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a player in a Bingo game.
 *
 * @author Milkory
 */
public class BingoPlayer {

    /** The original {@link Player} instance. */
    @Getter Player player;

    /** The {@link BingoGame} they belong to. */
    @Getter BingoGame game;

    /** The scoreboard to be shown to them. */
    @Getter Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    /** Their task finishing status. */
    boolean[] taskStatus = new boolean[25];

    public BingoPlayer(Player player, BingoGame game) {
        this.player = player;
        this.game = game;
    }

    /** Update the {@link #player} field. */
    public boolean updatePlayer() {
        Player p = Bukkit.getPlayer(player.getUniqueId());
        if (player == null || p == null) {
            return false;
        }
        player = p;
        return true;
    }

    /**
     * Check if the player finished a task.
     *
     * @param index The index of the task to be checked.
     * @return Finished or not.
     */
    public boolean hasFinished(int index) {
        return taskStatus[index];
    }

    /**
     * Check if the player finished a task.
     *
     * @param task The task instance to be checked.
     * @return Finished or not.
     */
    public boolean hasFinished(BingoTask task) {
        return taskStatus[game.getBoard().indexOf(task)];
    }

    /**
     * Make the player finish a task.
     *
     * @param index The index of the task to be finished.
     */
    public void finishTask(int index) {
        this.finishTask(game.getBoard().get(index));
    }

    /**
     * Make the player finish a task.
     *
     * @param task The task instance to be finished.
     */
    public void finishTask(BingoTask task) {
        var index = game.getBoard().indexOf(task);

        if (BingoUtil.callEvent(new BingoPlayerFinishTaskEvent(this, task, index))) {
            taskStatus[index] = true;

            if (Config.getMain().getBoolean("chat.complete-task-show")) {
                Bukkit.spigot().broadcast(Message.getComponents("chat.task", this.getPlayer().getName(), TaskUtil.getTaskComponent(task)));
            }
            this.updateScoreboard();
            Player p = this.getPlayer();
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2048.0f, 1.0f);
            p.spawnParticle(Particle.VILLAGER_HAPPY, p.getLocation().add(0, 0.5, 0), 50, 0.3, 0.3, 0.3);
            if (checkBingo(task)) {
                game.completeBingo(this, task);
            }
        }
    }

    /**
     * Get the amount of the player's finished tasks.
     *
     * @return The amount.
     */
    public int getFinishedCount() {
        int i = 0;
        for (boolean b : taskStatus) {
            if (b) {
                i++;
            }
        }
        return i;
    }

    /**
     * Check if the player has finished Bingo.
     *
     * @return Finished or not.
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
     * Check if the player has finished Bingo, by only checking a task around.
     *
     * @param index The index of the task.
     * @return Finished or not.
     */
    public boolean checkBingo(int index) {
        if (index % 6 == 0) {
            if (taskStatus[0] && taskStatus[6] && taskStatus[12] && taskStatus[18] && taskStatus[24])
                return true;
        } else if (index % 4 == 0 && index != 0) {
            if (taskStatus[4] && taskStatus[8] && taskStatus[12] && taskStatus[16] && taskStatus[20])
                return true;
        }
        int row = BingoUtil.getBoardX(index);
        if (taskStatus[row] && taskStatus[row + 5] && taskStatus[row + 10] && taskStatus[row + 15]
                && taskStatus[row + 20])
            return true;
        int col = BingoUtil.getBoardYFirst(BingoUtil.getBoardY(index));
        return taskStatus[col] && taskStatus[col + 1] && taskStatus[col + 2] && taskStatus[col + 3] && taskStatus[col + 4];
    }

    /**
     * Check if the player has finished Bingo, by only checking a task around.
     *
     * @param task The task instance.
     * @return Finished or not.
     */
    public boolean checkBingo(BingoTask task) {
        int index = game.getBoard().indexOf(task);
        if (index != -1) {
            return checkBingo(index);
        }
        return false;
    }

    /**
     * Generate the player's Bingo GUI.
     *
     * @return The player's Bingo GUI.
     */
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(new BingoGuiInventory(), 54, Message.get("gui.title"));
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
        im.setLore(Arrays.asList(Message.get("gui.goal-lore").split("\n")));
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
                is = game.getBoard().get(i).getShownItem();
            }
            inv.setItem(BingoUtil.getBoardX(i) + BingoUtil.getBoardY(i) * 9 + 2, is);
        }
        return inv;
    }

    /** Show their scoreboard. */
    public void showScoreboard() {
        Objective obj;
        try {
            obj = scoreboard.registerNewObjective("bingoInfo", "dummy", Message.get("scoreboard.in-game.title"));
        } catch (Exception ignored) {
            obj = scoreboard.getObjective("bingoInfo");
        }
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
        updateScoreboard();
    }

    public void clearScoreboard() {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    /** Update their scoreboard */
    public void updateScoreboard() {
        int i = 10;
        List<String> stringList = new ArrayList<String>();
        scoreboard.getEntries().forEach((s) -> scoreboard.resetScores(s));
        Objective obj = scoreboard.getObjective("bingoInfo");
        String[] tasksString = {"", "", "", "", ""};
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

    /** Give the item to the player which can open the GUI by right click. */
    public void giveGuiItem() {
        ItemStack is = new ItemStack(Material.PAPER);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(Message.get("item.goal.name"));
        im.setLore(Arrays.asList(Message.get("item.goal.lore").split("\n")));
        // 消失诅咒
        im.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        is.setItemMeta(im);
        player.getInventory().setItem(8, is);
    }

}