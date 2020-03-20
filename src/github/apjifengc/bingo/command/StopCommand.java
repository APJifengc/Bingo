package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class StopCommand {
	void onStopCommand(CommandSender sender, Bingo plugin) {
		if (sender.hasPermission("bingo.admin.stop")) {
			if (plugin.hasBingoGame()) {
				plugin.setCurrentGame(null);
				Bukkit.broadcastMessage(
						Message.getMessage("title") + "\n" + Message.getMessage("commands.stop.message"));
			} else {
				sender.sendMessage(Message.getMessage("prefix") + "§cThere is no game running!");
			}
		} else {
			sender.sendMessage(Message.getMessage("prefix") + "§cYou don't have the permission for this command.");
		}
	}
}
