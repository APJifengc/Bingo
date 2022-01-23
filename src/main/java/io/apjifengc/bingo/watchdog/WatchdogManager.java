package io.apjifengc.bingo.watchdog;

import io.apjifengc.bingo.world.WorldManager;
import lombok.Getter;
import org.spigotmc.WatchdogThread;

import java.lang.reflect.Field;

/**
 * Manage the watchdog thread in the server.
 *
 * @see WorldManager
 *
 * @author APJifengc
 */
public class WatchdogManager {
    @Getter
    private static WatchdogManager instance = null;

    static {
        try {
            instance = new WatchdogManager();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private final Field instanceField;

    private final Field lastTickField;

    private WatchdogManager() throws NoSuchFieldException {
        Field instanceField = WatchdogThread.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        this.instanceField = instanceField;
        Field lastTickField = WatchdogThread.class.getDeclaredField("lastTick");
        lastTickField.setAccessible(true);
        this.lastTickField = lastTickField;
    }

    public void tick() {
        try {
            WatchdogThread instance = (WatchdogThread)this.instanceField.get(null);
            if ((Long) this.lastTickField.get(instance) != 0L)
                WatchdogThread.tick();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
