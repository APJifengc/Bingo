package github.apjifengc.bingo;

import github.apjifengc.bingo.util.Msg;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Configs {

    @Getter
    static YamlConfiguration msgCfg = new YamlConfiguration();

    @Getter
    static YamlConfiguration taskCfg = new YamlConfiguration();

    @Getter
    static YamlConfiguration mainCfg = new YamlConfiguration();



    public static void reloadConfig(Bingo plugin) {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        try {
            msgCfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        YamlConfiguration defConfig = YamlConfiguration
                .loadConfiguration(new InputStreamReader(plugin.getResource("messages.yml")));
        msgCfg.setDefaults(defConfig);

        file = new File(plugin.getDataFolder(), "tasks.yml");
        try {
            taskCfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        defConfig = YamlConfiguration
                .loadConfiguration(new InputStreamReader(plugin.getResource("tasks.yml")));
        taskCfg.setDefaults(defConfig);

        file = new File(plugin.getDataFolder(), "config.yml");
        try {
            mainCfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        defConfig = YamlConfiguration
                .loadConfiguration(new InputStreamReader(plugin.getResource("config.yml")));
        mainCfg.setDefaults(defConfig);
    }
}
