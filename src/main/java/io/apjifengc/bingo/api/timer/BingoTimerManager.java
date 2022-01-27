package io.apjifengc.bingo.api.timer;

import io.apjifengc.bingo.Bingo;
import lombok.Getter;

/**
 * The Bingo timer manager class. <br/>
 * You can start, stop Bingo timer tasks using this manager.
 *
 * @author APJifengc
 */
public final class BingoTimerManager {
    @Getter
    private static final BingoTimer timer = new BingoTimer(Bingo.getInstance());

    /**
     * Start the timer. <br/>
     * This will not reset the timer, and it will start from where it stops. <br/>
     * For resetting, use {@link #resetTimer()}.
     */
    public static void startTimer() {
        timer.start();
    }

    /**
     * Stop the timer. <br/>
     * Any task that is running will not tick anymore. <br/>
     * Note that {@link BingoTimerTask#isRunning()} will still return whether it is running, it isn't affected
     * by this method.
     */
    public static void stopTimer() {
        timer.stop();
    }

    /**
     * Reset ths timer. <br/>
     * Note that after resetting the timer, the tasks that were stopped will not start again. <br/>
     * You can use {@link #startAllTasks()} to start the tasks again.
     */
    public static void resetTimer() {
        timer.reset();
    }

    /**
     * Start all the tasks.
     */
    public static void startAllTasks() {
        timer.startAllTasks();
    }

    /**
     * Start a task that is stopped.
     *
     * @param id The task id.
     */
    public static void startTask(String id) {
        timer.getTask(id).setRunning(true);
    }

    /**
     * Stop a task.
     *
     * @param id The task id.
     */
    public static void stopTask(String id) {
        timer.getTask(id).setRunning(false);
    }

    /**
     * Set the task's time.
     *
     * @param id         The task id.
     * @param time       The time to set.
     * @param isRelative Whether it is relative or absolute.
     */
    public static void setTime(String id, long time, boolean isRelative) {
        if (isRelative) {
            timer.setTime(id, timer.timestamp + time);
        } else {
            timer.setTime(id, time);
        }
    }

    /**
     * Start the task. <br/>
     * The time is absolute time starts from 0. <br/>
     * For example, if you start the task at time 5, and the time for the task is 6, then the task will be invoked
     * 1 tick later.
     *
     * @see #startFromNow(BingoTimerTask)
     */
    public static void start(BingoTimerTask task) {
        task.setRunning(true);
        timer.registerTask(task);
    }

    /**
     * Start the task. <br/>
     * The time is relative time starts from when you created it. <br/>
     * For example, if you start the task at time 5, and the time for the task is 6, then the task will be invoked
     * 6 tick later, at time 11.
     *
     * @see #start(BingoTimerTask)
     */
    public static void startFromNow(BingoTimerTask task) {
        task.setRunning(true);
        timer.registerTaskRelatively(task);
    }

    /**
     * Add the task to the timer. <br/>
     * This will only add it, but not starting it.
     */
    public static void add(BingoTimerTask task) {
        task.setRunning(false);
        timer.registerTask(task);
    }

    /**
     * Clear every task that is registered in the timer.
     */
    public static void clearTasks() {
        timer.clearTasks();
    }

    /**
     * Get the task by its id;
     *
     * @param id The task id.
     * @return The task.
     */
    public static BingoTimerTask getTask(String id) {
        return timer.getTask(id);
    }
}
