package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoGameState;
import github.apjifengc.bingo.util.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand {
	void onGuiCommand(CommandSender sender, Bingo plugin) {
		if (sender instanceof Player) {
			if (sender.hasPermission("bingo.use.gui")) {
				if (plugin.hasBingoGame()) {
					if (plugin.getCurrentGame().getPlayer((Player) sender) != null) {
						if (plugin.getCurrentGame().getState() == BingoGameState.RUNNING) {
							Player player = (Player) sender;
							player.closeInventory();
							player.openInventory(plugin.getCurrentGame().getPlayer(player).getInventory());
						} else {
							sender.sendMessage(Message.get("prefix") + Message.get("commands.gui.not-start"));
						}
					} else {
						sender.sendMessage(Message.get("prefix") + Message.get("commands.gui.not-in"));
					}
				} else {
					sender.sendMessage(Message.get("prefix") + Message.get("commands.gui.no-game"));
				}
			} else {
				sender.sendMessage(Message.get("prefix") + Message.get("commands.no-permission"));
			}
		} else {
			sender.sendMessage(Message.get("prefix") + Message.get("commands.no-console"));
		}
	}
}
