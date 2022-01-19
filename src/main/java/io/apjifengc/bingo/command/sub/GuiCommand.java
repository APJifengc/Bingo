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
                    var player = plugin.getCurrentGame().getPlayer((Player) sender);
                    if (player != null) {
                        if (plugin.getCurrentGame().getState() == BingoGame.State.RUNNING) {
                            if (args.length == 1) {
                                player.openGui();
                            } else if (args.length == 2) {
                                if (sender.hasPermission("bingo.use.gui.others")) {
                                    var target = plugin.getCurrentGame().getPlayer(args[1]);
                                    if (target != null) {
                                        player.openGui(target);
                                    } else {
                                        Message.sendToWithPrefix(sender, "commands.gui.other-not-in");
                                    }
                                } else {
                                    Message.sendToWithPrefix(sender, "commands.no-permission");
                                }
                            } else {
                                Message.sendToWithPrefix(sender, "commands.too-many-arguments");
                            }
                        } else {
                            Message.sendToWithPrefix(sender, "commands.gui.not-start");
                        }
                    } else {
                        Message.sendToWithPrefix(sender, "commands.gui.not-in");
                    }
                } else {
                    Message.sendToWithPrefix(sender, "commands.gui.no-game");
                }
            } else {
                Message.sendToWithPrefix(sender, "commands.no-permission");
            }
        } else {
            Message.sendToWithPrefix(sender, "commands.no-console");
        }
    }

}
