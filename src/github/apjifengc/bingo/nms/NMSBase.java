package github.apjifengc.bingo.nms;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMSBase {

	public String getVersion();

	public void respawnPlayer(Player player);

	public String getItemName(ItemStack is);

	public String getEntityName(Entity entity);

}