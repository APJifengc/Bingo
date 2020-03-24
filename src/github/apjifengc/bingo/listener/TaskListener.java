package github.apjifengc.bingo.listener;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.game.BingoGameState;
import github.apjifengc.bingo.game.BingoPlayer;
import github.apjifengc.bingo.game.BingoTask;
import github.apjifengc.bingo.game.tasks.BingoItemTask;
import github.apjifengc.bingo.util.BingoUtil;
import github.apjifengc.bingo.util.Configs;
import github.apjifengc.bingo.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TaskListener implements Listener {
	private final Bingo plugin;

	public TaskListener(Bingo plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	void onPickupItem(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player) {
			if (plugin.hasBingoGame()) {
				BingoGame game = plugin.getCurrentGame();
				if (game.getState() == BingoGameState.RUNNING) {
					BingoPlayer player = game.getPlayer((Player) event.getEntity());
					if (player != null) {
						getItem(player, event.getItem().getItemStack());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	void onClickInventory(InventoryClickEvent event) {
		if (event.getClickedInventory() != null
				&& (event.getClick().isLeftClick() || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD
						|| event.getAction() == InventoryAction.HOTBAR_SWAP)) {
			if (event.getRawSlot() == event.getSlot() && event.getResult() == Result.ALLOW
					&& event.getWhoClicked() instanceof Player) {
				if (plugin.hasBingoGame()) {
					BingoGame game = plugin.getCurrentGame();
					if (game.getState() == BingoGameState.RUNNING) {
						BingoPlayer player = game.getPlayer((Player) event.getWhoClicked());
						if (player != null) {
							getItem(player, event.getInventory().getItem(event.getRawSlot()));
						}
					}
				}
			}
		}
	}

	void getItem(BingoPlayer player, ItemStack is) {
		if (is.getType() == null) {
			return;
		}
		BingoGame game = plugin.getCurrentGame();
		boolean finished = false;
		boolean win = false;
		for (int i = 0; i < 25; i++) {
			BingoTask tas = game.getTasks().get(i);
			if (tas instanceof BingoItemTask) {
				BingoItemTask task = (BingoItemTask) tas;
				if (task.getTarget().getType() == is.getType() && !player.hasFinished(i)) {
					player.finishTask(i);
					win = player.checkBingo(i);
					finished = true;
				}
			}
		}
		if (finished) {
			if (Configs.getMainCfg().getBoolean("chat.complete-task-show")) {
				Bukkit.broadcastMessage(
						Message.get("chat.task", player.getPlayer().getName(), BingoUtil.getItemName(is)));
			}
			player.updateScoreboard();
			Player p = player.getPlayer();
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2048.0f, 1.0f);
			p.spawnParticle(Particle.VILLAGER_HAPPY, p.getLocation().add(0, 0.5, 0), 50, 0.3, 0.3, 0.3);
		}
		if (win) {
			Bukkit.broadcastMessage(player.getPlayer().getName() + " 赢了，太牛逼了！");
		}
	}

}
