package github.apjifengc.bingo;

import github.apjifengc.bingo.command.OnCommand;
import github.apjifengc.bingo.game.BingoGame;
import github.apjifengc.bingo.listener.InventoryListener;
import github.apjifengc.bingo.listener.OtherListener;
import github.apjifengc.bingo.listener.TaskListener;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Bingo extends JavaPlugin {

	@Setter
	@Getter
	BingoGame currentGame;

	List<Listener> listenerList = new ArrayList<Listener>();

	@Override
	public void onEnable() {
		getLogger().info("Bingooooooooo!");
		this.getCommand("bingo").setExecutor(new OnCommand(this));
		saveResource("tasks.yml", false);
		saveResource("messages.yml", false);
		saveDefaultConfig();
		Configs.reloadConfig(this);
		listenerList.add(new InventoryListener(this));
		listenerList.add(new OtherListener(this));
		listenerList.add(new TaskListener(this));
	}

	@Override
	public void onDisable() {
		getLogger().info("SAMPLE TEXT");
	}

	public boolean hasBingoGame() {
		return currentGame != null;
	}

}