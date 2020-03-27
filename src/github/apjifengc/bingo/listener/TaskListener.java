package github.apjifengc.bingo.listener;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.game.BingoGameState;
import github.apjifengc.bingo.game.BingoPlayer;
import github.apjifengc.bingo.game.BingoTask;
import github.apjifengc.bingo.game.tasks.BingoEntityTask;
import github.apjifengc.bingo.game.tasks.BingoItemTask;
import github.apjifengc.bingo.game.tasks.enums.EntityTask;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TaskListener implements Listener {
	private final Bingo plugin;

	public TaskListener(Bingo plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	void onPickupItem(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player) {
			if (plugin.hasBingoGame()) {
				BingoGame game = plugin.getCurrentGame();
				if (game.getState() == BingoGameState.RUNNING) {
					BingoPlayer player = game.getPlayer((Player) event.getEntity());
					if (player != null) {
						getItem(player, event.getItem().getItemStack());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	void onClickInventory(InventoryClickEvent event) {
		if (event.getClickedInventory() != null && event.getResult() == Result.ALLOW
				&& event.getWhoClicked() instanceof Player) {
			if (plugin.hasBingoGame()) {
				BingoGame game = plugin.getCurrentGame();
				if (game.getState() == BingoGameState.RUNNING) {
					if (event.getRawSlot() == event.getSlot()) {
						BingoPlayer player = game.getPlayer((Player) event.getWhoClicked());
						if (player != null && event.getInventory().getItem(event.getRawSlot()) != null) {
							getItem(player, event.getInventory().getItem(event.getRawSlot()));
						}
					} else if (game.getPlayer((Player) event.getWhoClicked()) != null
							&& event.getClickedInventory().getType() == InventoryType.PLAYER
							&& (event.getSlot() == 8 || event.getHotbarButton() == 8)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	void onUseBucket(PlayerBucketFillEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.RUNNING) {
				BingoPlayer player = game.getPlayer(event.getPlayer());
				if (player != null) {
					getItem(player, event.getItemStack());
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	void onEntityKill(EntityDeathEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.RUNNING) {
				BingoPlayer player = game.getPlayer(event.getEntity().getKiller());
				if (player != null) {
					killEntity(player, event.getEntityType());
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	void onEntityBreed(EntityBreedEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.RUNNING) {
				if (event.getBreeder() instanceof Player) {
					BingoPlayer player = game.getPlayer((Player)event.getBreeder());
					if (player != null) {
						breedEntity(player, event.getEntityType());
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	void onEntityDamage(EntityDamageByEntityEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.RUNNING) {
				if (event.getDamager() instanceof Player) {
					BingoPlayer player = game.getPlayer((Player)event.getDamager());
					if (player != null) {
						damageEntity(player, event.getEntityType());
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	void onEntityDrop(EntityDeathEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.RUNNING) {
				if (event.getEntity().getKiller() instanceof Player) {
					BingoPlayer player = game.getPlayer((Player)event.getEntity().getKiller());
					if (player != null) {
						entityDropItem(player, event.getDrops());
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	void onEntityTame(EntityTameEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.RUNNING) {
				if (event.getOwner() instanceof Player) {
					BingoPlayer player = game.getPlayer((Player)event.getOwner());
					if (player != null) {
						tameEntity(player, event.getEntityType());
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	void onCreatureSpawn(CreatureSpawnEvent event) {
		if (plugin.hasBingoGame()) {
			BingoGame game = plugin.getCurrentGame();
			if (game.getState() == BingoGameState.RUNNING) {
				if (event.getEntity() instanceof Player) {
					BingoPlayer player = game.getPlayer((Player)event.getEntity());
					if (player != null) {
						spawnCreature(player, event.getSpawnReason());
					}
				}
			}
		}
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

	void killEntity(BingoPlayer player, EntityType type) {
		BingoGame game = plugin.getCurrentGame();
		for (int i = 0; i < 25; i++) {
			BingoTask task = game.getTasks().get(i);
			if (task instanceof BingoEntityTask) {
				BingoEntityTask entityTask = (BingoEntityTask) task;
				if (entityTask.getTaskType() == EntityTask.KILL_ENTITY) {
					if (type == entityTask.getEntityType() && !player.hasFinished(i)) {
						player.finishTask(i);
					}
				}

			}
		}
	}

	void breedEntity(BingoPlayer player, EntityType type) {
		BingoGame game = plugin.getCurrentGame();
		for (int i = 0; i < 25; i++) {
			BingoTask task = game.getTasks().get(i);
			if (task instanceof BingoEntityTask) {
				BingoEntityTask entityTask = (BingoEntityTask) task;
				if (entityTask.getTaskType() == EntityTask.BREED_ENTITY) {
					if (type == entityTask.getEntityType() && !player.hasFinished(i)) {
						player.finishTask(i);
					}
				}
			}
		}
	}

	void damageEntity(BingoPlayer player, EntityType type) {
		BingoGame game = plugin.getCurrentGame();
		for (int i = 0; i < 25; i++) {
			BingoTask task = game.getTasks().get(i);
			if (task instanceof BingoEntityTask) {
				BingoEntityTask entityTask = (BingoEntityTask) task;
				if (entityTask.getTaskType() == EntityTask.DAMAGE_ENTITY) {
					if (type == entityTask.getEntityType() && !player.hasFinished(i)) {
						player.finishTask(i);
					}
				}
			}
		}
	}

	void entityDropItem(BingoPlayer player, List<ItemStack> drops) {
		BingoGame game = plugin.getCurrentGame();
		for (int i = 0; i < 25; i++) {
			BingoTask task = game.getTasks().get(i);
			if (task instanceof BingoEntityTask) {
				BingoEntityTask entityTask = (BingoEntityTask) task;
				if (entityTask.getTaskType() == EntityTask.ENTITY_DROP_ITEM) {
					for (ItemStack itemStack : drops) {
						if (itemStack.getType() == Material.getMaterial(entityTask.getArgument())) {
							player.finishTask(i);
							break;
						}
					}
				}
			}
		}
	}

	void tameEntity(BingoPlayer player, EntityType type) {
		BingoGame game = plugin.getCurrentGame();
		for (int i = 0; i < 25; i++) {
			BingoTask task = game.getTasks().get(i);
			if (task instanceof BingoEntityTask) {
				BingoEntityTask entityTask = (BingoEntityTask) task;
				if (entityTask.getTaskType() == EntityTask.TAME_ENTITY) {
					if (type == entityTask.getEntityType() && !player.hasFinished(i)) {
						player.finishTask(i);
					}
				}
			}
		}
	}

	void spawnCreature(BingoPlayer player, CreatureSpawnEvent.SpawnReason spawnReason) {
		BingoGame game = plugin.getCurrentGame();
		for (int i = 0; i < 25; i++) {
			BingoTask task = game.getTasks().get(i);
			if (task instanceof BingoEntityTask) {
				BingoEntityTask entityTask = (BingoEntityTask) task;
				if (entityTask.getTaskType() == EntityTask.CREATURE_SPAWN) {
					switch(spawnReason) {
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
					}

				}
			}
		}
	}
}
