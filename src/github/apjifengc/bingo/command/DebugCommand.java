package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoPlayer;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand {

	void onDebugCommand(CommandSender sender, String[] args, Bingo plugin) {
		if(sender.hasPermission("bingo.dev")) {
			if (args[1].equalsIgnoreCase("complete")) {
				BingoPlayer player = plugin.getCurrentGame().getPlayer((Player) sender);
				player.finishTask(Integer.parseInt(args[2]));
				player.updateScoreboard();
			}
		}
		
	}

}
