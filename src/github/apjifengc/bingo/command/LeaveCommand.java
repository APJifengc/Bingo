package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.util.Msg;

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
                        Bukkit.broadcastMessage(Msg.get("prefix") + Msg.get("commands.leave.success"));
                    } else {
                        sender.sendMessage(Msg.get("prefix") + Msg.get("commands.leave.not-in"));
                    }
                } else {
                    sender.sendMessage(Msg.get("prefix") + Msg.get("commands.leave.no-game"));
                }
            } else {
                sender.sendMessage(Msg.get("prefix") + Msg.get("commands.no-permission"));
            }
        } else {
            sender.sendMessage(Msg.get("prefix") + Msg.get("commands.no-console"));
        }
    }
}
