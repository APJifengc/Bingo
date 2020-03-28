package github.apjifengc.bingo.game.tasks;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoTask;
import github.apjifengc.bingo.game.tasks.enums.EntityTask;
import github.apjifengc.bingo.util.Message;
import github.apjifengc.bingo.util.NameFormatter;
import github.apjifengc.bingo.util.Skull;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;


/**
 * 代表一个生物有关任务
 *
 * @author APJifengc
 */
public class BingoEntityTask extends BingoTask {

    @Getter
    @Setter
    EntityTask taskType;

    @Getter
    @Setter
    EntityType entityType;

    @Getter
    @Setter
    String argument;

    /**
     * Bingo 生物任务
     *
     * @param taskType 任务类型
     * @param entityType 生物类型
     * @param argument 任务参数
     */
    public BingoEntityTask(EntityTask taskType, @Nullable EntityType entityType , @Nullable String argument) {
        this.taskType = taskType;
        this.argument = argument;
        this.entityType = entityType;
        ItemStack showItem;
        String entityName;
        if (taskType == EntityTask.CREATURE_SPAWN) {
            if (argument == "SNOWMAN") {
                showItem = Skull.getEntitySkull(EntityType.SNOWMAN);
                entityName = "Snowman";
            } else if (argument == "WITHER") {
                showItem = Skull.getEntitySkull(EntityType.WITHER);
                entityName = "Wither";
            } else {
                showItem = Skull.getEntitySkull(EntityType.IRON_GOLEM);
                entityName = "Iron golem";
            }
        } else {
            showItem = Skull.getEntitySkull(entityType);
            entityName = NameFormatter.toNameFormat(entityType.name());
        }
        ItemMeta itemMeta = showItem.getItemMeta();
        String taskName;
        String lore;

        if (taskType == EntityTask.CREATURE_SPAWN) {
            taskName = Message.get("task.entity-task.creature-spawn.title",
                    entityName
            );
            lore = Message.get("task.entity-task.creature-spawn.desc",
                    entityName
            );
        } else if (taskType == EntityTask.ENTITY_DROP_ITEM) {
            taskName = Message.get("task.entity-task.entity-drop-item.title",
                    entityName,
                    Bingo.NMS.getItemName(new ItemStack(Material.getMaterial(argument)))
            );
            lore = Message.get("task.entity-task.entity-drop-item.desc",
                    entityName,
                    Bingo.NMS.getItemName(new ItemStack(Material.getMaterial(argument)))
            );
        } else {
            taskName = Message.get("task.entity-task."+
                    taskType.name().toLowerCase().replace("_","-")+ ".title",
                    entityName
            );
            lore = Message.get("task.entity-task."+
                    taskType.name().toLowerCase().replace("_","-")+ ".desc",
                    entityName
            );
        }
        itemMeta.setDisplayName(taskName);
        this.taskName = taskName;
        itemMeta.setLore(Arrays.asList(lore.split("\n")));
        showItem.setItemMeta(itemMeta);
        this.showItem = showItem;
    }
}
