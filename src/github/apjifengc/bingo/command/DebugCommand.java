package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoPlayer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand {

	void onDebugCommand(CommandSender sender, String[] args, Bingo plugin) {
		if (args[1].equalsIgnoreCase("complete")) {
			BingoPlayer player = plugin.getCurrentGame().getPlayer((Player) sender);
			player.finishTask(Integer.parseInt(args[2]));
			player.updateScoreboard();
		} else if (args[1].equalsIgnoreCase("join")) {
			new JoinCommand().onJoinCommand(Bukkit.getEntity(UUID.fromString(args[2])), plugin);
		}
	}

}
