package io.apjifengc.bingo.game;

import io.apjifengc.bingo.exception.BadTaskException;
import io.apjifengc.bingo.game.task.BingoTask;
import io.apjifengc.bingo.game.task.impl.EntityTask;
import io.apjifengc.bingo.game.task.impl.ImpossibleTask;
import io.apjifengc.bingo.game.task.impl.ItemTask;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Message;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.apache.commons.lang.Validate.*;

public class BingoTaskManager {

    @Getter private static final BingoTaskManager instance = new BingoTaskManager();
    @Getter private static final Random random = new Random();

    @Getter private YamlConfiguration config;
    @Getter private List<String> taskPoll;

    @Getter private boolean isRepeatable = false;

    @Getter private Map<String, Class<? extends BingoTask>> taskMap = new HashMap<>() {{
        put("item", ItemTask.class);
        put("entity", EntityTask.class);
        put("impossible", ImpossibleTask.class);
    }};

    public BingoTaskManager() {
        reloadTaskConfig();
    }

    public void reloadTaskConfig() {
        this.config = Config.loadConfig("tasks.yml");
        this.isRepeatable = config.getBoolean("repeatable");
        this.taskPoll = parseTaskObject(config.get("tasks"), "");
        if (!canGenerateTasks()) Message.warning("errors.task-not-enough");
    }

    private List<String> parseTaskObject(Object obj, String prefix) {
        if (obj instanceof List) {
            var result = new ArrayList<String>();
            for (Object o : (List<?>) obj) {
                result.addAll(parseTaskObject(o, prefix));
            }
            return result;
        } else if (obj instanceof Map) {
            var result = new ArrayList<String>();
            var map = (Map<?, ?>) obj;
            for (Object o : map.keySet()) {
                var value = map.get(o);
                result.addAll(parseTaskObject(value, o + "::"));
            }
            return result;
        } else return Collections.singletonList(prefix + obj.toString());
    }

    public void registerTask(@NotNull String key, @NotNull Class<? extends BingoTask> clazz) {
        Objects.requireNonNull(key, "Task key cannot be null");
        Objects.requireNonNull(clazz, "Task class cannot be null");
        if (taskMap.containsKey(key)) {
            throw new IllegalArgumentException(String.format("Two tasks have conflicting key '%s' - '%s', '%s'",
                    key, taskMap.get(key).getName(), clazz.getName()));
        } else taskMap.put(key, clazz);
    }

    public List<BingoTask> generateTasks() {
        if (!isRepeatable && taskPoll.size() < 25) {
            throw new IllegalStateException("The number of tasks is not enough to generate tasks without repeating.");
        }
        return isRepeatable ? repeatableGenerateTasks() : nonRepeatableGenerateTasks();
    }

    @SneakyThrows public List<BingoTask> repeatableGenerateTasks() {
        var result = new ArrayList<BingoTask>(25);
        for (int __ = 0; __ < 25; __++) {
            var expr = taskPoll.get(random.nextInt(taskPoll.size()));
            result.add(parseTaskExpression(expr));
        }
        return result;
    }

    @SneakyThrows public List<BingoTask> nonRepeatableGenerateTasks() {
        var result = new ArrayList<BingoTask>(25);
        
    }

    public BingoTask parseTaskExpression(String expr) throws BadTaskException {
        isTrue(!expr.isEmpty(), "Empty expression detected");
        var split = expr.split("::");
        isTrue(split.length > 0, "Task type not found");
        var type = split[0];
        var clazz = taskMap.get(type);
        isTrue(clazz != null, "Unknown task type - " + type);
        try {
            var method = clazz.getMethod("newInstance", String[].class);
            return (BingoTask) method.invoke(null, (Object) Arrays.copyOfRange(split, 1, split.length));
        } catch (Throwable e) {
            throw new BadTaskException(String.format("Task '%s' didn't work properly.", type), e);
        }
    }

    public boolean canGenerateTasks() {
        return isRepeatable || taskPoll.size() >= 25;
    }

}
