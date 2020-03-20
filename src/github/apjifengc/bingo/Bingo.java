package github.apjifengc.bingo;

import github.apjifengc.bingo.command.OnCommand;
import github.apjifengc.bingo.game.BingoGame;
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
	}

	@Override
	public void onDisable() {
		getLogger().info("SAMPLE TEXT");
	}

	public boolean hasBingoGame() {
		return currentGame != null;
	}

}