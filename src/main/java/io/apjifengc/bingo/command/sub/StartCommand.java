package io.apjifengc.bingo.command.sub;

import org.bukkit.command.CommandSender;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.api.exception.BadTaskException;
import io.apjifengc.bingo.command.SubCommand;
import io.apjifengc.bingo.util.Message;

public class StartCommand extends SubCommand {

    private final Bingo plugin = Bingo.getInstance();

    @Override public void run(CommandSender sender, String[] args) {
        if (sender.hasPermission("bingo.admin.start")) {
            if (!plugin.hasBingoGame()) {
                try {
                    plugin.startGame();
                } catch (BadTaskException e) {
                    e.printStackTrace();
                    sender.sendMessage(Message.get("prefix") + Message.get("commands.start.unable-start") + e.getMessage());
                }
            } else {
                sender.sendMessage(Message.get("prefix") + Message.get("commands.start.already-running"));
            }
        } else {
            sender.sendMessage(Message.get("prefix") + Message.get("commands.no-permission"));
        }
    }

}
