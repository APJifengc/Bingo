package io.apjifengc.bingo.world;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Files;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

/**
 * 多世界管理器
 *
 * @author APJifengc
 */
public class WorldManager {

    public static void regenerateWorld(World world) {
        regenerateWorld(world.getName());
    }

    public static void regenerateWorld(String name) {
        if (Bukkit.getWorld(name) != null) {
            Bukkit.unloadWorld(name, false);
            Files.deleteDirectory(new File(Bukkit.getWorldContainer(), name));
        }
        World world = Bukkit.createWorld(new WorldCreator(name));
        int chunks = Config.getMain().getInt("game.pre-generate-range", 20);
        if (chunks <= 1) return;
        MutableInt count = new MutableInt(0);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bingo.getInstance().getLogger().info(String.format("Generating world: %.2f%%", count.intValue() / ((double) (2 * chunks + 1) * (2 * chunks + 1)) * 100.0));
                if (count.intValue() == (2 * chunks + 1) * (2 * chunks + 1)) {
                    cancel();
                }
            }
        }.runTaskTimer(Bingo.getInstance(), 0, 20);
        for (int i = -chunks; i <= chunks; i++) {
            for (int j = -chunks; j <= chunks; j++) {
                count.add(1);
                world.loadChunk(i, j);
            }
        }
    }

}
