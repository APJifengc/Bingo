package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.util.Msg;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class StopCommand {
	void onStopCommand(CommandSender sender, Bingo plugin) {
		if (sender.hasPermission("bingo.admin.stop")) {
			if (plugin.hasBingoGame()) {
				plugin.setCurrentGame(null);
				Bukkit.broadcastMessage(
						Msg.get("title") + "\n" + Msg.get("commands.stop.message"));
			} else {
				sender.sendMessage(Msg.get("prefix") + Msg.get("commands.stop.no-game"));
			}
		} else {
			sender.sendMessage(Msg.get("prefix") + Msg.get("commands.no-permission"));
		}
	}
}
