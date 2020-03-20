package github.apjifengc.bingo.command;

import org.bukkit.command.CommandSender;

import github.apjifengc.bingo.util.Message;

public class HelpCommand {

	final String msg = "§9§l-==============  §c§lBingo§9§l  ==============-";

	void onHelpCommand(CommandSender sender) {
		StringBuilder sb = new StringBuilder(msg);
		if (sender.hasPermission("bingo.use.gui"))
			sb.append("\n " + Message.getMessage("commands.help.gui"));
		if (sender.hasPermission("bingo.use.join"))
			sb.append("\n " + Message.getMessage("commands.help.join"));
		if (sender.hasPermission("bingo.use.leave"))
			sb.append("\n " + Message.getMessage("commands.help.leave"));
		if (sender.hasPermission("bingo.admin.start"))
			sb.append("\n " + Message.getMessage("commands.help.start"));
		if (sender.hasPermission("bingo.admin.stop"))
			sb.append("\n " + Message.getMessage("commands.help.stop"));
		if (sb.toString().equals(msg))
			sb.append("\n " + Message.getMessage("commands.help.no-permission"));
		sender.sendMessage(sb.toString());
	}

}