/**
 * bingo 命令
 * @author APJifengc
 */
package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.util.Msg;

import org.bukkit.command.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OnCommand implements TabExecutor {

	private final Bingo plugin;

	public OnCommand(Bingo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("bingo")) {
			if (args.length == 0) {
				new HelpCommand().onHelpCommand(sender);
			} else {
				if (args[0].equalsIgnoreCase("help")) {
					new HelpCommand().onHelpCommand(sender);
				} else if (args[0].equalsIgnoreCase("debug")) {
					new DebugCommand().onDebugCommand(sender, args , plugin);
				} else if (args[0].equalsIgnoreCase("start")) {
					new StartCommand().onStartCommand(sender, plugin);
				} else if (args[0].equalsIgnoreCase("stop")) {
					new StopCommand().onStopCommand(sender, plugin);
				} else if (args[0].equalsIgnoreCase("join")) {
					new JoinCommand().onJoinCommand(sender, plugin);
				} else if (args[0].equalsIgnoreCase("leave")) {
					new LeaveCommand().onLeaveCommand(sender, plugin);
				} else if (args[0].equalsIgnoreCase("gui")) {
					new GuiCommand().onGuiCommand(sender, plugin);
				} else if (args[0].equalsIgnoreCase("reload")) {
					new ReloadCommand().onReloadCommand(sender, plugin);
				} else {
					sender.sendMessage(Msg.get("prefix") + Msg.get("commands.unknown-command"));
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("bingo")) {
			if (args.length == 1) {
				return getSubCommands(sender).stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
			}
		}
		return Arrays.asList();
	}

	public List<String> getSubCommands(CommandSender sender) {
		List<String> sub = new ArrayList<String>();
		sub.add("help");
		if (sender.hasPermission("bingo.use.gui"))
			sub.add("gui");
		if (sender.hasPermission("bingo.use.join"))
			sub.add("join");
		if (sender.hasPermission("bingo.use.leave"))
			sub.add("leave");
		if (sender.hasPermission("bingo.admin.start"))
			sub.add("start");
		if (sender.hasPermission("bingo.admin.stop"))
			sub.add("stop");
		return sub;
	}
}
