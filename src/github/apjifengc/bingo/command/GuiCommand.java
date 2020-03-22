package github.apjifengc.bingo.command;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoGameState;
import github.apjifengc.bingo.gui.TasksGui;
import github.apjifengc.bingo.util.Msg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand {
    void onGuiCommand (CommandSender sender, Bingo plugin) {
        if (sender instanceof Player){
            if (sender.hasPermission("bingo.use.gui")) {
                if (plugin.hasBingoGame()) {
                    if (plugin.getCurrentGame().getPlayer((Player)sender)!=null) {
                        if(plugin.getCurrentGame().getState() == BingoGameState.RUNNING){
                                ((Player)sender).openInventory(new TasksGui().getTaskGUI((Player)sender,plugin));
                        } else {
                            sender.sendMessage(Msg.get("prefix") + Msg.get("commands.gui.not-start"));
                        }
                    } else {
                        sender.sendMessage(Msg.get("prefix")+Msg.get("commands.gui.not-in"));
                    }
                } else {
                    sender.sendMessage(Msg.get("prefix")+Msg.get("commands.gui.no-game"));
                }
            } else {
                sender.sendMessage(Msg.get("prefix")+Msg.get("commands.no-permission"));
            }
        } else {
            sender.sendMessage(Msg.get("prefix")+Msg.get("commands.no-console"));
        }
    }
}
