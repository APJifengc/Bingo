package github.apjifengc.bingo;

import github.apjifengc.bingo.commands.CommandMain;
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
		this.getCommand("bingo").setExecutor(new CommandMain(this));
	}
	
	@Override
	public void onDisable() {
		getLogger().info("SAMPLE TEXT");
	}

}