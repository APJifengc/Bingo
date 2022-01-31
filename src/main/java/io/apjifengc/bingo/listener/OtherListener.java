package io.apjifengc.bingo.listener;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.api.exception.BadTaskException;
import io.apjifengc.bingo.api.game.BingoGame;
import io.apjifengc.bingo.api.game.BingoPlayer;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Message;
import io.apjifengc.bingo.util.TeleportUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("ClassCanBeRecord")
public final class OtherListener implements Listener {

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
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (Config.getMain().getBoolean("server.auto-start-end", false)) {
            var player = event.getPlayer();
            if (plugin.getCurrentGame().getState() == BingoGame.State.RUNNING
                    && !Config.getMain().getBoolean("room.join-while-game")) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        Message.get("prefix") + Message.get("commands.join.disallow-join"));
                return;
            }
            if (plugin.getCurrentGame().getPlayer(player) == null) {
                if (plugin.getCurrentGame().getPlayers().size() < Config.getMain()
                        .getInt("room.max-player")) {
                    if (plugin.getCurrentGame().getState() == BingoGame.State.LOADING) {
                        event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                                Message.get("prefix") + Message.get("commands.leave.game-loading"));
                    }
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                            Message.get("prefix") + Message.get("commands.join.full-players"));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.hasBingoGame()) {
            BingoGame game = plugin.getCurrentGame();
            Player player = event.getPlayer();
            BingoPlayer gamePlayer = game.getPlayer(player);
            if (game.getState() == BingoGame.State.RUNNING && gamePlayer != null) {
                gamePlayer.updatePlayer();
                gamePlayer.giveGuiItem();
                player.setScoreboard(gamePlayer.getScoreboard());
                game.getBossbar().addPlayer(player);
                if (!player.getWorld().getName().equals(Config.getMain().getString("room.world-name"))) {
                    World world = game.getBingoWorld();
                    TeleportUtil.safeTeleport(player, world, 0, 0);
                }
                player.sendMessage(Message.get("chat.back"));
            }
        }
        if (Config.getMain().getBoolean("server.auto-start-end", false)) {
            var player = event.getPlayer();
            if (plugin.getCurrentGame().getState() == BingoGame.State.RUNNING
                    && !Config.getMain().getBoolean("room.join-while-game")) {
                return;
            }
            if (plugin.getCurrentGame().getPlayer(player) == null) {
                if (plugin.getCurrentGame().getPlayers().size() < Config.getMain()
                        .getInt("room.max-player")) {
                    if (plugin.getCurrentGame().getState() != BingoGame.State.LOADING) {
                        BingoGame game = plugin.getCurrentGame();
                        game.addPlayer(player);
                        player.sendMessage(Message.get("title-text") + "\n" + Message.get("commands.join.success",
                                game.getPlayers().size(), Config.getMain().getInt("room.max-player")));
                    }
                }
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
                    if (game.getTaskItem().isSimilar(event.getItem())) {
                        player.openGui();
                        event.setCancelled(true);
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
                    if (game.getTaskItem().isSimilar(event.getItemDrop().getItemStack())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
