package io.apjifengc.bingo.command.sub;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.command.SubCommand;
import io.apjifengc.bingo.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class StopCommand extends SubCommand {

    private final Bingo plugin = Bingo.getInstance();

    @Override public void run(CommandSender sender, String[] args) {
        if (sender.hasPermission("bingo.admin.stop")) {
            if (plugin.hasBingoGame()) {
                plugin.getCurrentGame().stop();
                plugin.setCurrentGame(null);
                Bukkit.broadcastMessage(Message.get("title-text") + "\n" + Message.get("commands.stop.message"));
            } else {
                sender.sendMessage(Message.get("prefix") + Message.get("commands.stop.no-game"));
            }
        } else {
            sender.sendMessage(Message.get("prefix") + Message.get("commands.no-permission"));
        }
    }

}
