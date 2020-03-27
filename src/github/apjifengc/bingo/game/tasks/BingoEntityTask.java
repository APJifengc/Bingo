package github.apjifengc.bingo.game.tasks;

import github.apjifengc.bingo.game.BingoTask;
import github.apjifengc.bingo.game.tasks.enums.EntityTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;


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
        ItemStack showItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta itemMeta = showItem.getItemMeta();
        if (entityType == null) {
            itemMeta.setDisplayName("CREATURE_SPAWN"+argument);
            this.taskName = "CREATURE_SPAWN"+argument;
        } else {
            itemMeta.setDisplayName(taskType+entityType.name());
            this.taskName = taskType+entityType.name();
        }
        showItem.setItemMeta(itemMeta);
        this.showItem = showItem;

        // TODO 生物显示（包括lore和name）
    }
}
