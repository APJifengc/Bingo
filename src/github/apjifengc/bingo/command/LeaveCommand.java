package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand {
    void onLeaveCommand (CommandSender sender, Bingo plugin) {
        if (sender instanceof Player){
            if (sender.hasPermission("bingo.use.leave")) {
                if (plugin.hasBingoGame()) {
                    if (plugin.getCurrentGame().getPlayer((Player)sender)!=null) {
                        plugin.getCurrentGame().removePlayer((Player) sender);
                        Bukkit.broadcastMessage(Message.getMessage("prefix") + Message.getMessage("commands.leave.success"));
                    } else {
                        sender.sendMessage(Message.getMessage("prefix") + Message.getMessage("commands.leave.not-in"));
                    }
                } else {
                    sender.sendMessage(Message.getMessage("prefix") + Message.getMessage("commands.leave.no-game"));
                }
            } else {
                sender.sendMessage(Message.getMessage("prefix") + Message.getMessage("commands.no-permission"));
            }
        } else {
            sender.sendMessage(Message.getMessage("prefix") + Message.getMessage("commands.no-console"));
        }
    }
}
