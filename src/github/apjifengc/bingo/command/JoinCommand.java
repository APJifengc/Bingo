package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand {
	void onJoinCommand(CommandSender sender, Bingo plugin) {
		if (sender instanceof Player) {
			if (sender.hasPermission("bingo.use.join")) {
				if (plugin.hasBingoGame()) {
					if (plugin.getCurrentGame().getPlayer((Player) sender) == null) {
						plugin.getCurrentGame().addPlayer((Player) sender);
						Bukkit.broadcastMessage(
								Message.getMessage("title") + "\n" + Message.getMessage("commands.join.success",
										String.valueOf(plugin.getCurrentGame().getPlayers().size()), "总人数，APJ还没做"));
					} else {
						sender.sendMessage(Message.getMessage("prefix") + Message.getMessage("commands.join.already-in"));
					}
				} else {
					sender.sendMessage(Message.getMessage("prefix") + Message.getMessage("commands.join.no-game"));
				}
			} else {
				sender.sendMessage(Message.getMessage("prefix") + Message.getMessage("commands.no-permission"));
			}
		} else {
			sender.sendMessage(Message.getMessage("prefix")
					+ Message.getMessage("commands.no-console"));
		}
	}
}
