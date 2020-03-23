package github.apjifengc.bingo.listener;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.game.BingoGameState;
import github.apjifengc.bingo.game.BingoPlayer;
import github.apjifengc.bingo.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OtherListener implements Listener {

	private final Bingo plugin;

	public OtherListener(Bingo plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.WAITING) {
				game.removePlayer(player);
			}
		}
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			Player player = event.getPlayer();
			BingoPlayer bplayer = game.getPlayer(player);
			if (game.getState() == BingoGameState.RUNNING && bplayer != null) {
				player.setScoreboard(bplayer.getScoreboard());
				player.sendMessage(Message.get("chat.back"));
			}
		}
	}

}
