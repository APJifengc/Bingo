package github.apjifengc.bingo.listener;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.game.BingoPlayer;
import github.apjifengc.bingo.game.BingoTask;
import github.apjifengc.bingo.game.tasks.BingoEntityTask;
import github.apjifengc.bingo.game.tasks.BingoItemTask;
import github.apjifengc.bingo.game.tasks.enums.EntityTask;

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

public class TaskListener implements Listener {
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
            for (int i = 0; i < 25; i++) {
                BingoTask task = game.getTasks().get(i);
                if (task instanceof BingoEntityTask) {
                    BingoEntityTask entityTask = (BingoEntityTask) task;
                    if (entityTask.getTaskType() == EntityTask.KILL_ENTITY) {
                        if (event.getEntityType() == entityTask.getEntityType() && !player.hasFinished(i)) {
                            player.finishTask(i);
                        }
                    } else if (entityTask.getTaskType() == EntityTask.ENTITY_DROP_ITEM) {
                        for (ItemStack itemStack : event.getDrops()) {
                            if (itemStack.getType() == Material.getMaterial(entityTask.getArgument())) {
                                player.finishTask(i);
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
            for (int i = 0; i < 25; i++) {
                BingoTask task = game.getTasks().get(i);
                if (task instanceof BingoEntityTask) {
                    BingoEntityTask entityTask = (BingoEntityTask) task;
                    if (entityTask.getTaskType() == EntityTask.BREED_ENTITY) {
                        if (event.getEntityType() == entityTask.getEntityType() && !player.hasFinished(i)) {
                            player.finishTask(i);
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
            for (int i = 0; i < 25; i++) {
                BingoTask task = game.getTasks().get(i);
                if (task instanceof BingoEntityTask) {
                    BingoEntityTask entityTask = (BingoEntityTask) task;
                    if (entityTask.getTaskType() == EntityTask.DAMAGE_ENTITY) {
                        if (event.getEntityType() == entityTask.getEntityType() && !player.hasFinished(i)) {
                            player.finishTask(i);
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
            for (int i = 0; i < 25; i++) {
                BingoTask task = game.getTasks().get(i);
                if (task instanceof BingoEntityTask) {
                    BingoEntityTask entityTask = (BingoEntityTask) task;
                    if (entityTask.getTaskType() == EntityTask.TAME_ENTITY) {
                        if (event.getEntityType() == entityTask.getEntityType() && !player.hasFinished(i)) {
                            player.finishTask(i);
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
                for (int i = 0; i < 25; i++) {
                    BingoTask task = game.getTasks().get(i);
                    if (task instanceof BingoEntityTask) {
                        BingoEntityTask entityTask = (BingoEntityTask) task;
                        if (entityTask.getTaskType() == EntityTask.CREATURE_SPAWN) {
                            switch (event.getSpawnReason()) {
                            case BUILD_IRONGOLEM:
                                if (entityTask.getArgument().equalsIgnoreCase("IRONGOLEM") && !player.hasFinished(i)) {
                                    player.finishTask(i);
                                }
                            case BUILD_SNOWMAN:
                                if (entityTask.getArgument().equalsIgnoreCase("SNOWMAN") && !player.hasFinished(i)) {
                                    player.finishTask(i);
                                }
                            case BUILD_WITHER:
                                if (entityTask.getArgument().equalsIgnoreCase("WITHER") && !player.hasFinished(i)) {
                                    player.finishTask(i);
                                }
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

    void getItem(BingoPlayer player, ItemStack is) {
        BingoGame game = plugin.getCurrentGame();
        for (int i = 0; i < 25; i++) {
            BingoTask task = game.getTasks().get(i);
            if (task instanceof BingoItemTask) {
                BingoItemTask itemTask = (BingoItemTask) task;
                if (itemTask.getTarget().getType() == is.getType() && !player.hasFinished(i)) {
                    player.finishTask(i);
                }
            }
        }
    }
}
