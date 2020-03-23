package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoPlayer;
import github.apjifengc.bingo.util.Message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand {
	void onLeaveCommand(CommandSender sender, Bingo plugin) {
		if (sender instanceof Player) {
			if (sender.hasPermission("bingo.use.leave")) {
				if (plugin.hasBingoGame()) {
					BingoPlayer bp = plugin.getCurrentGame().getPlayer((Player) sender);
					if (bp != null) {
						plugin.getCurrentGame().removePlayer((Player) sender);
						sender.sendMessage(Message.get("prefix") + Message.get("commands.leave.success"));
					} else {
						sender.sendMessage(Message.get("prefix") + Message.get("commands.leave.not-in"));
					}
				} else {
					sender.sendMessage(Message.get("prefix") + Message.get("commands.leave.no-game"));
				}
			} else {
				sender.sendMessage(Message.get("prefix") + Message.get("commands.no-permission"));
			}
		} else {
			sender.sendMessage(Message.get("prefix") + Message.get("commands.no-console"));
		}
	}
}
