package io.apjifengc.bingo.command.sub;

import io.apjifengc.bingo.command.SubCommand;
import io.apjifengc.bingo.util.Message;
import org.bukkit.command.CommandSender;


public class HelpCommand extends SubCommand {

    @Override public void run(CommandSender sender, String[] args) {
        StringBuilder sb = new StringBuilder(Message.get("title-text"));
        if (sender.hasPermission("bingo.use.gui"))
            sb.append("\n ").append(Message.get("commands.help.gui"));
        if (sender.hasPermission("bingo.use.join"))
            sb.append("\n ").append(Message.get("commands.help.join"));
        if (sender.hasPermission("bingo.use.leave"))
            sb.append("\n ").append(Message.get("commands.help.leave"));
        if (sender.hasPermission("bingo.admin.start"))
            sb.append("\n ").append(Message.get("commands.help.start"));
        if (sender.hasPermission("bingo.admin.stop"))
            sb.append("\n ").append(Message.get("commands.help.stop"));
        if (sender.hasPermission("bingo.admin.reload"))
            sb.append("\n ").append(Message.get("commands.help.reload"));
        if (sender.hasPermission("bingo.admin.config"))
            sb.append("\n ").append(Message.get("commands.help.config"));
        if (sb.toString().equals(Message.get("title-text")))
            sb.append("\n ").append(Message.get("commands.help.no-permission"));
        sender.sendMessage(sb.toString());
    }

}