package io.apjifengc.bingo.api.timer;

import io.apjifengc.bingo.Bingo;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * This represents a timer for Bingo games. <br/>
 * You should use {@link BingoTimerManager} to manage the timer.
 *
 * @author APJifengc
 */
public final class BingoTimer {
    private final Bingo plugin;

    private BukkitTask task;

    private final Map<String, BingoTimerTask> taskMap = new HashMap<>();

    @Getter
    private BingoTimerTask closestTask;

    long timestamp;

    public BingoTimer(Bingo plugin) {
        this.plugin = plugin;
    }

    public void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                timestamp++;
                long closestTime = Long.MAX_VALUE;
                BingoTimerTask closestTask = null;
                for (var entry : taskMap.entrySet()) {
                    var task = entry.getValue();
                    if (task.isRunning()) {
                        if (task.getInterval() != 0) {
                            task.nextTick++;
                            if (task.nextTick == task.getInterval()) {
                                task.nextTick = 0;
                                task.tick();
                            }
                        }
                        if (task.timestamp <= timestamp) {
                            task.run();
                            task.setRunning(false);
                        } else {
                            if (task.timestamp < closestTime) {
                                closestTime = task.timestamp;
                                closestTask = task;
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public void stop() {
        task.cancel();
    }

    public void reset() {
        timestamp = 0;
    }

    public boolean isStarted() {
        return task != null && !task.isCancelled();
    }

    public void registerTask(BingoTimerTask task) throws IllegalArgumentException {
        if (taskMap.containsKey(task.getId())) {
            throw new IllegalArgumentException("The task id '" + task.getId() + "' already exists!");
        }
        task.timestamp = task.getTime();
        taskMap.put(task.getId(), task);
    }

    public void registerTaskRelatively(BingoTimerTask task) throws IllegalArgumentException {
        if (taskMap.containsKey(task.getId())) {
            throw new IllegalArgumentException("The task id '" + task.getId() + "' already exists!");
        }
        task.timestamp = task.getTime() + timestamp;
        taskMap.put(task.getId(), task);
    }

    public BingoTimerTask getTask(String id) {
        return taskMap.get(id);
    }

    public long getTime(String id) {
        return taskMap.get(id).timestamp;
    }

    public void setTime(String id, long timestamp) {
        taskMap.get(id).timestamp = timestamp;
    }

    public void startAllTasks() {
        taskMap.values().forEach(t -> t.setRunning(true));
    }

    public void clearTasks() {
        taskMap.clear();
    }
}
