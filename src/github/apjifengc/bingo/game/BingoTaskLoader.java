package github.apjifengc.bingo.game;

import github.apjifengc.bingo.exception.BadTaskException;
import github.apjifengc.bingo.game.tasks.BingoEntityTask;
import github.apjifengc.bingo.game.tasks.BingoImpossibleTask;
import github.apjifengc.bingo.game.tasks.BingoItemTask;
import github.apjifengc.bingo.game.tasks.enums.EntityTask;
import github.apjifengc.bingo.util.Configs;
import github.apjifengc.bingo.util.Message;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BingoTaskLoader {

    static List<BingoTask> tasks = new ArrayList<BingoTask>();

    static void loadTasks() throws BadTaskException {
        //Item Task Load
        List<String> items = Configs.getTaskCfg().getStringList("items");
        for(String item : items) {
            if (item.equalsIgnoreCase("IMPOSSIBLE")) {
                tasks.add(new BingoImpossibleTask());
            } else {
                Material m = Material.getMaterial(item);
                if (m != null) {
                    tasks.add(new BingoItemTask(new ItemStack(m)));
                } else {
                    throw new BadTaskException(Message.get("errors.bad-task.cant-solve", item));
                }
            }
        }
        //Item Task Load
        List<String> entities = Configs.getTaskCfg().getStringList("entities");
        for(String entity : entities) {
            String[] splitEntity = entity.split("\\|");
            if (splitEntity.length==0) continue;
            if (splitEntity.length==1) throw new BadTaskException(Message.get("errors.bad-task.too-less-args"));
            EntityTask entityTaskType;
            EntityType entityType = null;
            try {
                entityTaskType = EntityTask.valueOf(splitEntity[0]);
            } catch (IllegalArgumentException e) {
                throw new BadTaskException(Message.get("errors.bad-task.cant-solve", splitEntity[0]));
            }
            if (entityTaskType != EntityTask.CREATURE_SPAWN) {
                try {
                    entityType = EntityType.valueOf(splitEntity[1]);
                } catch (IllegalArgumentException e) {
                    throw new BadTaskException(Message.get("errors.bad-task.cant-solve", splitEntity[1]));
                }
            }
            switch (entityTaskType) {
                case CREATURE_SPAWN:
                    if ((splitEntity[1].equalsIgnoreCase("IRONGOLEM"))
                    || (splitEntity[1].equalsIgnoreCase("SNOWMAN"))
                    || (splitEntity[1].equalsIgnoreCase("WITHER"))) {
                        tasks.add(new BingoEntityTask(EntityTask.CREATURE_SPAWN,null,splitEntity[1]));
                    } else {
                        throw new BadTaskException(Message.get("errors.bad-task.cant-solve", splitEntity[1]));
                    }
                    break;
                case ENTITY_DROP_ITEM:
                    Material m = Material.getMaterial(splitEntity[2]);
                    if (m != null) {
                        tasks.add(new BingoEntityTask(EntityTask.ENTITY_DROP_ITEM, entityType, splitEntity[2]));
                    } else {
                        throw new BadTaskException(Message.get("errors.bad-task.cant-solve", splitEntity[2]));
                    }
                    break;
                default:
                    tasks.add(new BingoEntityTask(entityTaskType, entityType, null));
            }
        }
    }
}
