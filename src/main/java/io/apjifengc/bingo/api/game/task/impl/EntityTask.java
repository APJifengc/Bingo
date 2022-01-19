package io.apjifengc.bingo.api.game.task.impl;

import io.apjifengc.bingo.api.game.task.BingoTask;
import io.apjifengc.bingo.util.Message;
import io.apjifengc.bingo.util.Skull;
import io.apjifengc.bingo.util.TaskUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang.Validate.isTrue;


/**
 * Represents a task which asks players to do something with entities.
 *
 * @author APJifengc, Milkory
 */
@SuppressWarnings("unused")
public class EntityTask extends BingoTask {

    @Getter private final Type type;
    @Getter private final EntityType entity;
    @Getter private final String param;
    @Getter private final ItemStack shownItem;
    @Getter private final BaseComponent[] shownName;

    public static EntityTask newInstance(String[] args) {
        isTrue(args.length >= 1, "Task type (arg 1) not found");
        isTrue(args.length >= 2, "Entity type (arg 2) not found");
        Type type;
        try {
            type = Type.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown entity task type - " + args[0]);
        }
        EntityType entity;
        try {
            entity = EntityType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown target entity type - " + args[1]);
        }
        if (type == Type.DROP) {
            isTrue(args.length >= 3, "Item to drop (arg 3) not found");
            return new EntityTask(type, entity, args[2].toUpperCase());
        }
        return new EntityTask(type, entity, null);
    }

    public EntityTask(Type type, @Nullable EntityType entity, @Nullable String param) {
        Objects.requireNonNull(entity, "Target entity cannot be null");
        Objects.requireNonNull(type, "Task type cannot be null");
        this.type = type;
        this.param = param;
        this.entity = entity;
        ItemStack shownItem;
        if (type == Type.SUMMON) {
            if ("SNOWMAN".equals(param)) {
                shownItem = Skull.getEntitySkull(EntityType.SNOWMAN);
                entity = EntityType.SNOWMAN;
            } else if ("WITHER".equals(param)) {
                shownItem = Skull.getEntitySkull(EntityType.WITHER);
                entity = EntityType.WITHER;
            } else {
                shownItem = Skull.getEntitySkull(EntityType.IRON_GOLEM);
                entity = EntityType.IRON_GOLEM;
            }
        } else {
            shownItem = Skull.getEntitySkull(entity);
        }

        BaseComponent[] taskName;
        List<String> rawLore;
        if (type == Type.DROP) {
            taskName = Message.getComponents("task.entity-task.drop.title", TaskUtil.getEntityName(entity),
                    TaskUtil.getItemName(Objects.requireNonNull(Material.getMaterial(param))));
            rawLore = Message.getWrapRaw("task.entity-task.drop.desc", TaskUtil.getEntityName(entity),
                    TaskUtil.getItemName(Objects.requireNonNull(Material.getMaterial(param))));
        } else {
            taskName = Message.getComponents(toLangKey(type) + ".title", TaskUtil.getEntityName(entity));
            rawLore = Message.getWrapRaw(toLangKey(type) + ".desc", TaskUtil.getEntityName(entity));
        }
        var rawTaskName = ComponentSerializer.toString(taskName);
        shownItem = TaskUtil.setRawDisplay(shownItem, rawTaskName, rawLore);
        this.shownName = taskName;
        this.shownItem = shownItem;
    }

    private String toLangKey(Type ori) {
        return "task.entity-task." + ori.name().toLowerCase().replace("_", "-");
    }

    @Override
    public @NotNull Listener getTaskListener() {
        return new Listener() {
            private Player placedBlockPlayer;

            @EventHandler(ignoreCancelled = true)
            void onEntityKill(EntityDeathEvent event) {
                Player player = event.getEntity().getKiller();
                if (getType() == EntityTask.Type.KILL) {
                    if (event.getEntityType() == getEntity()) {
                        finishTask(player);
                    }
                } else if (getType() == EntityTask.Type.DROP) {
                    for (ItemStack itemStack : event.getDrops()) {
                        if (itemStack.getType() == Material.getMaterial(getParam())) {
                            finishTask(player);
                            break;
                        }
                    }
                }
            }

            @EventHandler(ignoreCancelled = true)
            void onEntityBreed(EntityBreedEvent event) {
                var breeder = event.getBreeder();
                if (breeder instanceof Player) {
                    if (getType() == EntityTask.Type.BREED) {
                        if (event.getEntityType() == getEntity()) {
                            finishTask((Player) breeder);
                        }
                    }
                }
            }

            @EventHandler(ignoreCancelled = true)
            void onEntityDamage(EntityDamageByEntityEvent event) {
                var damager = event.getDamager();
                if (damager instanceof Player) {
                    if (getType() == EntityTask.Type.DAMAGE) {
                        if (event.getEntityType() == getEntity()) {
                            finishTask((Player) damager);
                        }
                    }
                }
            }

            @EventHandler(ignoreCancelled = true)
            void onEntityTame(EntityTameEvent event) {
                var owner = event.getOwner();
                if (owner instanceof Player) {
                    if (getType() == EntityTask.Type.TAME) {
                        if (event.getEntityType() == getEntity()) {
                            finishTask((Player) owner);
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
                    if (getType() == EntityTask.Type.SUMMON) {
                        switch (event.getSpawnReason()) {
                            case BUILD_IRONGOLEM:
                                if (getParam().equalsIgnoreCase("IRONGOLEM")) {
                                    finishTask(placedBlockPlayer);
                                }
                                break;
                            case BUILD_SNOWMAN:
                                if (getParam().equalsIgnoreCase("SNOWMAN")) {
                                    finishTask(placedBlockPlayer);
                                }
                                break;
                            case BUILD_WITHER:
                                if (getParam().equalsIgnoreCase("WITHER")) {
                                    finishTask(placedBlockPlayer);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                placedBlockPlayer = null;
            }
        };
    }

    public enum Type {
        KILL, BREED, DAMAGE, DROP, TAME, SUMMON
    }
}
