package io.apjifengc.bingo.api.game;

import io.apjifengc.bingo.api.exception.BadTaskException;
import io.apjifengc.bingo.api.game.task.BingoTask;
import io.apjifengc.bingo.api.game.task.impl.*;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Message;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.apache.commons.lang.Validate.isTrue;

@SuppressWarnings("unused")
public class BingoTaskManager {

    @Getter private static final BingoTaskManager instance = new BingoTaskManager();
    @Getter private static final Random random = new Random();

    @Getter private YamlConfiguration config;
    @Getter private List<String> taskPoll;

    /** Whether it is repeatable when generating. */
    @Getter private boolean isRepeatable = false;

    /** A map contains all registered tasks. */
    private final Map<String, Class<? extends BingoTask>> taskMap = new HashMap<>() {{
        put("item", ItemTask.class);
        put("entity", EntityTask.class);
        put("murder", MurderTask.class);
        put("impossible", ImpossibleTask.class);
    }};

    public BingoTaskManager() {
        reloadTaskConfig();
    }

    /** Get the map contains all registered tasks. */
    public Map<String, Class<? extends BingoTask>> getTaskMap() {
        return Collections.unmodifiableMap(taskMap);
    }

    /** Reload the {@code task.yml} file. */
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
                result.addAll(parseTaskObject(value, prefix + o.toString() + "::"));
            }
            return result;
        } else return Collections.singletonList(prefix + obj.toString());
    }

    /**
     * Register a {@link BingoTask}, which should do before game start.
     *
     * @param key   The key of the task which will be used in {@code task.yml} to represent this task.
     * @param clazz The class of the task.
     */
    public void registerTask(@NotNull String key, @NotNull Class<? extends BingoTask> clazz) {
        Objects.requireNonNull(key, "Task key cannot be null");
        Objects.requireNonNull(clazz, "Task class cannot be null");
        if (taskMap.containsKey(key)) {
            throw new IllegalArgumentException(String.format("Two tasks have conflicting key '%s' - '%s', '%s'",
                    key, taskMap.get(key).getName(), clazz.getName()));
        } else taskMap.put(key, clazz);
    }

    /**
     * Generate a list of {@link BingoTask} from {@code task.yml} file,
     * depending on the {@code repeatable} setting in the file.
     *
     * @return A list containing 25 tasks.
     * @throws BadTaskException When failed to parse a task expression.
     * @see #repeatableGenerateTasks()
     * @see #nonRepeatableGenerateTasks()
     */
    public List<BingoTask> generateTasks() throws BadTaskException {
        if (!isRepeatable && taskPoll.size() < 25) {
            throw new IllegalStateException("The number of tasks is not enough to generate tasks without repeating.");
        }
        return isRepeatable ? repeatableGenerateTasks() : nonRepeatableGenerateTasks();
    }

    /**
     * Generate a list of {@link BingoTask} from {@code task.yml} file.
     * This method allows repeatable tasks.
     *
     * @return A list containing 25 tasks.
     * @throws BadTaskException When failed to parse a task expression.
     * @see #generateTasks()
     * @see #nonRepeatableGenerateTasks()
     */
    public List<BingoTask> repeatableGenerateTasks() throws BadTaskException {
        var result = new ArrayList<BingoTask>(25);
        for (int i = 0; i < 25; i++) {
            var expr = taskPoll.get(random.nextInt(taskPoll.size()));
            result.add(parseTaskExpression(expr));
        }
        return result;
    }

    /**
     * Generate a list of {@link BingoTask} from {@code task.yml} file.
     * This method disallows repeatable tasks,
     * which will check their expression in {@code task.yml}.
     *
     * @return A list containing 25 tasks.
     * @throws BadTaskException When failed to parse a task expression.
     * @see #generateTasks()
     * @see #repeatableGenerateTasks()
     */
    public List<BingoTask> nonRepeatableGenerateTasks() throws BadTaskException {
        var result = new ArrayList<BingoTask>(25);
        var exprSet = new HashSet<String>(25);
        for (int i = 0; i < 25; i++) {
            var expr = taskPoll.get(random.nextInt(taskPoll.size()));
            while (exprSet.contains(expr)) expr = taskPoll.get(random.nextInt(taskPoll.size()));
            exprSet.add(expr);
            result.add(parseTaskExpression(expr));
        }
        return result;
    }

    /**
     * Parse a task expression to a {@link BingoTask}.
     *
     * @param expr The expression to be parsed.
     * @return A task according to the expression.
     * @throws BadTaskException When failed to parse the expression.
     */
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

    /**
     * Check if it can generate tasks according to the {@code task.yml} file.
     *
     * @return If {@link #isRepeatable} or {@link #taskPoll}'s size isn't lower than 25.
     */
    public boolean canGenerateTasks() {
        return isRepeatable || taskPoll.size() >= 25;
    }

}
