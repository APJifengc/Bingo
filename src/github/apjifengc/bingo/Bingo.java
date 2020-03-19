package github.apjifengc.bingo;

import github.apjifengc.bingo.commands.CommandMain;
import org.bukkit.plugin.java.JavaPlugin;

public class Bingo extends JavaPlugin {

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