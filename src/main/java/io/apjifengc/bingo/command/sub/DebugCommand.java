package io.apjifengc.bingo.command.sub;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.command.SubCommand;
import io.apjifengc.bingo.api.game.BingoPlayer;
import io.apjifengc.bingo.world.SchematicManager;
import io.apjifengc.bingo.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class DebugCommand extends SubCommand {

    private final Bingo plugin = Bingo.getInstance();

    @Override public void run(CommandSender sender, String[] args) {
        if (sender.hasPermission("bingo.dev")) {
            if (args.length < 2) {
                sender.sendMessage("You're lost.");
            } else if (args[1].equalsIgnoreCase("finish")) {
                BingoPlayer player = plugin.getCurrentGame().getPlayer((Player) sender);
                if (player == null) return;
                player.finishTask(Integer.parseInt(args[2]));
                player.updateScoreboard();
            } else if (args[1].equalsIgnoreCase("regenerate")) {
                WorldManager.regenerateWorld(args[2]);
            } else if (args[1].equalsIgnoreCase("teleport")) {
                ((Player) sender).teleport(new Location(Bukkit.getWorld(args[2]), 0, 120, 0));
            } else if (args[1].equalsIgnoreCase("paste")) {
                try {
                    SchematicManager.buildSchematic(new File(plugin.getDataFolder(), "lobby.schem"),
                            ((Player) sender).getLocation().clone());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (args[1].equalsIgnoreCase("undo")) {
                SchematicManager.undo();
            } else if ("unbreakable".equalsIgnoreCase(args[1])) {
                var item = ((Player) sender).getInventory().getItemInMainHand();
                var nbtItem = NBTItem.convertItemtoNBT(item);
                nbtItem.getCompound("tag").setBoolean("Unbreakable", true);
                ((Player) sender).getInventory().setItemInMainHand(NBTItem.convertNBTtoItem(nbtItem));
            } else {
                sender.sendMessage("?");
            }
        }
    }

}
