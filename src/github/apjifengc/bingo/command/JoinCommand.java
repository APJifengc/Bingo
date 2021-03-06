package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.game.BingoGameState;
import github.apjifengc.bingo.util.Configs;
import github.apjifengc.bingo.util.Message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand {
	void onJoinCommand(CommandSender sender, Bingo plugin) {
		if (sender instanceof Player) {
			if (sender.hasPermission("bingo.use.join")) {
				if (plugin.hasBingoGame()) {
					if (plugin.getCurrentGame().getState() == BingoGameState.RUNNING
							&& !Configs.getMainCfg().getBoolean("room.join-while-game")) {
						sender.sendMessage(Message.get("prefix") + Message.get("commands.join.disallow-join"));
						return;
					}
					if (plugin.getCurrentGame().getPlayer((Player) sender) == null) {
						if (plugin.getCurrentGame().getPlayers().size() < Configs.getMainCfg()
								.getInt("room.max-player")) {
							if (plugin.getCurrentGame().getState() != BingoGameState.LOADING) {
								Player player = (Player) sender;
								BingoGame game = plugin.getCurrentGame();
								game.addPlayer(player);
								sender.sendMessage(Message.get("title-text") + "\n" + Message.get("commands.join.success",
										game.getPlayers().size(), Configs.getMainCfg().getInt("room.max-player")));
							} else {
								sender.sendMessage(Message.get("prefix") + Message.get("commands.leave.game-loading"));
							}
						} else {
							sender.sendMessage(Message.get("prefix") + Message.get("commands.join.full-players"));
						}
					} else {
						sender.sendMessage(Message.get("prefix") + Message.get("commands.join.already-in"));
					}
				} else {
					sender.sendMessage(Message.get("prefix") + Message.get("commands.join.no-game"));
				}
			} else {
				sender.sendMessage(Message.get("prefix") + Message.get("commands.no-permission"));
			}
		} else {
			sender.sendMessage(Message.get("prefix") + Message.get("commands.no-console"));
		}
	}
}
