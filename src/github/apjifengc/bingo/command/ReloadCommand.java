package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.Configs;
import github.apjifengc.bingo.util.Msg;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    void onReloadCommand(CommandSender sender, Bingo plugin) {
        if (sender.hasPermission("bingo.admin.reload")) {
            Configs.reloadConfig(plugin);
            sender.sendMessage("Reload complete.");
        } else {
            sender.sendMessage(Msg.get("prefix")+ Msg.get("commands.no-permission"));
        }
    }
}
