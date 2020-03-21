package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.ConfigLoad;
import github.apjifengc.bingo.util.Message;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    void onReloadCommand(CommandSender sender, Bingo plugin) {
        if (sender.hasPermission("bingo.admin.reload")) {
            ConfigLoad.reloadConfig(plugin);
            sender.sendMessage("Reload complete.");
        } else {
            sender.sendMessage(Message.getMessage("prefix")+Message.getMessage("commands.no-permission"));
        }
    }
}
