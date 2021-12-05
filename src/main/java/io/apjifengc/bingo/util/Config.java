package io.apjifengc.bingo.util;

import io.apjifengc.bingo.Bingo;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Bingo configuration constants.
 *
 * @author Milkory
 */
public class Config {

    private static final Bingo plugin = Bingo.getInstance();

    @Getter private static YamlConfiguration task;
    @Getter private static YamlConfiguration main;

    public static void load() {
        task = loadConfig("tasks.yml");
        main = loadConfig("config.yml");
        Message.load();
    }

    /** Note: Only compatible with the Bingo plugin. */
    public static YamlConfiguration loadConfig(String path) {
        var file = plugin.saveResource(path);
        var config = YamlConfiguration.loadConfiguration(file);
        var defaults = YamlConfiguration.loadConfiguration(plugin.getResourceReader(path));
        config.setDefaults(defaults);
        return config;
    }

}
