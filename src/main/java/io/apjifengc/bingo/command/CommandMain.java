/**
 * bingo 命令
 *
 * @author APJifengc
 */
package io.apjifengc.bingo.command;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.command.sub.*;
import io.apjifengc.bingo.api.game.BingoGame;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Message;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CommandMain implements TabExecutor {

    private final Bingo plugin = Bingo.getInstance();

    public CommandMain() {
        Bukkit.getPluginCommand("bingo").setExecutor(this);
    }

    @Getter private final Map<String, SubCommand> commands = Map.ofEntries(
            Map.entry("debug", new DebugCommand()),
            Map.entry("gui", new GuiCommand()),
            Map.entry("help", new HelpCommand()),
            Map.entry("join", new JoinCommand()),
            Map.entry("leave", new LeaveCommand()),
            Map.entry("reload", new ReloadCommand()),
            Map.entry("start", new StartCommand()),
            Map.entry("stop", new StopCommand()),
            Map.entry("config", new ConfigCommand())
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        var sub = commands.get(args.length == 0 ? "help" : args[0]);
        if (sub != null) {
            sub.run(sender, args);
        } else {
            sender.sendMessage(Message.get("prefix") + Message.get("commands.unknown-command"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bingo")) {
            if (args.length == 1) {
                return getSubCommands(sender).stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
            } else if (args.length == 2) {
                if ("gui".equals(args[0])) {
                    if (sender instanceof Player && sender.hasPermission("bingo.use.gui.others")) {
                        if (plugin.hasBingoGame() && plugin.getCurrentGame().isAllowOpenOthersGui()) {
                            return Bukkit.getOnlinePlayers().stream()
                                    .map(HumanEntity::getName)
                                    .filter(it -> !sender.getName().equals(it))
                                    .collect(Collectors.toList());
                        }
                    }
                }
                if ("config".equals(args[0])) {
                    return Arrays.asList(
                            "startkits"
                    );
                }
            } else if (args.length == 3) {
                if ("config".equals(args[0])) {
                    if ("startkits".equals(args[1])) {
                        return Arrays.asList(
                                "add",
                                "remove",
                                "list"
                        );
                    }
                }
            } else if (args.length == 4) {
                if ("config".equals(args[0])) {
                    if ("startkits".equals(args[1])) {
                        if ("remove".equals(args[2])) {
                            List<String> kits = new ArrayList<>();
                            for (int i = 0; i < Config.getStartkits().size(); i++) {
                                kits.add(String.valueOf(i));
                            }
                            return kits;
                        }
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    public List<String> getSubCommands(CommandSender sender) {
        List<String> sub = new ArrayList<>();
        if (sender instanceof Player) {
            // Player-specific commands
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
        if (sender.hasPermission("bingo.admin.config"))
            sub.add("config");
        if (plugin.hasBingoGame()) {
            // 游戏正在运行，不允许开始游戏
            sub.remove("start");
            if (sender instanceof Player && plugin.getCurrentGame().getPlayer((Player) sender) != null) {
                // 在游戏中的玩家，不允许加入游戏
                sub.remove("join");
                if (plugin.getCurrentGame().getState() != BingoGame.State.RUNNING) {
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
        sub.add("help");
        return sub;
    }
}
