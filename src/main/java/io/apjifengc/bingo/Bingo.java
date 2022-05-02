package io.apjifengc.bingo;

import io.apjifengc.bingo.api.exception.BadTaskException;
import io.apjifengc.bingo.command.CommandMain;
import io.apjifengc.bingo.api.game.BingoGame;
import io.apjifengc.bingo.api.game.BingoPlayer;
import io.apjifengc.bingo.listener.InventoryListener;
import io.apjifengc.bingo.listener.OtherListener;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Message;
import io.apjifengc.bingo.world.SchematicManager;
import io.apjifengc.bingo.world.WorldManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
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

    @Getter @Setter @Nullable BingoGame currentGame;

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
        loadPlugin();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        if (Config.getMain().getBoolean("server.auto-start-end", false)) {
            try {
                startGame();
            } catch (BadTaskException e) {
                e.printStackTrace();
                Bukkit.shutdown();
            }
        }
    }

    public void loadPlugin() {
        Config.load();
        saveResource("lobby.schem");
    }

    @Override
    public void onDisable() {
        getLogger().info("I'm out.");
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        if (hasBingoGame()) {
            currentGame.forceStop();
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

    public void startGame() throws BadTaskException {
        String worldName = Config.getMain().getString("room.world-name");
        if (!(Config.getMain().getBoolean("debug") && WorldManager.exists(worldName))) {
            WorldManager.regenerateWorld(worldName);
        }
        var world = Bukkit.getWorld(worldName);
        world.setPVP(false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        try {
            SchematicManager.buildSchematic(new File(getDataFolder(), "lobby.schem"),
                    new Location(Bukkit.getWorld(worldName), 0, 200, 0));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        BingoGame game = new BingoGame();
        game.generateTasks();
        setCurrentGame(game);
        Bukkit.broadcastMessage(
                Message.get("title-text") + Message.get("commands.start.success"));
    }
}
