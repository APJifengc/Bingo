package github.apjifengc.bingo.command;

import org.bukkit.command.CommandSender;

import github.apjifengc.bingo.util.Msg;

public class HelpCommand {
	void onHelpCommand(CommandSender sender) {
		StringBuilder sb = new StringBuilder(Msg.get("title"));
		if (sender.hasPermission("bingo.use.gui"))
			sb.append("\n " + Msg.get("commands.help.gui"));
		if (sender.hasPermission("bingo.use.join"))
			sb.append("\n " + Msg.get("commands.help.join"));
		if (sender.hasPermission("bingo.use.leave"))
			sb.append("\n " + Msg.get("commands.help.leave"));
		if (sender.hasPermission("bingo.admin.start"))
			sb.append("\n " + Msg.get("commands.help.start"));
		if (sender.hasPermission("bingo.admin.stop"))
			sb.append("\n " + Msg.get("commands.help.stop"));
		if (sender.hasPermission("bingo.admin.reload"))
			sb.append("\n " + Msg.get("commands.help.reload"));
		if (sb.toString().equals(Msg.get("title")))
			sb.append("\n " + Msg.get("commands.help.no-permission"));
		sender.sendMessage(sb.toString());
	}

}