package io.apjifengc.bingo.command.sub;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.command.SubCommand;
import io.apjifengc.bingo.api.game.BingoGame;
import io.apjifengc.bingo.util.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand extends SubCommand {

    private final Bingo plugin = Bingo.getInstance();

    @Override public void run(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("bingo.use.gui")) {
                if (plugin.hasBingoGame()) {
                    if (plugin.getCurrentGame().getPlayer((Player) sender) != null) {
                        if (plugin.getCurrentGame().getState() == BingoGame.State.RUNNING) {
                            Player player = (Player) sender;
                            player.closeInventory();
                            player.openInventory(plugin.getCurrentGame().getPlayer(player).getInventory());
                        } else {
                            sender.sendMessage(Message.get("prefix") + Message.get("commands.gui.not-start"));
                        }
                    } else {
                        sender.sendMessage(Message.get("prefix") + Message.get("commands.gui.not-in"));
                    }
                } else {
                    sender.sendMessage(Message.get("prefix") + Message.get("commands.gui.no-game"));
                }
            } else {
                sender.sendMessage(Message.get("prefix") + Message.get("commands.no-permission"));
            }
        } else {
            sender.sendMessage(Message.get("prefix") + Message.get("commands.no-console"));
        }
    }

}
