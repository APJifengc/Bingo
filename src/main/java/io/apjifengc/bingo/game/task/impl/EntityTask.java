package io.apjifengc.bingo.game.task.impl;

import io.apjifengc.bingo.game.task.BingoTask;
import io.apjifengc.bingo.util.Message;
import io.apjifengc.bingo.util.NameUtil;
import io.apjifengc.bingo.util.Skull;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.apache.commons.lang.Validate.*;


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
        ItemStack showItem;
        String entityName;
        if (type == Type.SUMMON) {
            if ("SNOWMAN".equals(param)) {
                showItem = Skull.getEntitySkull(EntityType.SNOWMAN);
                entityName = "Snowman";
            } else if ("WITHER".equals(param)) {
                showItem = Skull.getEntitySkull(EntityType.WITHER);
                entityName = "Wither";
            } else {
                showItem = Skull.getEntitySkull(EntityType.IRON_GOLEM);
                entityName = "Iron Golem";
            }
        } else {
            showItem = Skull.getEntitySkull(entity);
            entityName = NameUtil.formatName(entity.name());
        }
        ItemMeta itemMeta = showItem.getItemMeta();
        String taskName;
        String lore;

        if (type == Type.DROP) {
            taskName = Message.get("task.entity-task.entity-drop-item.title", entityName,
                    NameUtil.getItemName(new ItemStack(Material.getMaterial(param))));
            lore = Message.get("task.entity-task.entity-drop-item.desc", entityName,
                    NameUtil.getItemName(new ItemStack(Material.getMaterial(param))));
        } else {
            taskName = Message.get(toLangKey(type) + ".title", entityName);
            lore = Message.get(toLangKey(type) + ".desc", entityName);
        }
        itemMeta.setDisplayName(taskName);
        this.shownName = new BaseComponent[]{ new TextComponent(taskName) };
        itemMeta.setLore(Arrays.asList(lore.split("\n")));
        showItem.setItemMeta(itemMeta);
        this.shownItem = showItem;
    }

    private String toLangKey(Type ori) {
        return "task.entity-task." + ori.name().toLowerCase().replace("_", "-");
    }

    public enum Type {
        KILL, BREED, DAMAGE, DROP, TAME, SUMMON
    }

}
