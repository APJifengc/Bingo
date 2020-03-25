package github.apjifengc.bingo.listener;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.command.GuiCommand;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.game.BingoGameState;
import github.apjifengc.bingo.game.BingoPlayer;
import github.apjifengc.bingo.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class OtherListener implements Listener {

	private final Bingo plugin;

	public OtherListener(Bingo plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (plugin.hasBingoGame() && plugin.getCurrentGame().getPlayer(player) != null) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.WAITING) {
				game.removePlayer(player);
			} else if (game.getState() == BingoGameState.RUNNING) {
				player.getInventory().setItem(8, new ItemStack(Material.AIR));
			}
			game.getBossbar().removePlayer(player);
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			Player player = event.getPlayer();
			BingoPlayer bplayer = game.getPlayer(player);
			if (game.getState() == BingoGameState.RUNNING && bplayer != null) {
				bplayer.updatePlayer();
				bplayer.giveGuiItem();
				player.setScoreboard(bplayer.getScoreboard());
				game.getBossbar().addPlayer(player);
				player.sendMessage(Message.get("chat.back"));
			}
		}
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.RUNNING) {
				BingoPlayer player = game.getPlayer(event.getPlayer());
				if (player != null && event.getAction() == Action.RIGHT_CLICK_AIR
						|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
						new GuiCommand().onGuiCommand(event.getPlayer(), plugin);
					}
				}
			}
		}
	}

	@EventHandler
	void onPlayerRespawn(PlayerRespawnEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.RUNNING) {
				BingoPlayer player = game.getPlayer(event.getPlayer());
				if (player != null) {
					player.giveGuiItem();
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	void onPlayerDropItem(PlayerDropItemEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.RUNNING) {
				BingoPlayer player = game.getPlayer(event.getPlayer());
				if (player != null) {
					if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

}
