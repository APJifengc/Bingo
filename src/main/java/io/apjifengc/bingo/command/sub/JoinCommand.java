package io.apjifengc.bingo.command.sub;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.command.SubCommand;
import io.apjifengc.bingo.game.BingoGame;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand extends SubCommand {

    private final Bingo plugin = Bingo.getInstance();

    @Override public void run(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("bingo.use.join")) {
                if (plugin.hasBingoGame()) {
                    if (plugin.getCurrentGame().getState() == BingoGame.State.RUNNING
                            && !Config.getMain().getBoolean("room.join-while-game")) {
                        sender.sendMessage(Message.get("prefix") + Message.get("commands.join.disallow-join"));
                        return;
                    }
                    if (plugin.getCurrentGame().getPlayer((Player) sender) == null) {
                        if (plugin.getCurrentGame().getPlayers().size() < Config.getMain()
                                .getInt("room.max-player")) {
                            if (plugin.getCurrentGame().getState() != BingoGame.State.LOADING) {
                                Player player = (Player) sender;
                                BingoGame game = plugin.getCurrentGame();
                                game.addPlayer(player);
                                sender.sendMessage(Message.get("title-text") + "\n" + Message.get("commands.join.success",
                                        game.getPlayers().size(), Config.getMain().getInt("room.max-player")));
                            } else {
                                sender.sendMessage(Message.get("prefix") + Message.get("commands.leave.game-loading"));
                            }
                        } else {
                            sender.sendMessage(Message.get("prefix") + Message.get("commands.join.full-players"));
                        }
                    } else {
                        sender.sendMessage(Message.get("prefix") + Message.get("commands.join.already-in"));
                    }
                } else {
                    sender.sendMessage(Message.get("prefix") + Message.get("commands.join.no-game"));
                }
            } else {
                sender.sendMessage(Message.get("prefix") + Message.get("commands.no-permission"));
            }
        } else {
            sender.sendMessage(Message.get("prefix") + Message.get("commands.no-console"));
        }
    }

}
