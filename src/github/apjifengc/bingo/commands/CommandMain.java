/**
 * Command /bingo start
 * @author APJifengc
 */
package github.apjifengc.bingo.commands;

import github.apjifengc.bingo.Bingo;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class CommandMain implements CommandExecutor {
    private final Bingo plugin;

    public CommandMain(Bingo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length==0){
            ArrayList usage = new ArrayList();
            String fullUsage;
            if(commandSender.hasPermission("bingo.use.gui")) usage.add(" §7/bingo gui §e- §6Open a GUI for Bingo.");
            if(commandSender.hasPermission("bingo.use.join")) usage.add(" §7/bingo join §e- §6Join a Bingo game.");
            if(commandSender.hasPermission("bingo.use.leave")) usage.add(" §7/bingo leave §e- §6Leave the Bingo game.");
            if(commandSender.hasPermission("bingo.admin.start")) usage.add(" §7/bingo start §e- §6Start a Bingo game..");
            if(commandSender.hasPermission("bingo.admin.stop")) usage.add(" §7/bingo stop §e- §6Stop the Bingo game.");
            if(commandSender.hasPermission("bingo.admin.setting")) usage.add(" §7/bingo setting §e- §6Setting a Bingo game.");
            if(usage.size()==0) commandSender.sendMessage("§9§l-==============§c§lBingo§9§l==============-\n §cYou don't have permission for this command.\n§9§l-=================================-");
            else {
                commandSender.sendMessage("§9§l-==============§c§lBingo§9§l==============-\n §eCommand usage:\n"+String.join("\n",usage)+"\n§9§l-=================================-");
            }
        }
       /** if (commandSender.hasPermission("bingo.admin.start")) {

        } else {
            commandSender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do that.");
        }*/
        return false;
    }
}

