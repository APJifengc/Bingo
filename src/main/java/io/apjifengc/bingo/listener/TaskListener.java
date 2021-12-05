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
    private Player placedBlockPlayer;

    public TaskListener(Bingo plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    void onPickupItem(EntityPickupItemEvent event) {
        BingoPlayer player = plugin.getPlayer(event.getEntity());
        if (player != null) {
            getItem(player, event.getItem().getItemStack());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    void onClickInventory(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getResult() == Result.ALLOW) {
            BingoPlayer player = plugin.getPlayer(event.getWhoClicked());
            if (event.getRawSlot() == event.getSlot()) {
                if (player != null && event.getInventory().getItem(event.getRawSlot()) != null) {
                    getItem(player, event.getInventory().getItem(event.getRawSlot()));
                }
            } else if (event.getClickedInventory().getType() == InventoryType.PLAYER
                    && (event.getSlot() == 8 || event.getHotbarButton() == 8)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onUseBucket(PlayerBucketFillEvent event) {
        BingoPlayer player = plugin.getPlayer(event.getPlayer());
        if (player != null) {
            getItem(player, event.getItemStack());
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onEntityKill(EntityDeathEvent event) {
        BingoPlayer player = plugin.getPlayer(event.getEntity().getKiller());
        if (player != null) {
            BingoGame game = plugin.getCurrentGame();
            for (BingoTask task : game.getBoard()) {
                if (task instanceof EntityTask && !player.hasFinished(task)) {
                    EntityTask entityTask = (EntityTask) task;
                    if (entityTask.getType() == EntityTask.Type.KILL) {
                        if (event.getEntityType() == entityTask.getEntity()) {
                            player.finishTask(task);
                        }
                    } else if (entityTask.getType() == EntityTask.Type.DROP) {
                        for (ItemStack itemStack : event.getDrops()) {
                            if (itemStack.getType() == Material.getMaterial(entityTask.getParam())) {
                                player.finishTask(task);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onEntityBreed(EntityBreedEvent event) {
        BingoPlayer player = plugin.getPlayer(event.getBreeder());
        if (player != null) {
            BingoGame game = plugin.getCurrentGame();
            for (BingoTask task : game.getBoard()) {
                if (task instanceof EntityTask && !player.hasFinished(task)) {
                    EntityTask entityTask = (EntityTask) task;
                    if (entityTask.getType() == EntityTask.Type.BREED) {
                        if (event.getEntityType() == entityTask.getEntity()) {
                            player.finishTask(task);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onEntityDamage(EntityDamageByEntityEvent event) {
        BingoPlayer player = plugin.getPlayer(event.getDamager());
        if (player != null) {
            BingoGame game = plugin.getCurrentGame();
            for (BingoTask task : game.getBoard()) {
                if (task instanceof EntityTask && !player.hasFinished(task)) {
                    EntityTask entityTask = (EntityTask) task;
                    if (entityTask.getType() == EntityTask.Type.DAMAGE) {
                        if (event.getEntityType() == entityTask.getEntity()) {
                            player.finishTask(task);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onEntityTame(EntityTameEvent event) {
        BingoPlayer player = plugin.getPlayer(event.getOwner());
        if (player != null) {
            BingoGame game = plugin.getCurrentGame();
            for (BingoTask task : game.getBoard()) {
                if (task instanceof EntityTask && !player.hasFinished(task)) {
                    EntityTask entityTask = (EntityTask) task;
                    if (entityTask.getType() == EntityTask.Type.TAME) {
                        if (event.getEntityType() == entityTask.getEntity()) {
                            player.finishTask(task);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.IRON_BLOCK
                || event.getBlockPlaced().getType() == Material.PUMPKIN
                || event.getBlockPlaced().getType() == Material.SNOW_BLOCK
                || event.getBlockPlaced().getType() == Material.WITHER_SKELETON_SKULL
                || event.getBlockPlaced().getType() == Material.SOUL_SAND
                || event.getBlockPlaced().getType() == Material.WITHER_SKELETON_WALL_SKULL) {
            placedBlockPlayer = event.getPlayer();
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BUILD_WITHER) {
            BingoPlayer player = plugin.getPlayer(placedBlockPlayer);
            if (player != null) {
                BingoGame game = plugin.getCurrentGame();
                for (BingoTask task : game.getBoard()) {
                    if (task instanceof EntityTask) {
                        EntityTask entityTask = (EntityTask) task;
                        if (entityTask.getType() == EntityTask.Type.SUMMON && !player.hasFinished(task)) {
                            switch (event.getSpawnReason()) {
                                case BUILD_IRONGOLEM:
                                    if (entityTask.getParam().equalsIgnoreCase("IRONGOLEM")) {
                                        player.finishTask(task);
                                    }
                                    break;
                                case BUILD_SNOWMAN:
                                    if (entityTask.getParam().equalsIgnoreCase("SNOWMAN")) {
                                        player.finishTask(task);
                                    }
                                    break;
                                case BUILD_WITHER:
                                    if (entityTask.getParam().equalsIgnoreCase("WITHER")) {
                                        player.finishTask(task);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
        }
        placedBlockPlayer = null;
    }

    void getItem(BingoPlayer player, ItemStack itemStack) {
        BingoGame game = plugin.getCurrentGame();
        for (BingoTask task : game.getBoard()) {
            if (task instanceof ItemTask) {
                ItemTask itemTask = (ItemTask) task;
                if (itemTask.getTarget().getType() == itemStack.getType() && !player.hasFinished(task)) {
                    player.finishTask(task);
                }
            }
        }
    }

}
