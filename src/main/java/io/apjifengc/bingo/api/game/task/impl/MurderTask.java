package io.apjifengc.bingo.api.game.task.impl;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.api.event.game.BingoGameStateChangeEvent;
import io.apjifengc.bingo.api.game.BingoGame;
import io.apjifengc.bingo.api.game.task.BingoTask;
import io.apjifengc.bingo.util.Message;
import io.apjifengc.bingo.util.TaskUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

@SuppressWarnings("unused")
public class MurderTask extends BingoTask {

    private static final Random rand = new Random();

    @Getter private UUID targetUniqueId;
    @Getter private BaseComponent[] shownName;
    private ItemStack shownItem;

    @Override public @NotNull ItemStack getShownItem() {
        var player = Bukkit.getPlayer(targetUniqueId);
        if (player != null) {
            var meta = (CompassMeta) shownItem.getItemMeta();
            meta.setLodestone(player.getLocation());
            shownItem.setItemMeta(meta);
        }
        return shownItem;
    }

    @Override public @NotNull Listener getTaskListener() {
        return new Listener() {
            @EventHandler
            public void handle(PlayerDeathEvent event) {
                var player = event.getEntity();
                var killer = event.getEntity().getKiller();
                if (killer != null && player.getUniqueId() == targetUniqueId) {
                    finishTask(killer);
                }
            }

            @EventHandler
            public void handle(BingoGameStateChangeEvent event) {
                if (event.getAfter() == BingoGame.State.RUNNING) {
                    var players = event.getGame().getPlayers();
                    var target = players.get(rand.nextInt(players.size())).getPlayer();
                    targetUniqueId = target.getUniqueId();
                    shownName = Message.getComponents("task.murder-task.title", target.getName());
                    var item = new ItemStack(Material.COMPASS);
                    item = TaskUtil.setRawDisplay(item, ComponentSerializer.toString(shownName), Message.getWrapRaw("task.murder-task.desc", target.getName()));
                    shownItem = item;
                }
            }
        };
    }

    @Override public @NotNull Image getIcon(boolean isFinished) throws IOException {
        return ImageIO.read(Bingo.getInstance().getResource("icons/other/" + (isFinished ? "green" : "red") + "/murder.png"));
    }

    public static MurderTask newInstance(String[] args) {
        return new MurderTask();
    }

}
