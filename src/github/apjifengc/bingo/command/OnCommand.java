/**
 * bingo 命令
 * @author APJifengc
 */
package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoGameState;
import github.apjifengc.bingo.util.Message;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OnCommand implements TabExecutor {

	private final Bingo plugin;

	public OnCommand(Bingo plugin) {
		this.plugin = plugin;
		plugin.getCommand("bingo").setExecutor(this);
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
					new DebugCommand().onDebugCommand(sender, args, plugin);
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
					sender.sendMessage(Message.get("prefix") + Message.get("commands.unknown-command"));
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
		if (sender instanceof Player) {
			// 玩家专属命令
			if (sender.hasPermission("bingo.use.gui"))
				sub.add("gui");
			if (sender.hasPermission("bingo.use.join"))
				sub.add("join");
			if (sender.hasPermission("bingo.use.leave"))
				sub.add("leave");
		}
		if (sender.hasPermission("bingo.admin.start"))
			sub.add("start");
		if (sender.hasPermission("bingo.admin.stop"))
			sub.add("stop");
		if (sender.hasPermission("bingo.admin.reload"))
			sub.add("reload");
		if (plugin.hasBingoGame()) {
			// 游戏正在运行，不允许开始游戏
			sub.remove("start");
			if (sender instanceof Player && plugin.getCurrentGame().getPlayer((Player) sender) != null) {
				// 在游戏中的玩家，不允许加入游戏
				sub.remove("join");
				if (plugin.getCurrentGame().getState() != BingoGameState.RUNNING) {
					sub.remove("gui");
				}
			} else {
				// 不在游戏中的玩家或控制台，不允许离开游戏和打开 GUI
				sub.remove("gui");
				sub.remove("leave");
			}
		} else {
			// 游戏不在运行，不允许停止游戏、加入游戏、退出游戏、打开 GUI
			sub.remove("stop");
			sub.remove("join");
			sub.remove("leave");
			sub.remove("gui");
		}
		return sub;
	}
}
