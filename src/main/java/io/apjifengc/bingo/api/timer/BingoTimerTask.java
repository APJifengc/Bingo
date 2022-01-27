package io.apjifengc.bingo.api.timer;

import lombok.Getter;
import lombok.Setter;

/**
 * This represents a timed task in Bingo game.
 *
 * @author APJifengc
 */
public abstract class BingoTimerTask {
    @Getter
    private long interval;
    @Getter
    private long time;
    @Getter
    private String id;
    @Getter
    @Setter
    private boolean running;

    long timestamp;

    long nextTick;

    /**
     * The method will be invoked when the timer hits the time.
     */
    public abstract void run();

    /**
     * THe method will be invoked every few ticks. <br/>
     * You can set the interval through the method {@link #tickInterval(long)}. <br/>
     * Note: If the run and tick is in the same tick, it will invoke tick first, then run.
     *
     * @see #tickInterval(long)
     */
    public abstract void tick();

    /**
     * Set the interval between every time the {@link #tick()} method is invoked. <br/>
     *
     * @param interval The interval. (ticks)
     */
    public BingoTimerTask tickInterval(long interval) {
        this.interval = interval;
        return this;
    }

    /**
     * Set when the task will run. <br/>
     * The time could be either absolute or relative, depend on whether you choose {@link #start()} or
     * {@link #startFromNow()}.
     *
     * @param time The time. (ticks)
     */
    public BingoTimerTask time(long time) {
        this.time = time;
        return this;
    }

    /**
     * Set ths task's id. <br/>
     * You could use {@link BingoTimerManager} to manage the task using this id.
     *
     * @param id The task id.
     */
    public BingoTimerTask id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Start the task. <br/>
     * The time is absolute time starts from 0. <br/>
     * For example, if you start the task at time 5, and the time for the task is 6, then the task will be invoked
     * 1 tick later.
     *
     * @see #startFromNow()
     */
    public void start() {
        BingoTimerManager.start(this);
    }

    /**
     * Start the task. <br/>
     * The time is relative time starts from when you created it. <br/>
     * For example, if you start the task at time 5, and the time for the task is 6, then the task will be invoked
     * 6 tick later, at time 11.
     *
     * @see #start()
     */
    public void startFromNow() {
        BingoTimerManager.startFromNow(this);
    }

    /**
     * Add the task to the timer. <br/>
     * This will only add it, but not starting it.
     */
    public void add() {
        BingoTimerManager.add(this);
    }

    /**
     * Get how long will the task run.
     *
     * @return The relative time.
     */
    public long getRelativeTime() {
        return timestamp - BingoTimerManager.getTimer().timestamp;
    }
}
