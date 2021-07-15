package io.apjifengc.bingo.command.sub;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.command.SubCommand;
import io.apjifengc.bingo.exception.BadTaskException;
import io.apjifengc.bingo.game.BingoGame;
import io.apjifengc.bingo.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.command.*;

public class StartCommand extends SubCommand {

    private final Bingo plugin = Bingo.getInstance();

    @Override public void run(CommandSender sender, String[] args) {
        if (sender.hasPermission("bingo.admin.start")) {
            if (!plugin.hasBingoGame()) {
                BingoGame game = new BingoGame(plugin);
                try {
                    game.generateTasks();
                } catch (BadTaskException e) {
                    e.printStackTrace();
                    sender.sendMessage(Message.get("prefix") + Message.get("commands.start.unable-start") + e.getMessage());
                    return;
                }
                plugin.setCurrentGame(game);
                Bukkit.broadcastMessage(
                        Message.get("title-text") + Message.get("commands.start.success"));
            } else {
                sender.sendMessage(Message.get("prefix") + Message.get("commands.start.already-running"));
            }
        } else {
            sender.sendMessage(Message.get("prefix") + Message.get("commands.no-permission"));
        }
    }

}
