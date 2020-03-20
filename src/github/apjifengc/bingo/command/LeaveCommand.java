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
                        Bukkit.broadcastMessage(Message.getMessage("prefix") + "§eYou have quited the Bingo game just now.\n");
                    } else {
                        sender.sendMessage(Message.getMessage("prefix") + "§cYou are not in the game!");
                    }
                } else {
                    sender.sendMessage(Message.getMessage("prefix") + "§cThere is no game running!");
                }
            } else {
                sender.sendMessage(Message.getMessage("prefix") + "§cYou don't have the permission for this command.");
            }
        } else {
            sender.sendMessage(Message.getMessage("prefix") + "§cThis command is only for players. You can't use it in the console.");
        }
    }
}
