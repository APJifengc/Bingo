package io.apjifengc.bingo.listener;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.api.game.BingoGame;
import io.apjifengc.bingo.api.game.BingoPlayer;
import io.apjifengc.bingo.api.game.task.BingoTask;
import io.apjifengc.bingo.api.game.task.impl.EntityTask;
import io.apjifengc.bingo.api.game.task.impl.ItemTask;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

public final class TaskListener implements Listener {

    private final Bingo plugin;

    public TaskListener(Bingo plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
