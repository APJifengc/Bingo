package github.apjifengc.bingo;

import github.apjifengc.bingo.command.OnCommand;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.listener.ClickInventoryListener;
import github.apjifengc.bingo.util.Message;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.plugin.java.JavaPlugin;

public class Bingo extends JavaPlugin {

	@Setter
	@Getter
	BingoGame currentGame;

	@Override
	public void onEnable() {
		getLogger().info("Bingooooooooo!");
		this.getCommand("bingo").setExecutor(new OnCommand(this));
		saveResource("tasks.yml", false);
		saveResource("messages.yml", false);
		saveResource("config.yml", false);
		ConfigLoad.reloadConfig(this);
		getServer().getPluginManager().registerEvents(new ClickInventoryListener(this),this);
	}

	@Override
	public void onDisable() {
		getLogger().info("SAMPLE TEXT");
	}

	public boolean hasBingoGame() {
		return currentGame != null;
	}

}