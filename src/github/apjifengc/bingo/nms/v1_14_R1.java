package github.apjifengc.bingo.nms;

import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_14_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_14_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayInClientCommand.EnumClientCommand;

public class v1_14_R1 implements NMSBase {

	@Override
	public String getVersion() {
		return "v1_14_R1";
	}

	@Override
	public void respawnPlayer(Player player) {
		if (player.isDead()) {
			((CraftPlayer) player).getHandle().playerConnection
					.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
		}
	}

	@Override
	public String getItemName(ItemStack is) {
		return ChatSerializer.a("{\"translate\":\""
				+ org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack.asNMSCopy(is).getItem().getName() + "\"}")
				.getText();
	}

}
