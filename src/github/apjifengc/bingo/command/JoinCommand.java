package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoGame;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand {
    void onJoinCommand (CommandSender sender, Bingo plugin) {
        if (sender instanceof Player){
            if (sender.hasPermission("bingo.use.join")) {
                if (plugin.hasBingoGame()) {
                    if (plugin.getCurrentGame().getPlayer((Player)sender)==null) {
                        plugin.getCurrentGame().addPlayer((Player)sender);
                        Bukkit.broadcastMessage("§9§l-==============  §c§lBingo§9§l  ==============-\n" +
                                "  §eYou have joined the Bingo game.\n" +
                                "  §eThere is current §2"+plugin.getCurrentGame().getPlayers().size()+"/"+"§e players in the game.\n" +
                                "  §ePlease wait for the game start.\n");
                    } else {
                        sender.sendMessage("§c§l Bingo §9§l▌ §cYou are already in the game!");
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
