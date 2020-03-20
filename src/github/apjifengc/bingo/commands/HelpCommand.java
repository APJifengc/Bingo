package github.apjifengc.bingo.commands;

import org.bukkit.command.CommandSender;

public class HelpCommand {

	final String msg = "§9§l-==============  §c§lBingo§9§l  ==============-";

	void onHelpCommand(CommandSender sender) {
		StringBuilder sb = new StringBuilder(msg);
		if (sender.hasPermission("bingo.use.gui"))
			sb.append("\n §7/bingo gui §e- §6Open a GUI for Bingo.");
		if (sender.hasPermission("bingo.use.join"))
			sb.append("\n §7/bingo join §e- §6Join a Bingo game.");
		if (sender.hasPermission("bingo.use.leave"))
			sb.append("\n §7/bingo leave §e- §6Leave the Bingo game.");
		if (sender.hasPermission("bingo.admin.start"))
			sb.append("\n §7/bingo start §e- §6Start a Bingo game.");
		if (sender.hasPermission("bingo.admin.stop"))
			sb.append("\n §7/bingo stop §e- §6Stop the Bingo game.");
		if (sb.toString().equals(msg))
			sb.append("\n   §cYou don't have any permission for commands.");
		sb.append("\n§9§l-===================================-");
		sender.sendMessage(sb.toString());
	}

}