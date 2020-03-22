package github.apjifengc.bingo.listener;

import github.apjifengc.bingo.Bingo;
import github.apjifengc.bingo.game.BingoPlayer;
import github.apjifengc.bingo.util.Msg;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OtherListener implements Listener {
    private final Bingo plugin;
    public OtherListener(Bingo plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(plugin.getCurrentGame().getPlayer(event.getPlayer())!=null) {
            //TODO 完善一下 游戏未开始时移除玩家，开始后不移除
            plugin.getCurrentGame().removePlayer(event.getPlayer());
            event.setQuitMessage(null);
        }
    }
}
