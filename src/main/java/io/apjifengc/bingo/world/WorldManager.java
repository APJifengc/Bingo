package io.apjifengc.bingo.world;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Files;
import io.apjifengc.bingo.watchdog.WatchdogManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;

/**
 * 多世界管理器
 *
 * @author APJifengc
 */
public class WorldManager {

    public static boolean exists(String name) {
        return new File(Bukkit.getWorldContainer(), name).exists();
    }

    public static void regenerateWorld(World world) {
        regenerateWorld(world.getName());
    }

    public static void regenerateWorld(String name) {
        if (exists(name)) {
            Bukkit.unloadWorld(name, false);
            Files.deleteDirectory(new File(Bukkit.getWorldContainer(), name));
        }
        var thread = new Thread("anti-watchdog-thread") {
            @SuppressWarnings("BusyWait")
            @Override
            public void run() {
                while (!interrupted()) {
                    WatchdogManager.getInstance().tick();
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
        thread.start();
        World world = Bukkit.createWorld(new WorldCreator(name));
        int chunks = Config.getMain().getInt("game.pre-generate-range", 20);
        if (chunks <= 1) return;
        int count = 0;
        for (int i = -chunks; i <= chunks; i++) {
            for (int j = -chunks; j <= chunks; j++) {
                if (count % 10 == 0)
                    Bingo.getInstance().getLogger().info(String.format(
                            "Generating world: %.2f%%", count / ((double) (2 * chunks + 1) * (2 * chunks + 1)) * 100.0)
                    );
                count++;
                world.loadChunk(i, j);
            }
        }
        thread.interrupt();
    }

}
