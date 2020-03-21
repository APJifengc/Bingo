package github.apjifengc.bingo;

import github.apjifengc.bingo.util.Message;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigLoad {

    @Getter
    static YamlConfiguration messageConfig = new YamlConfiguration();

    @Getter
    static YamlConfiguration tasksConfig = new YamlConfiguration();

    @Getter
    static YamlConfiguration mainConfig = new YamlConfiguration();



    public static void reloadConfig(Bingo plugin) {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        try {
            messageConfig.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        YamlConfiguration defConfig = YamlConfiguration
                .loadConfiguration(new InputStreamReader(plugin.getResource("messages.yml")));
        messageConfig.setDefaults(defConfig);
        Message.setConfig(messageConfig);

        file = new File(plugin.getDataFolder(), "tasks.yml");
        try {
            tasksConfig.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        defConfig = YamlConfiguration
                .loadConfiguration(new InputStreamReader(plugin.getResource("tasks.yml")));
        tasksConfig.setDefaults(defConfig);

        file = new File(plugin.getDataFolder(), "config.yml");
        try {
            mainConfig.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        defConfig = YamlConfiguration
                .loadConfiguration(new InputStreamReader(plugin.getResource("config.yml")));
        mainConfig.setDefaults(defConfig);
    }
}
