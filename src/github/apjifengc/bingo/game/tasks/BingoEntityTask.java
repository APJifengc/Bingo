package github.apjifengc.bingo.game.tasks;

import github.apjifengc.bingo.game.BingoTask;
import github.apjifengc.bingo.game.tasks.enums.EntityTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Skull;


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
    String[] arguments;

    /**
     * Bingo 生物任务
     *
     * @param taskType 任务类型
     * @param arguments 任务参数
     */
    public BingoEntityTask(EntityTask taskType, String[] arguments) {
        this.taskType = taskType;
        this.arguments = arguments;
        ItemStack showItem = new ItemStack(Material.PLAYER_HEAD);
    }
}
