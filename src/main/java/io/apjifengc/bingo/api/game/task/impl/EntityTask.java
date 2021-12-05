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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang.Validate.isTrue;


/**
 * Represents a task which asks players to do something with entities.
 *
 * @author APJifengc, Milkory
 */
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

    public enum Type {
        KILL, BREED, DAMAGE, DROP, TAME, SUMMON
    }

}