package io.apjifengc.bingo.command.sub;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.command.SubCommand;
import io.apjifengc.bingo.util.Message;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {

    private final Bingo plugin = Bingo.getInstance();

    @Override public void run(CommandSender sender, String[] args) {
        if (sender.hasPermission("bingo.admin.reload")) {
            plugin.loadPlugin();
            sender.sendMessage("Reload complete.");
        } else {
            sender.sendMessage(Message.get("prefix") + Message.get("commands.no-permission"));
        }
    }

}
