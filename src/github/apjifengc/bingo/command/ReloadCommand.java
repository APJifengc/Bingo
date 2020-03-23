package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.util.Message;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    void onReloadCommand(CommandSender sender, Bingo plugin) {
        if (sender.hasPermission("bingo.admin.reload")) {
            plugin.loadPlugin();
            sender.sendMessage("Reload complete.");
        } else {
            sender.sendMessage(Message.get("prefix")+ Message.get("commands.no-permission"));
        }
    }
}
