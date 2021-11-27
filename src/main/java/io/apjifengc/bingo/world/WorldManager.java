package io.apjifengc.bingo.world;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.util.Config;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;

/**
 * 多世界管理器
 *
 * @author APJifengc
 */
public class WorldManager {
    public static void regenerateWorld(String name) {
        if (Bukkit.getWorld(name) != null) {
            try {
                Bukkit.unloadWorld(name, false);
                FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer(), name));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        World world = Bukkit.createWorld(new WorldCreator(name));
        int chunks = Config.getMain().getInt("game.pre-generate-range", 20);
        if (chunks <= 1) return;
        int count = 0;
        for (int i = -chunks; i <= chunks; i++) {
            for (int j = -chunks; j <= chunks; j++) {
                count++;
                Bingo.getInstance().getLogger().info(String.format("Generating world: %.2f%%", count / ((double) (2 * chunks + 1) * (2 * chunks + 1)) * 100.0));
                world.loadChunk(i, j);
            }
        }
    }
}
