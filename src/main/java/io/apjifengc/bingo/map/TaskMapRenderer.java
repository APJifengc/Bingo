package io.apjifengc.bingo.map;

import io.apjifengc.bingo.api.game.BingoGame;
import io.apjifengc.bingo.api.game.BingoPlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Random;

public class TaskMapRenderer extends MapRenderer {
    private final BingoGame game;
    private final Random random;

    public TaskMapRenderer(BingoGame game) {
        super(true);
        this.game = game;
        random = new Random();
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        for (int i = 1; i <= 128; i++) {
            for (int j = 1; j <= 128; j++) {
                canvas.setPixel(i, j, MapPalette.TRANSPARENT);
            }
        }
        for (int i = 16; i <= 113; i++) {
            for (int j = 16; j <= 113; j++) {
                canvas.setPixel(i, j, MapPalette.LIGHT_GRAY);
            }
        }
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                byte color = game.getPlayer(player).hasFinished(5 * j + i - 6) ? MapPalette.LIGHT_GREEN : MapPalette.RED;
                for (int k = 17 + i * 19 - 18; k <= 16 + i * 19 ; k++) {
                    for (int q = 17 + j * 19 - 18; q <= 16 + j * 19 ; q++) {
                        canvas.setPixel(k, q, color);
                    }
                }
            }
        }
    }
}
