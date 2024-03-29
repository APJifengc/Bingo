package io.apjifengc.bingo.command.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.api.game.BingoGame;
import io.apjifengc.bingo.api.game.BingoPlayer;
import io.apjifengc.bingo.command.SubCommand;
import io.apjifengc.bingo.util.Message;

public class LeaveCommand extends SubCommand {

    private final Bingo plugin = Bingo.getInstance();

    @Override public void run(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("bingo.use.leave")) {
                if (plugin.hasBingoGame()) {
                    BingoPlayer bp = plugin.getCurrentGame().getPlayer((Player) sender);
                    if (bp != null) {
                        if (plugin.getCurrentGame().getState() != BingoGame.State.LOADING) {
                            sender.sendMessage(Message.get("prefix") + Message.get("commands.leave.success"));
                            plugin.getCurrentGame().removePlayer((Player) sender);
                        } else {
                            sender.sendMessage(Message.get("prefix") + Message.get("commands.leave.game-loading"));
                        }
                    } else {
                        sender.sendMessage(Message.get("prefix") + Message.get("commands.leave.not-in"));
                    }
                } else {
                    sender.sendMessage(Message.get("prefix") + Message.get("commands.leave.no-game"));
                }
            } else {
                sender.sendMessage(Message.get("prefix") + Message.get("commands.no-permission"));
            }
        } else {
            sender.sendMessage(Message.get("prefix") + Message.get("commands.no-console"));
        }
    }

}
