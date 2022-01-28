package io.apjifengc.bingo.watchdog;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.api.game.BingoGame;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class AntiWatchdogPopulator extends BlockPopulator {
    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk source) {
        if (Bingo.getInstance().getCurrentGame().getState() == BingoGame.State.LOADING) {
            WatchdogManager.getInstance().tick();
        }
    }
}
