package github.apjifengc.bingo.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import github.apjifengc.bingo.util.Message;

public class DebugCommand {

	void onDebugCommand(CommandSender sender, String[] args) {
		Bukkit.broadcastMessage(Message.getMessage("test", "123", "456", "789"));
	}

}
