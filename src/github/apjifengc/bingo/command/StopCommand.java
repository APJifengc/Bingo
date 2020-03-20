package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoGame;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class StopCommand {
    void onStopCommand (CommandSender sender, Bingo plugin) {
        if (sender.hasPermission("bingo.admin.stop")) {
            if (plugin.hasBingoGame()) {
                plugin.setCurrentGame(null);
                Bukkit.broadcastMessage("§9§l-==============  §c§lBingo§9§l  ==============-\n" +
                        "  §cThe Bingo game has been stopped by an administer.\n\n");
            } else {
                sender.sendMessage("§c§l Bingo §9§l▌ §cThere is no game running!");
            }
        } else {
            sender.sendMessage("§c§l Bingo §9§l▌ §cYou don't have the permission for this command.");
        }
    }
}
