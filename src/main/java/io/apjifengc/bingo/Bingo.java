package io.apjifengc.bingo;

import io.apjifengc.bingo.command.CommandMain;
import io.apjifengc.bingo.game.BingoGame;
import io.apjifengc.bingo.game.BingoPlayer;
import io.apjifengc.bingo.listener.InventoryListener;
import io.apjifengc.bingo.listener.OtherListener;
import io.apjifengc.bingo.listener.TaskListener;
import io.apjifengc.bingo.util.Config;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * The main class of Bingo.
 *
 * @author Milkory
 */
public class Bingo extends JavaPlugin {

    @Getter
    private static Bingo instance = null;

    public Bingo() {
        instance = this;
    }

    @Getter @Setter BingoGame currentGame;

    @Getter CommandMain commandMain;

    @Override public void onLoad() {
        if (instance != this) instance = this;
    }

    @Override
    public void onEnable() {
        getLogger().info("Bingooooooooo!");
        commandMain = new CommandMain();
        new InventoryListener(this);
        new OtherListener(this);
        new TaskListener(this);
        loadPlugin();
    }

    public void loadPlugin() {
        Config.load();
        saveResource("lobby.schem");
    }

    @Override
    public void onDisable() {
        getLogger().info("I'm out.");
        if (hasBingoGame()) {
            currentGame.stop();
        }
    }

    public boolean hasBingoGame() {
        return currentGame != null;
    }

    @Nullable
    public BingoPlayer getPlayer(Object player) {
        if (player == null) return null;
        if (player instanceof Player) {
            if (hasBingoGame()) {
                if (currentGame.getState() == BingoGame.State.RUNNING) {
                    return currentGame.getPlayer((Player) player);
                }
            }
        }
        return null;
    }

    public InputStreamReader getResourceReader(String file) {
        return new InputStreamReader(Objects.requireNonNull(this.getResource(file)));
    }

    public File saveResource(String path) {
        var file = new File(getDataFolder(), path);
        if (!file.exists()) { // Create the config file if it isn't exist.
            super.saveResource(path, false);
        }
        return file;
    }

}