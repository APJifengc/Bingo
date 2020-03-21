package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.exception.BadTaskException;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;

public class StartCommand {
	void onStartCommand(CommandSender sender, Bingo plugin) {
		if (sender.hasPermission("bingo.admin.start")) {
			if (!plugin.hasBingoGame()) {
				BingoGame game = new BingoGame();
				try {
					game.generateTasks();
				} catch (BadTaskException e) {
					e.printStackTrace();
					sender.sendMessage(Message.getMessage("prefix") + Message.getMessage("commands.start.unable-start") + e.getMessage());
					return;
				}
				plugin.setCurrentGame(game);
				Bukkit.broadcastMessage(
						Message.getMessage("title")+Message.getMessage("commands.start.success"));
			} else {
				sender.sendMessage(Message.getMessage("prefix") + Message.getMessage("commands.start.already-running"));
			}
		} else {
			sender.sendMessage(Message.getMessage("prefix") + Message.getMessage("commands.no-permission"));
		}
	}
}
