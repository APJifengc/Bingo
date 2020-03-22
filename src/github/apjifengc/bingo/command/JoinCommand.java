package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.Configs;
import github.apjifengc.bingo.util.Msg;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand {
	void onJoinCommand(CommandSender sender, Bingo plugin) {
		if (sender instanceof Player) {
			if (sender.hasPermission("bingo.use.join")) {
				if (plugin.hasBingoGame()) {
					if (plugin.getCurrentGame().getPlayer((Player) sender) == null) {
						if (plugin.getCurrentGame().getPlayers().size()== Configs.getMainCfg().getInt("room.max-player")){
							plugin.getCurrentGame().addPlayer((Player) sender);
							Bukkit.broadcastMessage(
									Msg.get("title") + "\n" + Msg.get("commands.join.success",
											String.valueOf(plugin.getCurrentGame().getPlayers().size()), String.valueOf(Configs.getMainCfg().getInt("room.max-player"))));
						} else {
							sender.sendMessage(Msg.get("prefix") + Msg.get("commands.join.full-players"));
						}
					} else {
						sender.sendMessage(Msg.get("prefix") + Msg.get("commands.join.already-in"));
					}
				} else {
					sender.sendMessage(Msg.get("prefix") + Msg.get("commands.join.no-game"));
				}
			} else {
				sender.sendMessage(Msg.get("prefix") + Msg.get("commands.no-permission"));
			}
		} else {
			sender.sendMessage(Msg.get("prefix")
					+ Msg.get("commands.no-console"));
		}
	}
}
