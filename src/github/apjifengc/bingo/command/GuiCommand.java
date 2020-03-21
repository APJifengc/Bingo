package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.gui.TasksGui;
import github.apjifengc.bingo.util.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand {
    void onGuiCommand (CommandSender sender, Bingo plugin) {
        if (sender instanceof Player){
            if (sender.hasPermission("bingo.use.gui")) {
                if (plugin.hasBingoGame()) {
                    if (plugin.getCurrentGame().getPlayer((Player)sender)!=null) {
                        ((Player)sender).openInventory(new TasksGui().getTaskGUI((Player)sender,plugin));
                    } else {
                        sender.sendMessage(Message.getMessage("prefix")+Message.getMessage("commands.gui.not-in"));
                    }
                } else {
                    sender.sendMessage(Message.getMessage("prefix")+Message.getMessage("commands.gui.no-game"));
                }
            } else {
                sender.sendMessage(Message.getMessage("prefix")+Message.getMessage("commands.no-permission"));
            }
        } else {
            sender.sendMessage(Message.getMessage("prefix")+Message.getMessage("commands.no-console"));
        }
    }
}
