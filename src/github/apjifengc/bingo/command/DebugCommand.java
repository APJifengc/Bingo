package github.apjifengc.bingo.command;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import github.apjifengc.bingo.exception.BadTaskException;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.game.BingoTask;

public class DebugCommand {

	void onDebugCommand(CommandSender sender, String[] args) {
		BingoGame game = new BingoGame();
		try {
			game.generateTasks();
		} catch (IOException | InvalidConfigurationException | BadTaskException e) {
			e.printStackTrace();
		}
		for (BingoTask task : game.getTasks()) {
			Bukkit.broadcastMessage(task.getShowItem().toString());
		}
	}

}
