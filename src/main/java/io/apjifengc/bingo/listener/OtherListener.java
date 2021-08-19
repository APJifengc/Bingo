package io.apjifengc.bingo.listener;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.game.BingoGame;
import io.apjifengc.bingo.game.BingoPlayer;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class OtherListener implements Listener {

    private final Bingo plugin;

    public OtherListener(Bingo plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.hasBingoGame() && plugin.getCurrentGame().getPlayer(player) != null) {
            BingoGame game = plugin.getCurrentGame();
            if (game.getState() == BingoGame.State.WAITING) {
                game.removePlayer(player);
            } else if (game.getState() == BingoGame.State.RUNNING) {
                player.getInventory().setItem(8, new ItemStack(Material.AIR));
            }
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            game.getBossbar().removePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.hasBingoGame()) {
            BingoGame game = plugin.getCurrentGame();
            Player player = event.getPlayer();
            BingoPlayer bplayer = game.getPlayer(player);
            if (game.getState() == BingoGame.State.RUNNING && bplayer != null) {
                bplayer.updatePlayer();
                bplayer.giveGuiItem();
                player.setScoreboard(bplayer.getScoreboard());
                game.getBossbar().addPlayer(player);
                if (!player.getWorld().getName().equals(Config.getMain().getString("room.world-name"))) {
                    // TODO: Multiworld
                    /*plugin.getMultiverseCore().getSafeTTeleporter().safelyTeleport(
                            plugin.getServer().getConsoleSender(), player, plugin.getMultiverseCore().getDestFactory()
                                    .getDestination(Config.getMain().getString("room.world-name")));*/
                }
                player.sendMessage(Message.get("chat.back"));
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (plugin.hasBingoGame()) {
            BingoGame game = plugin.getCurrentGame();
            if (game.getState() == BingoGame.State.RUNNING) {
                BingoPlayer player = game.getPlayer(event.getPlayer());
                if (player != null && (event.getAction() == Action.RIGHT_CLICK_AIR
                        || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                    if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
                        // TODO: Not good :<
                        plugin.getCommandMain().getCommands().get("gui").run(event.getPlayer(), new String[]{});
                    }
                }
            }
        }
    }

    @EventHandler
    void onPlayerRespawn(PlayerRespawnEvent event) {
        if (plugin.hasBingoGame()) {
            BingoGame game = plugin.getCurrentGame();
            if (game.getState() == BingoGame.State.RUNNING) {
                BingoPlayer player = game.getPlayer(event.getPlayer());
                if (player != null) {
                    // TODO: Multiworld
                    //plugin.getMultiverseCore().getSafeTTeleporter().safelyTeleport(plugin.getServer().getConsoleSender(), event.getPlayer(), plugin.getMultiverseCore().getDestFactory().getDestination(Config.getMain().getString("room.world-name")));
                    player.giveGuiItem();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerDropItem(PlayerDropItemEvent event) {
        if (plugin.hasBingoGame()) {
            BingoGame game = plugin.getCurrentGame();
            if (game.getState() == BingoGame.State.RUNNING) {
                BingoPlayer player = game.getPlayer(event.getPlayer());
                if (player != null) {
                    if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
