package github.apjifengc.bingo;

import github.apjifengc.bingo.command.OnCommand;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.game.BingoPlayer;
import github.apjifengc.bingo.listener.InventoryListener;
import github.apjifengc.bingo.listener.OtherListener;
import github.apjifengc.bingo.listener.TaskListener;
import github.apjifengc.bingo.util.Configs;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Bingo extends JavaPlugin {

	@Setter
	@Getter
	BingoGame currentGame;

	@Override
	public void onEnable() {
		getLogger().info("Bingooooooooo!");
		new OnCommand(this);
		new InventoryListener(this);
		new OtherListener(this);
		new TaskListener(this);
		loadPlugin();
	}
	
	public void loadPlugin(){
		saveResource("tasks.yml", false);
		saveResource("messages.yml", false);
		saveDefaultConfig();
		Configs.reloadConfig(this);
	}
	
	@Override
	public void onDisable() {
		getLogger().info("SAMPLE TEXT");
		if(hasBingoGame()) {
			for (BingoPlayer bp : currentGame.getPlayers()) {
				bp.clearScoreboard();
			}
		}
		
	}

	public boolean hasBingoGame() {
		return currentGame != null;
	}

}