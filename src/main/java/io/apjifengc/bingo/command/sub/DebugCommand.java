package io.apjifengc.bingo.command.sub;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.command.SubCommand;
import io.apjifengc.bingo.game.BingoPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand extends SubCommand {

    private final Bingo plugin = Bingo.getInstance();

    @Override public void run(CommandSender sender, String[] args) {
        if (sender.hasPermission("bingo.dev")) {
            if (args[1].equalsIgnoreCase("finish")) {
                BingoPlayer player = plugin.getCurrentGame().getPlayer((Player) sender);
                if (player == null) return;
                player.finishTask(Integer.parseInt(args[2]));
                player.updateScoreboard();
            }
        }
    }

}
