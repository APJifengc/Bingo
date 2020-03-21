package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import github.apjifengc.bingo.util.Message;
import org.bukkit.entity.Player;

public class DebugCommand {

	void onDebugCommand(CommandSender sender, String[] args, Bingo plugin) {
		plugin.getCurrentGame().getPlayer((Player)sender).finishTask(5);
	}

}
