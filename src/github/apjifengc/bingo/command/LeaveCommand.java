package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoGameState;
import github.apjifengc.bingo.game.BingoPlayer;
import github.apjifengc.bingo.util.Configs;
import github.apjifengc.bingo.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand {
	void onLeaveCommand(CommandSender sender, Bingo plugin) {
		if (sender instanceof Player) {
			if (sender.hasPermission("bingo.use.leave")) {
				if (plugin.hasBingoGame()) {
					BingoPlayer bp = plugin.getCurrentGame().getPlayer((Player) sender);
					if (bp != null) {
						if (plugin.getCurrentGame().getState() != BingoGameState.LOADING) {
							sender.sendMessage(Message.get("prefix") + Message.get("commands.leave.success"));
							plugin.getCurrentGame().removePlayer((Player) sender);
							plugin.getMultiverseCore().getSafeTTeleporter().safelyTeleport(Bukkit.getConsoleSender(),
									(Player) sender, plugin.getMultiverseCore().getDestFactory()
											.getDestination(Configs.getMainCfg().getString("room.main-world")));
						} else {
							sender.sendMessage(Message.get("prefix") + Message.get("commands.leave.game-loading"));
						}
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
