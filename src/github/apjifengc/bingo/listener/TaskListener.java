package github.apjifengc.bingo.listener;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.game.BingoGameState;
import github.apjifengc.bingo.game.BingoPlayer;
import github.apjifengc.bingo.game.BingoTask;
import github.apjifengc.bingo.game.tasks.BingoItemTask;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

public class TaskListener implements Listener {
	private final Bingo plugin;

	public TaskListener(Bingo plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
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
		if (event.getClickedInventory() != null && event.getResult() == Result.ALLOW
				&& event.getWhoClicked() instanceof Player) {
			if (plugin.hasBingoGame()) {
				BingoGame game = plugin.getCurrentGame();
				if (game.getState() == BingoGameState.RUNNING) {
					if (event.getRawSlot() == event.getSlot()) {
						BingoPlayer player = game.getPlayer((Player) event.getWhoClicked());
						if (player != null && event.getInventory().getItem(event.getRawSlot()) != null) {
							getItem(player, event.getInventory().getItem(event.getRawSlot()));
						}
					} else if (game.getPlayer((Player) event.getWhoClicked()) != null
							&& event.getClickedInventory().getType() == InventoryType.PLAYER
							&& (event.getSlot() == 8 || event.getHotbarButton() == 8)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	void onUseBucket(PlayerBucketFillEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.RUNNING) {
				BingoPlayer player = game.getPlayer(event.getPlayer());
				if (player != null) {
					getItem(player, event.getItemStack());
				}
			}
		}
	}

	void getItem(BingoPlayer player, ItemStack is) {
		if (is.getType() == null) {
			return;
		}
		BingoGame game = plugin.getCurrentGame();
		for (int i = 0; i < 25; i++) {
			BingoTask task = game.getTasks().get(i);
			if (task instanceof BingoItemTask) {
				BingoItemTask itemTask = (BingoItemTask) task;
				if (itemTask.getTarget().getType() == is.getType() && !player.hasFinished(i)) {
					player.finishTask(i);
				}
			}
		}
	}

}
