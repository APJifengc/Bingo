package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
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
                        Bukkit.broadcastMessage("§c§l Bingo §9§l▌ §eYou have quited the Bingo game just now.\n");
                    } else {
                        sender.sendMessage("§c§l Bingo §9§l▌ §cYou are not in the game!");
                    }
                } else {
                    sender.sendMessage("§c§l Bingo §9§l▌ §cThere is no game running!");
                }
            } else {
                sender.sendMessage("§c§l Bingo §9§l▌ §cYou don't have the permission for this command.");
            }
        } else {
            sender.sendMessage("§c§l Bingo §9§l▌ §cThis command is only for players. You can't use it in the console.");
        }
    }
}
