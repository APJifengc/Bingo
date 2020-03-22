package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import github.apjifengc.bingo.util.Msg;
import org.bukkit.entity.Player;

public class DebugCommand {

	void onDebugCommand(CommandSender sender, String[] args, Bingo plugin) {
		plugin.getCurrentGame().getPlayer((Player)sender).finishTask(Integer.parseInt(args[1]));
	}

}
