package io.apjifengc.bingo.map;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import io.apjifengc.bingo.api.game.BingoGame;

@SuppressWarnings("deprecation")
public class TaskMapRenderer extends MapRenderer {
    private final BingoGame game;
    private static final Set<Player> dirty = new HashSet<>();

    public static void makeDirty(Player player) {
        dirty.add(player);
    }

    public static void makeClean(Player player) {
        dirty.remove(player);
    }

    public TaskMapRenderer(BingoGame game) {
        super(true);
        this.game = game;
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        if (dirty.contains(player)) {
            for (int i = 0; i <= 127; i++) {
                for (int j = 0; j <= 127; j++) {
                    canvas.setPixel(i, j, MapPalette.TRANSPARENT);
                }
            }
            for (int i = 16; i <= 127 - 16; i++) {
                for (int j = 16; j <= 127 - 16; j++) {
                    canvas.setPixel(i, j, MapPalette.matchColor(Color.BLACK));
                }
            }
            for (int i = 1; i <= 5; i++) {
                for (int j = 1; j <= 5; j++) {
                    int x = 1 + 16 + (i - 1) * 19;
                    int y = 1 + 16 + (j - 1) * 19;
                    try {
                        Image image = game.getBoard().get(i + j * 5 - 6).getIcon(game.getPlayer(player).hasFinished(i + j * 5 - 6));
                        canvas.drawImage(x, y, image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            dirty.remove(player);
        }
    }
}
