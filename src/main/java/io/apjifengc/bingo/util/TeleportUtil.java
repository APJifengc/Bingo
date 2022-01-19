package io.apjifengc.bingo.util;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Random;

/**
 * 关于传送的杂项类
 *
 * @author APJifengc
 */
public class TeleportUtil {
    /** Send player to a world safely. */
    public static void safeTeleport(Player player, World world, int x, int z) {
        player.teleport(world.getHighestBlockAt(x, z).getLocation().clone().add(0, 1, 0));
    }

    /** Randomly teleport the player in the world. */
    public static void randomTeleport(Player player, World world, int x, int z, int range) {
        Random random = new Random();
        range -= 10; // Prevent from teleporting into border.
        safeTeleport(player, world, x + range / 2 - random.nextInt(range) - 1, z + range / 2 - random.nextInt(range) - 1);
    }
}
