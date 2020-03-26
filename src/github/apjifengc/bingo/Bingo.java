package github.apjifengc.bingo;

import github.apjifengc.bingo.command.OnCommand;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.listener.InventoryListener;
import github.apjifengc.bingo.listener.OtherListener;
import github.apjifengc.bingo.listener.TaskListener;
import github.apjifengc.bingo.nms.NMSBase;
import github.apjifengc.bingo.util.Configs;
import lombok.Getter;
import lombok.Setter;
import com.onarandombox.MultiverseCore.*;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Bingo extends JavaPlugin {

	@Setter
	@Getter
	BingoGame currentGame;

	@Getter
	MultiverseCore multiverseCore;

	public static NMSBase NMS;

	@Override
	public void onEnable() {
		if (!loadNMS()) {
			getLogger().warning("Can't use Bingo plugin in this server.");
			getPluginLoader().disablePlugin(this);
		} else {
			multiverseCore = ((MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core"));
			getLogger().info("Bingooooooooo!");
			new OnCommand(this);
			new InventoryListener(this);
			new OtherListener(this);
			new TaskListener(this);
			loadPlugin();
		}
	}

	public void loadPlugin() {
		saveResource("tasks.yml", false);
		saveResource("messages.yml", false);
		saveDefaultConfig();
		Configs.reloadConfig(this);
	}

	private boolean loadNMS() {
		try {
			NMS = (NMSBase) Class
					.forName("github.apjifengc.bingo.nms." + getServer().getClass().getPackage().getName().substring(23))
					.newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void onDisable() {
		getLogger().info("SAMPLE TEXT");
		if (hasBingoGame()) {
			currentGame.stop();
		}
	}

	public boolean hasBingoGame() {
		return currentGame != null;
	}

}