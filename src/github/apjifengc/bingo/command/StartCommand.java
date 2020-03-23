package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.exception.BadTaskException;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.command.*;

public class StartCommand {
	void onStartCommand(CommandSender sender, Bingo plugin) {
		if (sender.hasPermission("bingo.admin.start")) {
			if (!plugin.hasBingoGame()) {
				BingoGame game = new BingoGame(plugin);
				try {
					game.generateTasks();
				} catch (BadTaskException e) {
					e.printStackTrace();
					sender.sendMessage(Message.get("prefix") + Message.get("commands.start.unable-start") + e.getMessage());
					return;
				}
				plugin.setCurrentGame(game);
				Bukkit.broadcastMessage(
						Message.get("title")+ Message.get("commands.start.success"));
			} else {
				sender.sendMessage(Message.get("prefix") + Message.get("commands.start.already-running"));
			}
		} else {
			sender.sendMessage(Message.get("prefix") + Message.get("commands.no-permission"));
		}
	}
}
