package io.apjifengc.bingo.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.apjifengc.bingo.Bingo;
import org.bukkit.entity.Player;

/**
 * This class provides some features with bungeecord.
 *
 * @author APJifengc
 */
public class BungeecordUtil {
    /**
     * Connect a player to a bungeecord server.
     *
     * @param player The layer to connect.
     * @param server The server to connect.
     */
    public static void sendPlayer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(Bingo.getInstance(), "BungeeCord", out.toByteArray());
    }
}
