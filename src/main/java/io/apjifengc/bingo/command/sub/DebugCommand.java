package io.apjifengc.bingo.command.sub;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.command.SubCommand;
import io.apjifengc.bingo.game.BingoPlayer;
import io.apjifengc.bingo.world.SchematicManager;
import io.apjifengc.bingo.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DebugCommand extends SubCommand {

    private final Bingo plugin = Bingo.getInstance();

    @Override public void run(CommandSender sender, String[] args) {
        if (sender.hasPermission("bingo.dev")) {
            if (args[1].equalsIgnoreCase("finish")) {
                BingoPlayer player = plugin.getCurrentGame().getPlayer((Player) sender);
                if (player == null) return;
                player.finishTask(Integer.parseInt(args[2]));
                player.updateScoreboard();
            }
            if (args[1].equalsIgnoreCase("regenerate")) {
                WorldManager.regenerateWorld(args[2]);
            }
            if (args[1].equalsIgnoreCase("teleport")) {
                ((Player) sender).teleport(new Location(Bukkit.getWorld(args[2]), 0, 120, 0));
            }
            if (args[1].equalsIgnoreCase("paste")) {
                try {
                    SchematicManager.buildSchematic(new File(plugin.getDataFolder(), "lobby.schem"),
                            ((Player) sender).getLocation().clone());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (args[1].equalsIgnoreCase("undo")) {
                SchematicManager.undo();
            }
        }
    }

}
